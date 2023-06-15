package com.nick.scannertranslator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Log;

import com.nick.scannertranslator.R;
import com.nick.scannertranslator.adapters.AdapterPdfView;
import com.nick.scannertranslator.models.ModelPdfView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfViewActivity extends AppCompatActivity {

    private RecyclerView pdfViewRv;

    private String pdfUri;

    private static final String TAG = "PDF_VIEW_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        // Cambia el título del ActionBar, muestra el botón de retroceso en el action bar
        getSupportActionBar().setTitle("PDF Viewer");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfViewRv = findViewById(R.id.pdfViewRV);
        pdfUri = getIntent().getStringExtra("pdfUri");

        Log.d(TAG, "onCreate: pdfUri"+pdfUri);

        loadPdfPages();
    }

    private PdfRenderer.Page mCurrentPage = null;
    private void loadPdfPages() {
        Log.d(TAG, "loadPdfPages: ");

        ArrayList<ModelPdfView> pdfViewArrayList = new ArrayList<>();

        AdapterPdfView adapterPdfView = new AdapterPdfView(this, pdfViewArrayList);

        pdfViewRv.setAdapter(adapterPdfView);

        File file = new File(Uri.parse(pdfUri).getPath());
        try{

            getSupportActionBar().setSubtitle(file.getName());

        }catch(Exception e){

            Log.d(TAG, "loadPdfPages: ", e);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer mPdfRendered = new PdfRenderer(parcelFileDescriptor);

                    int pageCount = mPdfRendered.getPageCount();

                    if(pageCount <= 0){

                        Log.d(TAG, "run: No hay páginas en el archivo PDF");
                    }else{

                        Log.d(TAG, "run: El archivo PDF tiene páginas");

                        for(int i=0; i<pageCount; i++){

                            if(mCurrentPage != null){
                                mCurrentPage.close();
                            }

                            mCurrentPage = mPdfRendered.openPage(i);

                            Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);

                            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                            pdfViewArrayList.add(new ModelPdfView(Uri.parse(pdfUri), (i+1), pageCount, bitmap));
                        }
                    }

                }catch (Exception e){

                    Log.d(TAG, "run: 01",e);

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: UI thread...");
                        adapterPdfView.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}