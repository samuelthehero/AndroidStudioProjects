package com.example.monitorterremotos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.monitorterremotos.adapter.TerremotoAdapter;
import com.example.monitorterremotos.databaseFile.TerremotoSQLiteHelper;
import com.example.monitorterremotos.model.Terremoto;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    Button btRetrofit;
    private SQLiteDatabase db;
    int id;
    String place;
    long time;
    double magnitude;
    private List<Terremoto> lista_terremoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Abrimos la base de datos 'DBTerremoto' en modo escritura
        TerremotoSQLiteHelper usdbh =
                new TerremotoSQLiteHelper(this, "DBTerremoto", null, 2);

        db = usdbh.getWritableDatabase();

        insertDatos(lista_terremoto);
        mostrarDatos();
        setUpView();
    }

    private void insertDatos(List<Terremoto> terremotoList) {
        for (Terremoto terremoto: terremotoList) {
            int id = Integer.parseInt(terremoto.getId());
            String place = terremoto.getProperties().getPlace();
            float magnitude = terremoto.getProperties().getMagnitude();
            String time = terremoto.getProperties().getTime();
            List<Float> latitude = terremoto.getGeomtery().getCoordinates();
            List<Float> longitude = terremoto.getGeomtery().getCoordinates();
            String sql = "INSERT INTO terremoto (id, place, time, magnitude, latitude, longitude) VALUES(" +
                    "'"+id+"','"+place+"', '"+time+"','"+magnitude+"','"+latitude+"','"+longitude+"')";
            db.execSQL(sql);
        }
    }

    private void mostrarDatos(){
        Cursor c = db.rawQuery("SELECT id, place, time, magnitude, longitude, latitude FROM Terremoto", null);
        if (c.moveToFirst()){
            do {
                int id = Integer.parseInt((c.getString(0)));
                String place = c.getString(1);
                float magnitude = c.getFloat(2);
                String time = c.getString(3);
                float latitude = c.getFloat(4);
                float longitude = c.getFloat(5);
            }while(c.moveToNext());
        }
    }

    private void setUpView() {
        btRetrofit = findViewById(R.id.btnAcceso);

        btRetrofit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RetrofitActivity.class);
                i.putExtra("id", id);
                i.putExtra("place", place);
                i.putExtra("time", time);
                i.putExtra("magnitude", magnitude);
                startActivity(i);
            }
        });
    }

    // CERRAMOS LA BASE DE DATOS
    public void onStop(){
        super.onStop();
        db.close();
    }
}