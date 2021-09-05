package com.example.galleryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity2 extends AppCompatActivity {
private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        img=findViewById(R.id.imageview123);
        String path = getIntent().getStringExtra("PATH");
        File imgFile = new  File(path);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            img.setImageBitmap(myBitmap);
        }
    }
}