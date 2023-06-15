package com.nick.scannertranslator.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nick.scannertranslator.R;
import com.nick.scannertranslator.models.ModelPdfView;

import java.util.ArrayList;

public class AdapterPdfView extends RecyclerView.Adapter<AdapterPdfView.HolderPdfView> {

    private Context context;
    private ArrayList<ModelPdfView> pdfViewArrayList;

    public AdapterPdfView(Context context, ArrayList<ModelPdfView> pdfViewArrayList) {
        this.context = context;
        this.pdfViewArrayList = pdfViewArrayList;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public HolderPdfView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inicia o infla cada item del RecyclerView del layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_pdf_view, parent, false);
        return new HolderPdfView(view);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HolderPdfView holder, int position) {

        ModelPdfView modelPdfView = pdfViewArrayList.get(position);

        Bitmap bitmap = modelPdfView.getBitmap();
        int pageNumber = position+1;

        Glide.with(context)
                .load(bitmap)
                .placeholder(R.drawable.icon_image)
                .into(holder.imageIv);

        holder.pageNumberTv.setText(""+pageNumber);
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        // Devuelve el número de filas de la lista
        return pdfViewArrayList.size();
    }


    class HolderPdfView extends RecyclerView.ViewHolder{

        TextView pageNumberTv;
        ImageView imageIv;

        public HolderPdfView(@NonNull View itemView) {
            super(itemView);

            pageNumberTv = itemView.findViewById(R.id.pageNumberTv);
            imageIv = itemView.findViewById(R.id.imageIv);
        }
    }
}
