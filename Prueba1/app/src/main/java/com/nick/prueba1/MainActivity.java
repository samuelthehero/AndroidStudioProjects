package com.nick.prueba1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button ReconocerTexto, TraducirTexto;
    private ImageView imagen;
    private EditText TextoReconocidoET;
    private Uri uri = null;
//    ActivityResultLauncher<String> mGetContent;

    private ProgressDialog progressDialog;

    // Reconocedor de texto
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReconocerTexto = findViewById(R.id.ReconocerTexto);
        imagen = findViewById(R.id.imagen);
        TextoReconocidoET = findViewById(R.id.TextoReconocidoET);
        TraducirTexto = findViewById(R.id.TraducirTexto);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false); // Esto evita que el progress dialog se cierre cuando el usuario presione fuera del progressdialog

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Evento al boton
        ReconocerTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobamos que exista una imagen seteada dentro del ListView
                if(uri == null){
                    Toast.makeText(MainActivity.this, "Por favor, seleccione una imagen", Toast.LENGTH_SHORT).show();
                }else{
                    reconocerTextoImagen();
                }
            }
        });

    }

    private void reconocerTextoImagen() {
        progressDialog.setMessage("Preparando imagen");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this, uri);
            progressDialog.setMessage("Reconociendo texto");
            Task<Text> textTask = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            // En caso de que la acción se ejecute correctamente entra en este método
                            progressDialog.dismiss(); // Descartamos el progressdialog
                            // Obtenemos el texto de la imagen
                            String texto = text.getText();
                            TextoReconocidoET.setText(texto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // En caso contrario entraría en este otro
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "No se pudo reconocer el texto. Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error al preparar la imagen "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaARL.launch(intent);
    }

    private void abrirCamara(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Titulo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion");

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camaraARL.launch(intent);
    }

    private ActivityResultLauncher<Intent> galeriaARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        uri = data.getData();
                        imagen.setImageURI(uri);
                        TextoReconocidoET.setText("");
                    }else{
                        Toast.makeText(MainActivity.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> camaraARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        imagen.setImageURI(uri);
                        TextoReconocidoET.setText("");
                    }else{
                        Toast.makeText(MainActivity.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mi_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Acción de los elementos del menú

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Menu_abrir_galeria){
            abrirGaleria();
//            Toast.makeText(this, "Abrir galería", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.Menu_abrir_camara){
            abrirCamara();
//            Toast.makeText(this, "Abrir cámara", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}