package com.example.tfg;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.imgproc.Imgproc;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat1;
    BaseLoaderCallback baseLoaderCallback;
    private ImageView imageView;

    boolean isFastActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.camera_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        Button toggleFast = (Button) this.findViewById(R.id.toggleFast);
        toggleFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFastActive = !isFastActive;
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {

                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;

                }
            }
        };
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat1=new Mat(width,height, CvType.CV_8UC4);

    }

    @Override
    public void onCameraViewStopped() {
        mat1.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat1=inputFrame.rgba();

        FastFeatureDetector fast = FastFeatureDetector.create();

        //This to rotate the Frame to 90 degree
        Mat mRgbaT = mat1.t();
        Core.flip(mat1.t(), mRgbaT, 1);
        Imgproc.resize(mRgbaT, mRgbaT, mat1.size());

        if(isFastActive) {

            Log.d("OPA", "ALO" + mRgbaT.toString());

            Imgproc.cvtColor(mRgbaT, mRgbaT, Imgproc.COLOR_RGB2GRAY);

            MatOfKeyPoint kp = new MatOfKeyPoint();

            fast.detect(mRgbaT, kp);

            Imgproc.cvtColor(mRgbaT, mRgbaT, Imgproc.COLOR_GRAY2RGB);


            for (KeyPoint k : kp.toArray()) {
                Log.d("OPA", k.toString());
                drawKeyPoint(mRgbaT, k.pt.x, k.pt.y);
            }
        }

        return mRgbaT;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null)
        {
            cameraBridgeViewBase.disableView();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug())
        {
            Toast.makeText(getApplicationContext(),"There is problem in OpenCV",Toast.LENGTH_SHORT).show();
        }
        else {
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!OpenCVLoader.initDebug())
        {
            Toast.makeText(getApplicationContext(),"There is problem in OpenCV",Toast.LENGTH_SHORT).show();
        }
    }

    void drawKeyPoint(Mat img, double x, double y) {
        Imgproc.circle(img, new Point(x, y), 5, Colors.getRandomColor(), 5);
    }
}

