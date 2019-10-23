package com.example.tfg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.bravobit.ffmpeg.FFmpeg;

class COLORS {
  static Scalar BLUE = new Scalar(0, 120, 255, 0);
  static Scalar PURPLE = new Scalar(189, 0, 255, 0);
  static Scalar ORANGE = new Scalar(255, 154, 0, 0);
  static Scalar GREEN = new Scalar(1, 255, 31, 0);
  static Scalar YELLOW = new Scalar(227, 255, 0, 0);

  static Scalar getRandomColor() {
    Scalar[] Colors = {BLUE, PURPLE, ORANGE, GREEN, YELLOW};

    return Colors[(int) (Math.random() * Colors.length)];
  }
}

public class MainActivity extends AppCompatActivity {

  private static final int CAMERA_REQUEST = 1888;
  private ImageView imageView;
  private static final int MY_CAMERA_PERMISSION_CODE = 100;

  private Uri capturedImageUri;

  private AlertDialog alerta;

  private void DialogAlert(String title, String message) {
    //Cria o gerador do AlertDialog
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //define o titulo
    builder.setTitle(title);
    //define a mensagem
    builder.setMessage(message);
    //define um botão como positivo
    builder.setPositiveButton("Positivo", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface arg0, int arg1) {
        Toast.makeText(MainActivity.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
      }
    });
    //define um botão como negativo.
    builder.setNegativeButton("Negativo", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface arg0, int arg1) {
        Toast.makeText(MainActivity.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
      }
    });
    //cria o AlertDialog
    alerta = builder.create();
    //Exibe
    alerta.show();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    OpenCVLoader.initDebug();

    this.imageView = (ImageView) this.findViewById(R.id.img);
    Button photoButton = (Button) this.findViewById(R.id.photo);
    photoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
          requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 30);
        } else if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
          //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 30);
          requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
          openBackCamera();
        }
      }
    });

    if (FFmpeg.getInstance(this).isSupported()) {
      DialogAlert("Funciona", "Funciona MESMO");
    } else {
      DialogAlert("Não Funciona", "SE FUDEU");
    }
  }

  private String pictureImagePath = "";

  private void openBackCamera() {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = timeStamp + ".jpg";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
    File file = new File(pictureImagePath);
    Uri outputFileUri = FileProvider.getUriForFile(
      MainActivity.this,
      "com.example.tfg.fileprovider",
      file);
    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    startActivityForResult(cameraIntent, 1);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    Log.d("OPA", requestCode + " REQUEST CODE");

    if (requestCode == 30) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
        //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(cameraIntent, CAMERA_REQUEST);
      } else {
        Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
      }
    }

    if (requestCode == MY_CAMERA_PERMISSION_CODE) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
      } else {
        Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("OPA", resultCode + "");
    if (requestCode == 1) {
      File imgFile = new File(pictureImagePath);
      if (imgFile.exists()) {
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView myImage = (ImageView) findViewById(R.id.img);
        myImage.setImageBitmap(myBitmap);

      }
    }

  }

  public void analyzePhotos(View v) {

    FastFeatureDetector fast = FastFeatureDetector.create();

    Mat img = new Mat();

    //img = Utils.loadResource(getApplicationContext(), R.drawable.test);
    Utils.bitmapToMat(((BitmapDrawable) imageView.getDrawable()).getBitmap(), img);

    Log.d("OPA", "ALO" + img.toString());

    Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);

    MatOfKeyPoint kp = new MatOfKeyPoint();

    fast.detect(img, kp);

    Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2RGB);


    for (KeyPoint k : kp.toArray()) {
      Log.d("OPA", k.toString());
      drawKeyPoint(img, k.pt.x, k.pt.y);
    }

    //Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGRA);


    //Mat img_result = img.clone();
    //Imgproc.Canny(img, img_result, 80, 90);
    //Bitmap img_bitmap = Bitmap.createBitmap(img_result.cols(), img_result.rows(),Bitmap.Config.ARGB_8888);


    Bitmap img_bitmap = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.RGB_565);

    Utils.matToBitmap(img, img_bitmap);
    ImageView imageView = findViewById(R.id.img);
    imageView.setImageBitmap(img_bitmap);
  }

  void drawKeyPoint(Mat img, double x, double y) {
    Imgproc.circle(img, new Point(x, y), 5, COLORS.getRandomColor(), 5);
  }
}