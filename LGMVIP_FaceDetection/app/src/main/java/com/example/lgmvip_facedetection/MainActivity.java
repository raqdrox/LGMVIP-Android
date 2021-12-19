package com.example.lgmvip_facedetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button cameraButton;
    private final static int REQUEST_IMAGE_CAPTURE = 124;
    InputImage image;
    FaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing our firebase in main activity
        FirebaseApp.initializeApp(MainActivity.this);

        // finding the elements by their id's allotted.
        cameraButton = findViewById(R.id.camera_button);

        // setting an onclick listener to the button so as
        // to request image capture using camera
        cameraButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        // making a new intent for opening camera
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(
                                getPackageManager())
                                != null) {
                            startActivityForResult(
                                    intent, REQUEST_IMAGE_CAPTURE);
                        }
                        else {
                            // if the image is not captured, set
                            // a toast to display an error image.
                            Toast
                                    .makeText(
                                            MainActivity.this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        // after the image is captured, ML Kit provides an
        // easy way to detect faces from variety of image
        // types like Bitmap

        super.onActivityResult(requestCode, resultCode,
                data);
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();
            Bitmap bitmap = (Bitmap) extra.get("data");
            AnalyzeFaces(bitmap);
        }
    }
    // If you want to configure your face detection model
    // according to your needs, you can do that with a
    // FirebaseVisionFaceDetectorOptions object.
    private void AnalyzeFaces(Bitmap bitmap)
    {
        FaceDetectorOptions options
                = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        try {

            image = InputImage.fromBitmap(bitmap,0);
            detector = FaceDetection.getClient(options);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                faces -> {
                                    List<FaceModel> faceModelList=new ArrayList<>();
                                    int i = 1;
                                    for (Face face : faces) {
                                        FaceModel faceModel = new FaceModel();
                                        faceModel.setFaceId(i);
                                        faceModel.setAngleX(face.getHeadEulerAngleX());
                                        faceModel.setAngleY(face.getHeadEulerAngleY());
                                        faceModel.setAngleZ(face.getHeadEulerAngleZ());
                                        faceModel.setSmile(face.getSmilingProbability()*100);
                                        faceModel.setLeftEye(face.getLeftEyeOpenProbability()*100);
                                        faceModel.setRightEye(face.getRightEyeOpenProbability()*100);
                                        faceModelList.add(faceModel);
                                        i++;


                                    }if (faces.size() == 0) {
                                        Toast.makeText(MainActivity.this,"NO FACES DETECTED",Toast.LENGTH_SHORT).show();
                                    }else {
                                        System.out.println("Done");
                                        DialogFragment resultDialog
                                                = new ResultDialog(this,faceModelList);
                                        resultDialog.setCancelable(true);
                                        resultDialog.show(
                                                getSupportFragmentManager(),
                                                LCOFaceDetection.RESULT_DIALOG);
                                    }

                                })
                        .addOnFailureListener(
                                e -> Toast.makeText(MainActivity.this,"Exception Occurred : "+e,Toast.LENGTH_SHORT).show());
    }
}