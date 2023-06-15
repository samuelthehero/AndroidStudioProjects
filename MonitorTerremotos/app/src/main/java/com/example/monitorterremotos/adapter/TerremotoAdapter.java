package com.example.monitorterremotos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitorterremotos.ItemClickInterface;
import com.example.monitorterremotos.R;
import com.example.monitorterremotos.model.Terremoto;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class TerremotoAdapter extends RecyclerView.Adapter<TerremotoAdapter.TerremotoAdapterHoler> {

    private List<Terremoto> terremotos;
    private List<Terremoto.Properties> properties;
    private List<Terremoto.Geometry> geometryList;
    private ItemClickInterface listener;

    public TerremotoAdapter(List<Terremoto> terremotos, List<Terremoto.Properties> properties, List<Terremoto.Geometry> geometryList, ItemClickInterface listener) {
        this.terremotos = terremotos;
        this.properties = properties;
        this.geometryList = geometryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TerremotoAdapterHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_item_retrofit, parent, false);
        return new TerremotoAdapterHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TerremotoAdapterHoler holder, int position) {
        Terremoto terremoto = terremotos.get(position);
        Terremoto.Properties properties = null;
        holder.properties.get(position);
        Terremoto.Geometry geometry = null;
        holder.geometryList.get(position);
        holder.titular.setText(terremoto.getId());
        holder.lugar.setText(properties.getPlace());
        holder.time.setText(properties.getTime());
        holder.magnitud.setText((int) properties.getMagnitude());
        holder.longitud.setText(String.valueOf(holder.longitud));
        holder.latitud.setText(String.valueOf(holder.latitud));
        holder.imageView.setImageResource(terremoto.getImage());
        holder.itemView.setOnClickListener(view -> {
            listener.terremotoItemClickListener(holder, terremoto);
        });
    }

    public void setData (List<Terremoto> terremotos) {
        this.terremotos = terremotos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return terremotos.size();
    }

    public class TerremotoAdapterHoler extends RecyclerView.ViewHolder{
        private TextView titular, lugar, magnitud, latitud, longitud, time;
        private List<Terremoto.Properties> properties;
        private List<Terremoto.Geometry> geometryList;
        private ShapeableImageView imageView;

        public TerremotoAdapterHoler(@NonNull View itemView) {
            super(itemView);
            titular = itemView.findViewById(R.id.titular);
            lugar = itemView.findViewById(R.id.lugar);
            time = itemView.findViewById(R.id.time);
            magnitud = itemView.findViewById(R.id.magnitud);
            latitud = itemView.findViewById(R.id.latitud);
            longitud = itemView.findViewById(R.id.longitud);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }
}
