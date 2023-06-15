package com.nick.imagecropperexample;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yalantis.ucrop.UCropActivity;

public class MainActivity extends AppCompatActivity {

    ImageView iv_pick_image;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_pick_image = findViewById(R.id.iv_pick_image);

        iv_pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Intent intent = new Intent(MainActivity.this, CropperActivity.class);
                        intent.putExtra("DATA", result.toString());
                        startActivityForResult(intent, 101);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1 && requestCode == 101){

            String result = data.getStringExtra("RESULT");
            Uri resultUri;
            if (result != null) {

                resultUri = Uri.parse(result);

                iv_pick_image.setImageURI(resultUri);
            }
        }
    }
}