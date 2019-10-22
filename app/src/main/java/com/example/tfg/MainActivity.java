package com.example.tfg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.imgproc.Imgproc;


import java.io.IOException;

class COLORS {
    static Scalar BLUE = new Scalar(0,120,255,0);
    static Scalar PURPLE = new Scalar(189,0,255,0);
    static Scalar ORANGE = new Scalar(255,154,0,0);
    static Scalar GREEN = new Scalar(1,255,31,0);
    static Scalar YELLOW = new Scalar(227,255,0,0);

    static Scalar getRandomColor(){
        Scalar[] Colors = {BLUE, PURPLE, ORANGE, GREEN, YELLOW};

        return Colors[(int) (Math.random() * Colors.length)];
    }
}

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();
    }

    public void analyzePhotos(View v){

        FastFeatureDetector fast = FastFeatureDetector.create();

        Mat img = null;

        try {
            img = Utils.loadResource(getApplicationContext(), R.drawable.test);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);

        MatOfKeyPoint kp = new MatOfKeyPoint();

        fast.detect(img, kp);

        Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2RGB);


        for(KeyPoint k: kp.toArray()){
            Log.d("OPA", k.toString());
            drawKeyPoint(img, k.pt.x, k.pt.y);
        }

        //Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGRA);



        //Mat img_result = img.clone();
        //Imgproc.Canny(img, img_result, 80, 90);
        //Bitmap img_bitmap = Bitmap.createBitmap(img_result.cols(), img_result.rows(),Bitmap.Config.ARGB_8888);



        Bitmap img_bitmap = Bitmap.createBitmap(img.cols(), img.rows(),Bitmap.Config.RGB_565);

        Utils.matToBitmap(img, img_bitmap);
        ImageView imageView = findViewById(R.id.img);
        imageView.setImageBitmap(img_bitmap);
    }

    void drawKeyPoint(Mat img, double x, double y){
        Imgproc.circle(img, new Point(x, y), 5, COLORS.getRandomColor(), 5);
    }
}