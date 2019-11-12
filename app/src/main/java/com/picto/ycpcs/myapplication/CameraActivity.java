package com.picto.ycpcs.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CameraActivity extends AppCompatActivity {

    //public static int count = 0;
    final int TAKE_PHOTO_CODE = 5;
    ImageView imageView ;
    ApplicationState applicationState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.content_camera);
=======
        setContentView(R.layout.activity_camera);
>>>>>>> master
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.imageView);
        //Toast.makeText(MainActivity.this, "Picto app started", Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        // get global data reference
        //applicationState = ((ApplicationState)getApplicationContext());
        //applicationState.setParentActivity(this);

        loadSettings(); // load user settings
    }

    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.CameraActivity"));
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_messages)
        {
            startActivity(new Intent(this, MessagesListActivity.class));
            return true;
        }
        else if (id == R.id.action_pictures)
        {
            startActivity(new Intent(this, PictureListActivity.class));
            return true;
        }
        // action_pictures
        else if (id == R.id.action_take_picture) {
            takePicture();
            return true;
        }
        else if (id == R.id.action_login)
        {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startLoginActivity()
    {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void takePicture()
    {
        //Toast.makeText(MainActivity.this, "take picture clicked", Toast.LENGTH_LONG).show();

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case TAKE_PHOTO_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    applicationState.setLastPicture(bitmap);
                    int size = bitmap.getByteCount();
                    //Toast.makeText(MainActivity.this, "picture taken, size is: " + size + " bytes", Toast.LENGTH_LONG).show();
                    imageView.setImageBitmap(bitmap);


                    //startActivity(new Intent(this, MessageSendSaveActivity.class));
                    startActivity(new Intent(this, PictureSaveActivity.class));
                    break;

            }
            return;
        }
        else
        {

        }

        //Toast.makeText(MainActivity.this, "result hit resultCode " + resultCode + "  REQUEST code " + requestCode, Toast.LENGTH_LONG).show();


        super.onActivityResult(requestCode, resultCode, data);
    }

    void loadSettings()
    {
        try {
            String settings_filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + applicationState.getPictoSettingsFilename();
            File file = new File(settings_filename);

            if (file.exists() == true) {
                // read the contents of the file
                byte[] content = applicationState.getByteArrayFromFile(settings_filename);

                PictoSettings newItem = PictoSettings.bytesToPictoSettings(content);
                applicationState.ipAddress(newItem.ipAddress());
                applicationState.username(newItem.username());

            }
        }
        catch(Exception e)
        {

        }
    }
}
