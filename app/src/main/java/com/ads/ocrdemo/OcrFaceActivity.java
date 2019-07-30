package com.ads.ocrdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.text.TextRecognizer.Builder;

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

        surfaceView = findViewById(R.id.surfaceView);
        imageView = findViewById(R.id.imgImage);
        textView = findViewById(R.id.tvGoogleViewText);

        /*nrc,doc type, first, middle, last, date, gender, image*/
        runTextRecognitionBlock();

    }

    void runTextRecognitionBlock() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Text Detector Dependencies are not yet available", Toast.LENGTH_SHORT).show();
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            //surfaceView
            runSurfaceView();

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> textBlockSparseArray = detections.getDetectedItems();
                    if (textBlockSparseArray.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < textBlockSparseArray.size(); i++) {
                                    TextBlock item = textBlockSparseArray.valueAt(i);
                                    stringBuilder.append(item.getValue().trim());
                                    stringBuilder.append("\n");
                                }
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    void runFaceDetectionBlock() {
        final FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setProminentFaceOnly(true)
                .setTrackingEnabled(true)
                .build();

        if (!faceDetector.isOperational()) {
            Toast.makeText(this, "Face Detector Dependencies are not yet available", Toast.LENGTH_SHORT).show();
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), faceDetector)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedPreviewSize(320, 240)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            runSurfaceView();

            faceDetector.setProcessor(new Detector.Processor<Face>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Face> detections) {
                    Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(),Bitmap.Config.RGB_565);
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    final SparseArray<Face> faceSparseArray = faceDetector.detect(frame);

                    Paint paint = new Paint();
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(tempBitmap);
                    canvas.drawBitmap(bitmap,0,0,null);

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
                }
            });

            /**/
            /*Paint paint = new Paint();
            paint.setStrokeWidth(5);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            Bitmap tempBitmap = Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(),Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(bitmap,0,0,null);
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

            imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));*/
            /**/

        }
    }

    void runMultiDetector(){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).build();

        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(textRecognizer)
                .add(faceDetector)
                .build();

        if (!multiDetector.isOperational()) {
            // ...
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), multiDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(15.0f)
                    .build();
        }
    }

    void runSurfaceView() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Activity appActivity = (Activity) getApplicationContext();
                    ActivityCompat.requestPermissions(appActivity,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION_ID);
                    return;
                }

                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    String errorMsg = "Error Starting Camera Source: OcrFaceActivity.runSurfaceView.surfaceCreated => " + e.getMessage();
                    System.out.println(errorMsg);
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_ID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        String errorMsg = "Error Starting Camera Source: OcrFaceActivity.onRequestPermissionsResult => " + e.getMessage();
                        System.out.println(errorMsg);
                        e.printStackTrace();
                    }
                }
            }
        }    }
}
