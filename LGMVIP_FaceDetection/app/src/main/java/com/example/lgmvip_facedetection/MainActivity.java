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

        FirebaseApp.initializeApp(MainActivity.this);
        cameraButton = findViewById(R.id.camera_button);


        cameraButton.setOnClickListener(
                v -> {
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

                        Toast
                                .makeText(
                                        MainActivity.this,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode,
                data);
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extra = data.getExtras();
            Bitmap bitmap = (Bitmap) extra.get("data");
            AnalyzeFaces(bitmap);
        }
    }

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