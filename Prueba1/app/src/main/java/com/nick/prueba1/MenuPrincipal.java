package com.nick.prueba1;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nick.prueba1.MainActivity;
import com.nick.prueba1.R;

public class MenuPrincipal extends AppCompatActivity {

    Button btnReconocer, btnAcercaDe, btnSalir;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        btnReconocer = findViewById(R.id.btnReconocer);
        btnAcercaDe = findViewById(R.id.btnAcercaDe);
        btnSalir = findViewById(R.id.btnSalir);
        dialog = new Dialog(this);

        btnReconocer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            }
        });

        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialog();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void mostrarDialog(){
        Button btnEntendido;

        dialog.setContentView(R.layout.acerca_de);

        btnEntendido = dialog.findViewById(R.id.btnEntentido);

        btnEntendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }
}