package com.nick.scannertranslator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.nick.scannertranslator.ImageListFragment;
import com.nick.scannertranslator.PDFListFragment;
import com.nick.scannertranslator.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.botton_menu_imagenes){
                    loadImagesFragment();
                } else if (itemId == R.id.botton_menu_pdf) {
                    loadPdfsFragment();
                }
                return true;
            }
        });

        loadImagesFragment();
    }

    private void loadImagesFragment() {
        setTitle("Images");
        ImageListFragment imageListFragment = new ImageListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, imageListFragment, "ImageListFragment");
        fragmentTransaction.commit();
    }
    private void loadPdfsFragment() {
        setTitle("PDF");
        PDFListFragment pdfListFragment = new PDFListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, pdfListFragment, "PDFListFragment");
        fragmentTransaction.commit();
    }
}