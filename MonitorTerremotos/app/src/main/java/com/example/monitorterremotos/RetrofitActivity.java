package com.example.monitorterremotos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitorterremotos.adapter.TerremotoAdapter;
import com.example.monitorterremotos.model.Data;
import com.example.monitorterremotos.model.Terremoto;
import com.example.monitorterremotos.webServiceClient.Api;
import com.example.monitorterremotos.webServiceClient.WebServiceClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitActivity extends AppCompatActivity implements ItemClickInterface{

    private RecyclerView recyclerView;
    private TerremotoAdapter adapter;
    private List<Terremoto> terremotos;
    private List<Terremoto.Properties> properties;
    private List<Terremoto.Geometry> geometryList;
    private Retrofit retrofit;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private OkHttpClient httpClientBuilder;
    private WebServiceClient client;
    private TextView latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
    }

    private void lanzarPeticion() {
        final ProgressDialog progressDialog = new ProgressDialog(RetrofitActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Espere, por favor");
        progressDialog.show();

        latitude = (TextView) findViewById(R.id.latitud);
        longitude = (TextView) findViewById(R.id.longitud);

        (Api.getClient().getTerremotos()).enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressDialog.dismiss();
                adapter.setData(response.body().getFeatures());
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d("TAG", "Error: "+t.getMessage());
                Toast.makeText(RetrofitActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void setUpView(){
        terremotos = new ArrayList<>();
        properties = new ArrayList<>();
        geometryList = new ArrayList<>();
//        ItemClickInterface listener = new ItemClickInterface() {
//            @Override
//            public void terremotoItemClickListener(TerremotoAdapter.TerremotoAdapterHoler holder, Terremoto terremoto) {
//                Intent i = new Intent(RetrofitActivity.this, MapsActivity.class);
//                i.putExtra("latitude", (CharSequence) latitude);
//                i.putExtra("longitude", (CharSequence) longitude);
//                startActivity(i);
//            }
//        };
        ItemClickInterface listener = null;
        adapter = new TerremotoAdapter(terremotos, properties, geometryList, null);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager lin = new LinearLayoutManager(this);
        lin.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(lin);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void terremotoItemClickListener(TerremotoAdapter.TerremotoAdapterHoler holder, Terremoto terremoto) {
        Intent i = new Intent(RetrofitActivity.this, MapsActivity.class);
        String longitud = String.valueOf(terremoto.getGeomtery().getCoordinates().get(0));
        String latitude = String.valueOf(terremoto.getGeomtery().getCoordinates().get(1));
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitud);
    }

}