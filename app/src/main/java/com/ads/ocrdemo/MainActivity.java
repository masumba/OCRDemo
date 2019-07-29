package com.ads.ocrdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoogleVision = findViewById(R.id.btnGoogleVision);
        Button btnFace = findViewById(R.id.btnFace);
        Button btnBoth = findViewById(R.id.btnBoth);

        btnGoogleVision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GoogleVisionActivity.class);
                startActivity(intent);
            }
        });

        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FaceDetectionActivity.class);
                startActivity(intent);
            }
        });

        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,OcrFaceActivity.class);
                startActivity(intent);
            }
        });
    }
}
