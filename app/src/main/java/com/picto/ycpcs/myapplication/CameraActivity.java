package com.picto.ycpcs.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {
    final int TAKE_PHOTO_CODE = 5;
    ImageView imageView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void takePicture()
    {

        final String picturesPath = getApplicationContext().getFilesDir().getPath().toString() + "/snap_it_picFolder/"; // Files dir
        Toast.makeText(CameraActivity.this, "take picture clicked", Toast.LENGTH_LONG).show();

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:
                    //Toast.makeText(MainActivity.this, "picture taken", Toast.LENGTH_LONG).show();
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    int size = bitmap.getByteCount();
                    Toast.makeText(CameraActivity.this, "picture taken, size is: " + size + " bytes", Toast.LENGTH_LONG).show();
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
}
