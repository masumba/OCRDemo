package com.ads.ocrdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Builder;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class OcrFaceActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private ImageView imageView;
    private static final int REQUEST_CAMERA_PERMISSION_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_face);

        /*surfaceView = findViewById(R.id.surfaceView);
        imageView = findViewById(R.id.imgImage);
        textView = findViewById(R.id.tvGoogleViewText);*/

        /*nrc,doc type, first, middle, last, date, gender, image*/
        faceCreateImg();
    }

    void faceCreateImg(){
        imageView = findViewById(R.id.imgImage);
        ImageView imageViewFace = findViewById(R.id.imgFace);

        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.face);
        imageView.setImageBitmap(bitmap);

        /*Style the square*/
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        /*create temp bitmap*/
        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);
        //canvas.drawBitmap(bitmap,0,0,null);

        /*Face Detection*/
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        if (!faceDetector.isOperational()){
            Toast.makeText(this, "Face Detector Dependencies are not yet available", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faceSparseArray = faceDetector.detect(frame);

            System.out.println("Monkey = "+faceSparseArray.size());

            for (int i=0; i<faceSparseArray.size();i++){
                Face face = faceSparseArray.valueAt(i);
                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1+face.getWidth();
                float y2 = y1+face.getHeight();
                RectF rectF = new RectF(x1,y1,x2,y2);

                //, Region.Op.REPLACE
                System.out.println("Donkey ="+rectF.left+"-"+rectF.top+"-"+rectF.right+"-"+rectF.bottom);

                int intWidth = (Math.round(rectF.width()));
                int intHeight = (Math.round(rectF.height()));
                Bitmap tempBitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.RGB_565);
                Canvas canvas1 = new Canvas(tempBitmap1);

                canvas1.clipRect(rectF.left,rectF.top,rectF.right,rectF.bottom);
                canvas1.getClipBounds();

                canvas1.drawBitmap(bitmap,0,0,null);
                imageViewFace.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap1));

                //canvas.drawRoundRect(rectF,2,2,paint);

            }

            imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            //imageViewFace.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            //imageViewFace.setImageBitmap(tempBitmap2);
        }
    }
}
