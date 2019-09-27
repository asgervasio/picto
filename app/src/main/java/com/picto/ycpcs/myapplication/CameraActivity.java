package com.picto.ycpcs.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    final int TAKE_PHOTO_CODE = 5;
    private String pictureImagePath = "";
    ImageView imageView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.imageView);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            //Toast.makeText(MainActivity.this, "Search clicked", Toast.LENGTH_LONG).show();
            takePicture();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void takePicture()
    {

        //pictureImagePath = getApplicationContext().getFilesDir().getPath().toString() + "/picto/images/"; // Files dir
        Toast.makeText(CameraActivity.this, "take picture clicked", Toast.LENGTH_LONG).show();
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = timeStamp + ".jpg";
        //File storageDir = Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File file = new File(pictureImagePath);
        //Uri outputFileUri = Uri.fromFile(file);
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:
                    //ImageView cameraImage = (ImageView) findViewById(R.id.action_camera);
                    //Toast.makeText(MainActivity.this, "picture taken", Toast.LENGTH_LONG).show();

                    //File imgFile= new File(pictureImagePath);
                    //if(imgFile.exists()){
                        //Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //}

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    int size = bitmap.getByteCount();
                    //Toast.makeText(CameraActivity.this, "picture taken, size is: " + size + " bytes", Toast.LENGTH_LONG).show();
                    imageView.setImageBitmap(bitmap);
                    break;
            }
            return;
        }
        else
        {

        }

        Toast.makeText(CameraActivity.this, "result hit resultCode " + resultCode + "  REQUEST code " + requestCode, Toast.LENGTH_LONG).show();


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showFullImage(View view) {
        String path = (String) view.getTag();

        if (path != null) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri imgUri = Uri.parse("file://" + path);
            intent.setDataAndType(imgUri, "image/*");
            startActivity(intent);

        }

    }
}
