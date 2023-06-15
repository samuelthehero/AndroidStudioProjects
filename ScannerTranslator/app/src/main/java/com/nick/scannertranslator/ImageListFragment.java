package com.nick.scannertranslator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nick.scannertranslator.adapters.AdapterImagen;
import com.nick.scannertranslator.models.ModelImage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageListFragment extends Fragment {
    private static final String TAG = "IMAGE_LIST_TAG";

    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private String[] permisosCamara;
    private String[] permisosTarjeta;

    private Uri imageUri = null;
    private FloatingActionButton aniadirImagen;
    private RecyclerView imagenList;
    private ArrayList<ModelImage> imagenesArrayList;
    private AdapterImagen adapterImagen;

    private ProgressDialog progressDialog;
    private Context mContext;

    public ImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permisosCamara = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        permisosTarjeta = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        aniadirImagen = view.findViewById(R.id.AniadirImagen);
        imagenList = view.findViewById(R.id.imagenList);

        // Se inicia el proceso de conversión.
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        loadImages();

        aniadirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_imagenes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.images_item_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Eliminar imagenes")
                    .setMessage("¿Seguro que quieres eliminar todas/las imágenes seleccionadas?")
                    .setPositiveButton("Eliminar todo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Eliminar todas las imágenes de la lista
                            eliminarImagenes(true);
                        }
                    })
                    .setNeutralButton("Eliminar seleccionada", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Eliminar sólo las imágenes selecionadas
                            eliminarImagenes(false);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Cancelar la acción de eliminar las imágenes, cuadro de diálogo
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } else if (itemId == R.id.lista_imagenes_pdf) {
            // Se muestra una lista con las imágenes seleccionadas para convertir a PDF
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Convertir a PDF").setMessage("Comvertir todas/imágenes seleccionadas a PDF")
                    .setPositiveButton("Convertir a PDF", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Convertir todas las imágenes seleccionadas
                            comvertirAPDF(true);
                        }
                    })
                    .setNeutralButton("Convertir seleccionada", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Convertir sólo las imágenes seleccionadas
                            comvertirAPDF(false);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Cancelar la acción
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void comvertirAPDF(boolean convertirTodo){
        Log.d(TAG, "comvertirAPDF: convertirTodo: "+convertirTodo);

        progressDialog.setMessage("Convirtiendo a PDF...");
        progressDialog.show();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "run: Se inicia la tarea");

                ArrayList<ModelImage> listaImagenesPdf = new ArrayList<>();
                if(convertirTodo){
                    listaImagenesPdf = imagenesArrayList;
                }else{
                    // Sólo las imágenes seleccionadas
                    for(int i=0; i < imagenesArrayList.size(); i++){
                        if(imagenesArrayList.get(i).isChecked()){
                            listaImagenesPdf.add(imagenesArrayList.get(i));
                        }
                    }
                }

                Log.d(TAG, "run: Tamaño de la lista de imágenes a PDF:"+listaImagenesPdf.size());
                
                try{
                    File root = new File(mContext.getExternalFilesDir(null), Constantes.IMAGES_FOLDER);
                    root.mkdirs();
                    // Nombre con extensión de archivo
                    long timestamp = System.currentTimeMillis();
                    String nombreArchivo = "PDF_"+timestamp+".pdf";

                    Log.d(TAG, "run: nombreArchivo: "+nombreArchivo);

                    File file = new File(root, nombreArchivo);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PdfDocument pdfDocument = new PdfDocument();

                    for(int i=0; i < listaImagenesPdf.size(); i++){

                        Uri imageToPDFUri = listaImagenesPdf.get(i).getImageUri();
                        
                        try{

                            Bitmap bitmap;
                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(), imageToPDFUri));
                            }else{

                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageToPDFUri);
                            }

                            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);

                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i+1).create();

                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);



                            Paint paint = new Paint();
                            paint.setColor(Color.WHITE);

                            Canvas canvas = page.getCanvas();
                            canvas.drawPaint(paint);
                            canvas.drawBitmap(bitmap, 0f, 0f, null);

                            pdfDocument.finishPage(page);
                            bitmap.recycle();

                        }catch(Exception e){

                            Log.d(TAG, "run: ",e);
                        }
                    }

                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();

                }catch(Exception e){
//                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.d(TAG, "run: ",e);
                }

                //
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: Convertido...");

                        progressDialog.dismiss();
                        Toast.makeText(mContext, "Convertido...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void eliminarImagenes(boolean eliminarTodo){
        ArrayList<ModelImage> eliminarListaImagenes = new ArrayList<>();
        if(eliminarTodo){
            eliminarListaImagenes = imagenesArrayList;
        }
        else{
            // Elimina sólo las imágenes seleccionadas, las añade a la lista de imágenes eliminadas
            for(int i = 0; i<imagenesArrayList.size(); i++){

                // Comprueba si la imagen ha sido seleccionada o no
                if(imagenesArrayList.get(i).isChecked()){
                    // Si ha sido seleccionada, se añade a la lista de imágenes eliminadas
                    eliminarListaImagenes.add(imagenesArrayList.get(i));
                }
            }
        }

        for(int i = 0; i<eliminarListaImagenes.size(); i++){

            try {
                String rutaImagenAEliminar = eliminarListaImagenes.get(i).getImageUri().getPath();

                File file = new File(rutaImagenAEliminar);
                if(file.exists()){
                    // Elimina el archivo y devuelve el resultado como verdadero o falso
                    boolean esEliminada = file.delete();
                    // Se muestra en el Log
                    Log.d(TAG, "eliminarImagen: esEliminada"+esEliminada);
                }
            }catch(Exception e){
                Log.d(TAG, "eliminarImagenes: ", e);
            }
        }

        Toast.makeText(mContext, "Imagen/es eliminada/as", Toast.LENGTH_SHORT).show();

//        loadImages();
    }

    private void loadImages() {
        Log.d(TAG, "loadImages: ");

        imagenesArrayList = new ArrayList<>();
        adapterImagen = new AdapterImagen(mContext, imagenesArrayList);
        // set adapter
        imagenList.setAdapter(adapterImagen);

        File folder = new File(mContext.getExternalFilesDir(null), Constantes.IMAGES_FOLDER);

        if(folder.exists()){
            // Si la carpeta existe, intenta cargar los archivos
            Log.d(TAG, "loadImages: Si la carpeta existe, intenta cargar los archivos");
            File[] files = folder.listFiles();

            if(files != null){
                Log.d(TAG, "loadImages: La carpeta existe y tiene imágenes");

                for(File file : files){
                    Log.d(TAG, "loadImages: "+file.getName());

                    Uri imageUri = Uri.fromFile(file);
                    ModelImage modelImage = new ModelImage(imageUri, false);

                    imagenesArrayList.add(modelImage);
                    adapterImagen.notifyItemInserted(imagenesArrayList.size());
                }
            }else{
                Log.d(TAG, "loadImages: La carpeta existe pero esta vacía");
            }
        }else{
            Log.d(TAG, "loadImages: La carpeta no existe");
        }
    }

    private void guardarImagenDirectorio(Uri imageUriToBeSaved){
        Log.d(TAG, "guardarImagenDirectorio: ");
        try {
            Bitmap bitmap;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(), imageUriToBeSaved));
            }else{

                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUriToBeSaved);
            }

            File directory = new File(mContext.getExternalFilesDir(null), Constantes.IMAGES_FOLDER);
            directory.mkdirs();

            long timestamp = System.currentTimeMillis();
            String fileName = timestamp + ".jpeg";

            File file = new File(mContext.getExternalFilesDir(null), ""+ Constantes.IMAGES_FOLDER +"/"+fileName);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.d(TAG, "guardarImagenDirectorio: Imagen guardada");
                Toast.makeText(mContext, "Imagen guardada", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.d(TAG, "guardarImagenDirectorio: Fallo al guardar la imagen en ", e);
                Log.d(TAG, "guardarImagenDirectorio: Fallo al guardar la imagen en "+e.getMessage());
                Toast.makeText(mContext, "Fallo al guardar la imagen en "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.d(TAG, "guardarImagenDirectorio: Fallo al preparar la imagen en ", e);
            Log.d(TAG, "guardarImagenDirectorio: Fallo al preparar la imagen en "+e.getMessage());
            Toast.makeText(mContext, "Fallo al preparar la imagen en "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputDialog(){
        Log.d(TAG, "showInputDialog: ");
        PopupMenu popupMenu = new PopupMenu(mContext, aniadirImagen);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CÁMARA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALERÍA");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if(itemId ==1){
                    // SI LA CÁMARA ES CLICADA, COMPRUEBA ANTES SI LOS PERMISOS ESTÁN CONCEDIDOS
                    Log.d(TAG, "onMenuItemClick: SI LA CÁMARA ES CLICADA, COMPRUEBA ANTES SI LOS PERMISOS ESTÁN CONCEDIDOS");
                    if(comprobarPermisosCamara()){
                        pickImageCamera();
                    }else{
                        permisosSolicitadosCamara();
                    }
                } else if (itemId == 2) {
                    // SI LA GALERÍA ES CLICADA, COMPRUEBA ANTES SI LOS PERMISOS ESTÁN CONCEDIDOS
                    Log.d(TAG, "onMenuItemClick: SI LA GALERÍA ES CLICADA, COMPRUEBA ANTES SI LOS PERMISOS ESTÁN CONCEDIDOS");
                    if(comprobarPersmisosTarjeta()){
                        pickImageGallery();
                    }else{
                        permisosSolicitadosTarjeta();
                    }
                }
                return true;
            }
        });
    }

    private void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: Obtenida imagen de la galería: "+imageUri);
                        guardarImagenDirectorio(imageUri);

                        ModelImage modelImage = new ModelImage(imageUri, false);
                        imagenesArrayList.add(modelImage);
                        adapterImagen.notifyItemInserted(imagenesArrayList.size());
                    }else{
                        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_TITLE");
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_DESCRIPTION");

        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: Obtenida imagen de la cámara: "+imageUri);
                        guardarImagenDirectorio(imageUri);

                        ModelImage modelImage = new ModelImage(imageUri, false);
                        imagenesArrayList.add(modelImage);
                        adapterImagen.notifyItemInserted(imagenesArrayList.size());
                    }else{
                        Toast.makeText(mContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean comprobarPersmisosTarjeta(){
        Log.d(TAG, "comprobarPersmisosTarjeta: ");

        boolean result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;

        return result;
    }

    private void permisosSolicitadosTarjeta(){
        Log.d(TAG, "permisosSolicitadosTarjeta: ");

        requestPermissions(permisosTarjeta, STORAGE_REQUEST_CODE);
    }

    private boolean comprobarPermisosCamara(){
        Log.d(TAG, "comprobarPermisosCamara: ");

        boolean camaraResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean tarjetaResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        return camaraResult && tarjetaResult;
    }

    private void permisosSolicitadosCamara(){
        Log.d(TAG, "permisosSolicitadosCamara: ");
        requestPermissions(permisosCamara, CAMERA_REQUEST_CODE);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){

                    boolean camaraAceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean tarjetaAceptada = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(camaraAceptada && tarjetaAceptada){
                        // SI LOS PERMISOS DE CÁMARA Y TARJETA ESTÁN CONCEDIDOS, SE PROCEDE A ARRANCAR LA CÁMARA
                        Log.d(TAG, "onRequestPermissionsResult: SI LOS PERMISOS DE CÁMARA Y TARJETA ESTÁN CONCEDIDOS, SE PROCEDE A ARRANCAR LA CÁMARA");
                        pickImageCamera();
                    }else{
                        Log.d(TAG, "onRequestPermissionsResult: Permisos de Cámara y Tarjeta requeridos");
                        Toast.makeText(mContext, "Permisos de Cámara y Tarjeta requeridos", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.d(TAG, "onRequestPermissionsResult: Cancelado por el usuario");
                    Toast.makeText(mContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean tarjetaAceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(tarjetaAceptada){
                        // SI LOS PERMISOS DE TARJETA ESTÁN CONCEDIDOS, SE PROCEDE A ARRANCAR LA GALERÍA
                        Log.d(TAG, "onRequestPermissionsResult: SI LOS PERMISOS DE TARJETA ESTÁN CONCEDIDOS, SE PROCEDE A ARRANCAR LA GALERÍA");
                        pickImageGallery();
                    }else
                        // SI LOS PERMIDOS DE TARJETA ESTÁN DENEGADOS, NO SE PODRÁ ARRANCAR LA GALERÍA
                        Log.d(TAG, "onRequestPermissionsResult: SI LOS PERMIDOS DE TARJETA ESTÁN DENEGADOS, NO SE PODRÁ ARRANCAR LA GALERÍA");
                        Toast.makeText(mContext, "Permisos de tarjeta requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onRequestPermissionsResult: Cancelado por el usuario");
                    Toast.makeText(mContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
}