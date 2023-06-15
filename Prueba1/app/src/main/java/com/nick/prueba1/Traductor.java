package com.nick.prueba1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Traductor extends AppCompatActivity {

    private String idiomaOrigen, idiomaDestino;
    private TextView texOrigen, textDestino;
    private Spinner spinnerOrigen, spinnerDestino;
    private ArrayAdapter<CharSequence> adapterOrigen, adapterDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traductor);

        spinnerOrigen = findViewById(R.id.IdiomaOrigen);
        spinnerDestino = findViewById(R.id.IdiomaDestino);

        adapterOrigen = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.spinner_layout);
        adapterDestino = ArrayAdapter.createFromResource(this, R.array.languages2, R.layout.spinner_layout);

        adapterOrigen.setDropDownViewResource(com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item);
        adapterDestino.setDropDownViewResource(com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item);

        spinnerOrigen.setAdapter(adapterOrigen);
        spinnerDestino.setAdapter(adapterDestino);

        Intent intent = getIntent();
        String texto_a_traducir = intent.getStringExtra("texto");

        EditText textoIntent = findViewById(R.id.textoOrigen);
        textoIntent.setText(texto_a_traducir);
    }
}