package com.nick.proyectoscannertranslator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nick.proyectoscannertranslator.MyApplication;
import com.nick.proyectoscannertranslator.R;
import com.nick.proyectoscannertranslator.models.ModelPdf;
import com.nick.proyectoscannertranslator.RvListenerPdf;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdapterPdf extends RecyclerView.Adapter<AdapterPdf.HolderPdf>{

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private RvListenerPdf rvListenerPdf;

    private static final String TAG = "ADAPTER_PDF_TAG";

    public AdapterPdf(Context context, ArrayList<ModelPdf> pdfArrayList, RvListenerPdf rvListenerPdf) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.rvListenerPdf = rvListenerPdf;
    }

    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pdf, parent, false);

        return new HolderPdf(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdf holder, @SuppressLint("RecyclerView") int position) {

        ModelPdf modelPdf = pdfArrayList.get(position);

        String name = modelPdf.getFile().getName();

        long timestamp = modelPdf.getFile().lastModified();

        String formatteDate = MyApplication.formatTimestamp(timestamp);

        cargarTamañoArchivo(modelPdf, holder);
        cargarThumbnailDeArchivoPDF(modelPdf, holder);

        holder.nameTv.setText(name);
        holder.dateTv.setText(formatteDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListenerPdf.onPdfClick(modelPdf, position);
            }
        });

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListenerPdf.onPdfMoreClick(modelPdf, position, holder);
            }
        });

    }

    private void cargarThumbnailDeArchivoPDF(ModelPdf modelPdf, HolderPdf holder) {

        Log.d(TAG, "cargarThumbnailDeArchivoPDF: ");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Bitmap thumbnailBitmap = null;
                int pageCount = 0;

                try{

                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(modelPdf.getFile(), ParcelFileDescriptor.MODE_READ_ONLY);

                    PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                    pageCount = pdfRenderer.getPageCount();
                    if(pageCount<=0){

                        Log.d(TAG, "cargarThumbnailDeArchivoPDF run: Sin páginas");

                    }else{

                        PdfRenderer.Page paginaActual = pdfRenderer.openPage(0);

                        thumbnailBitmap = Bitmap.createBitmap(paginaActual.getWidth(), paginaActual.getHeight(), Bitmap.Config.ARGB_8888);

                        paginaActual.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    }

                }catch(Exception e){
                    Log.d(TAG, "cargarThumbnailDeArchivoPDF run: ", e);
                }

                Bitmap finalThumbnailBitmap = thumbnailBitmap;
                int finalPageCount = pageCount;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "cargarThumbnailDeArchivoPDF run: Ajustes de Thumbnail");

                        Glide.with(context)
                                .load(finalThumbnailBitmap)
                                .fitCenter()
                                .placeholder(R.drawable.ic_pdf_black)
                                .into(holder.thumbnailIv);

                        holder.pagesTv.setText(""+ finalPageCount +" páginas");
                    }
                });

            }
        });

    }

    private void cargarTamañoArchivo(ModelPdf modelPdf, HolderPdf holder) {

        double bytes = modelPdf.getFile().length();

        double kb = bytes/1024;
        double mb = kb/1024;

        String size = "";

        if(mb > 1){

            size = String.format("%.2f",mb)+" MB";
            
        } else if (kb >= 1) {

            size = String.format("%.2f",kb)+" KB";

        }
        else{

            size = String.format("%.2f",bytes)+" bytes";

        }

        Log.d(TAG, "cargarTamañoArchivo: Tamaño del archivo: "+size);

        holder.sizeTv.setText(size);

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class HolderPdf extends RecyclerView.ViewHolder{

        public ImageView thumbnailIv;
        public TextView nameTv, pagesTv, sizeTv, dateTv;
        public ImageButton moreBtn;

        public HolderPdf(@NonNull View itemView) {
            super(itemView);

            thumbnailIv = itemView.findViewById(R.id.thumbnailIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            pagesTv = itemView.findViewById(R.id.pagesTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            dateTv = itemView.findViewById(R.id.dateTv);

        }
    }
}
