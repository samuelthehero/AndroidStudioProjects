package com.nick.scannertranslator.adapters;

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
import com.nick.scannertranslator.MyApplication;
import com.nick.scannertranslator.R;
import com.nick.scannertranslator.RvListenerPdf;
import com.nick.scannertranslator.models.ModelPdf;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdapterPdf extends RecyclerView.Adapter<AdapterPdf.HolderPdf> {

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private RvListenerPdf rvListenerPdf;

    private static final String TAG = "ADAPTER_PDF_TAG";

    public AdapterPdf(android.content.Context context, ArrayList<ModelPdf> pdfArrayList, RvListenerPdf rvListenerPdf) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.rvListenerPdf = rvListenerPdf;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pdf, parent, false);
        return new HolderPdf(view);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HolderPdf holder, int position) {
        ModelPdf modelPdf = pdfArrayList.get(position);

        String name = modelPdf.getFile().getName();
        long timestamp = modelPdf.getFile().lastModified();

        String formatteDate = MyApplication.formatTimestamp(timestamp);

        // Funcion para llamar al tamaño del archivo del controlador
        loadFileSize(modelPdf, holder);
        loadThumbnailFromPdfFile(modelPdf, holder);

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

    private void loadThumbnailFromPdfFile(ModelPdf modelPdf, HolderPdf holder) {

        Log.d(TAG, "loadThumbnailFromPdfFile: ");

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
                        // No hay páginas en pdf, no se muestra el thumbnail

                        Log.d(TAG, "loadThumbnailFromPdfFile run: No hay páginas");
                        
                    }else{
                        // Hay páginas en pdf, se muestra el thumbnails. Usar "OnePage" para abrir una página PDF en específica
                        PdfRenderer.Page currentPage = pdfRenderer.openPage(0);

                        thumbnailBitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);

                        currentPage.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        
                    }

                }catch(Exception e){
                    Log.d(TAG, "loadThumbnailFromPdfFile run: ", e);
                }

                Bitmap finalThumbnailBitmap = thumbnailBitmap;
                int finalPageCount = pageCount;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "loadThumbnailFromPdfFile run: Ajustes de Thumbnail");

                        Glide.with(context)
                                .load(finalThumbnailBitmap)
                                .fitCenter()
                                .placeholder(R.drawable.icon_pdf)
                                .into(holder.tumbnailTv);

//                        holder.pagesTv.setText(""+finalPageCount+" Pages");
                    }
                });
            }
        });
    }

    private void loadFileSize(ModelPdf modelPdf, HolderPdf holder) {
        // Obtiene el tamaño del archivo en Bytes
        double bytes = modelPdf.getFile().length();

        double kb = bytes/1024;
        double mb = bytes/1024;

        String size = "";

        if(mb >= 1){

            size = String.format("%.2f", mb) + " MB";

        } else if (kb >= 1) {

            size = String.format("%.2f", kb) + " KB";

        }else{

            size = String.format("%.2f", bytes) + " bytes";

        }

        Log.d(TAG, "loadFileSize: Tamaño del archivo: "+size);

        holder.sizesTv.setText(size);
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    public class HolderPdf extends RecyclerView.ViewHolder{

        public ImageView tumbnailTv;
        public TextView nameTv, pagesTv, sizesTv, dateTv;
        public ImageButton moreBtn;

        public HolderPdf(@NonNull View itemView) {
            super(itemView);

            tumbnailTv = itemView.findViewById(R.id.tumbnailIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            sizesTv = itemView.findViewById(R.id.sizeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            dateTv = itemView.findViewById(R.id.dateTv);
        }
    }
}
