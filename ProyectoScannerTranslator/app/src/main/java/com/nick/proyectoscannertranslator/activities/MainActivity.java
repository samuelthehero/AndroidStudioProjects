package com.nick.proyectoscannertranslator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.nick.proyectoscannertranslator.ImageListFragment;
import com.nick.proyectoscannertranslator.PdfListFragment;
import com.nick.proyectoscannertranslator.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        loadImagesFragemt();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.bottom_menu_images) {
                    // Muestra lista de imágenes
                    loadImagesFragemt();
                } else if (itemId == R.id.bottom_menu_pdfs) {
                    // Muestra lista de PDFS
                    loadPdfsFragment();
                }

                return true;
            }
        });
    }

    private void loadImagesFragemt() {
        setTitle("Imágenes");
        ImageListFragment imageListFragment = new ImageListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, imageListFragment, "");
        fragmentTransaction.commit();
    }

    private void loadPdfsFragment() {
        setTitle("Lista de PDFS");
        PdfListFragment pdfListFragment = new PdfListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, pdfListFragment, "");
        fragmentTransaction.commit();
    }
}