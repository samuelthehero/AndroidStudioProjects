package com.nick.scannertranslator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nick.scannertranslator.activities.PdfViewActivity;
import com.nick.scannertranslator.adapters.AdapterPdf;
import com.nick.scannertranslator.models.ModelPdf;

import java.io.File;
import java.util.ArrayList;

public class PDFListFragment extends Fragment {

    private RecyclerView pdfRV;

    private Context mContext;

    private ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdf adapterPdf;
    private static final String TAG = "PDF_LIST_TAG";

    public PDFListFragment() {
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
        return inflater.inflate(R.layout.fragment_pdf_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdfRV = view.findViewById(R.id.pdfRV);
        loadPdfDocuments();
    }

    private void loadPdfDocuments() {

        // Inicia el arrayList antes de añadirle datos y obtiene los datos de alli
        pdfArrayList = new ArrayList<>();

        // Inicia el adapter para colocarlo como contexto en el primer parámetro del recyclerView.
        // El segundo parámetro es para el arrayList de los documentos PDF
        adapterPdf = new AdapterPdf(mContext, pdfArrayList, new RvListenerPdf() {
            @Override
            public void onPdfClick(ModelPdf modelPdf, int position) {

                Intent intent = new Intent(mContext, PdfViewActivity.class);
                intent.putExtra("pdfUri", ""+modelPdf.getUri());
                startActivity(intent);
            }

            @Override
            public void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder) {
                // Muestra un diálogo de opciones como Eliminar, Renombrar y Compartir
                showMoreOptions(modelPdf, holder);
            }
        });
        // Coloca el adapter en el RecyclerView
        pdfRV.setAdapter(adapterPdf);

        // La carpeta donde los documentos son guardados después de la conversión de la imagen
        File directorio = new File(mContext.getExternalFilesDir(null), Constantes.PDF_FOLDER);

        // Comprueba si la capeta existe o no
        if(directorio.exists()){// Si la carpeta existe, obtiene una lista de archivos de ello
            File[] files = directorio.listFiles();
            Log.d(TAG, "loadPdfDocument: Cantidad de archivos: "+files.length);

            for(File entrada : files){
                Log.d(TAG, "loadPdfDocument: Nombre del archivo: "+entrada.getName());

                Uri uri = Uri.fromFile(entrada);

                ModelPdf modelPdf = new ModelPdf(entrada, uri);

                pdfArrayList.add(modelPdf);

                adapterPdf.notifyItemInserted(pdfArrayList.size());
            }
        }
    }

    private void showMoreOptions(ModelPdf modelPdf, AdapterPdf.HolderPdf holder) {

        Log.d(TAG, "showMoreOptions: ");


        PopupMenu popupMenu = new PopupMenu(mContext, holder.moreBtn);

        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Renombrar");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Eliminar");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Compartir");

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Obtiene el id del item seleccionado del menu
                int itemId = item.getItemId();

                if(itemId == 0){

                    renombrarPdf(modelPdf);
                } else if (itemId == 1) {


                }
                if(itemId == 2){

                }
                return true;
            }
        });
    }

    private void renombrarPdf(ModelPdf modelPdf){
        Log.d(TAG, "renombrarPdf: ");
    }
}