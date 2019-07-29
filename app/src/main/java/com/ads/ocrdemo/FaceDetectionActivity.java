package com.ads.ocrdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class FaceDetectionActivity extends AppCompatActivity {

    SurfaceView cameraView;
    ImageView imageView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        cameraView = findViewById(R.id.surfaceView);
        imageView = findViewById(R.id.imgImage);

        /**/
        Bitmap bitmap = BitmapFactory.decodeResource(FaceDetectionActivity.this.getResources(), R.drawable.face);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(bitmap,0,0,null);

        /*Face Detect*/
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        if (!faceDetector.isOperational()){
            Toast.makeText(this, "Face Detector Dependencies are not yet available", Toast.LENGTH_SHORT).show();
            return;
        }
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faceSparseArray = faceDetector.detect(frame);

            for (int i=0; i<faceSparseArray.size();i++){
                Face face = faceSparseArray.valueAt(i);
                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1+face.getWidth();
                float y2 = y1+face.getHeight();
                RectF rectF = new RectF(x1,y1,x2,y2);
                canvas.drawRoundRect(rectF,2,2,paint);
            }

            imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));



        /**/
    }
}
