package com.nick.proyectoscannertranslator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nick.proyectoscannertranslator.R;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView imageIv;

    private static final String TAG = "IMAGE_TAG";
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        getSupportActionBar().setTitle("ImageView");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageIv = findViewById(R.id.imageIv);

        image = getIntent().getStringExtra("imageUri");

        Log.d(TAG, "onCreate: Imagen: "+image);

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_image_black)
                .into(imageIv);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}