package com.picto.ycpcs.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity
{

    final int TAKE_PHOTO_CODE = 5;
    ImageView imageView ;
    ApplicationState applicationState = null;
    static final private int MENU_ABOUT = Menu.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.imageView);
        //Toast.makeText(MainActivity.this, "Picto app started", Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        FloatingActionButton fabmessages = (FloatingActionButton) findViewById(R.id.fab_message);
        fabmessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MessagesListActivity"));

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        FloatingActionButton fabpictures = (FloatingActionButton) findViewById(R.id.fab_picture);
        fabpictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".PictureListActivity"));

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        FloatingActionButton fabsettings = (FloatingActionButton) findViewById(R.id.fab_settings);
        fabsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".SettingsActivity"));

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        FloatingActionButton fabcontacts = (FloatingActionButton) findViewById(R.id.fab_contacts);
        fabcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactListActivity"));

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        FloatingActionButton fablogin = (FloatingActionButton) findViewById(R.id.fab_login_server);
        fablogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".LoginActivity"));

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());
        //applicationState.setParentActivity(this);

        loadSettings(); // load user settings

        if(applicationState.username().length() > 0) {
            setTitle("Picto (" + applicationState.username() + ")");
        }

    }

    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        //startActivity(new Intent().setClassName("com.cs381.picto", "com.cs381.picto.MainActivity"));
        startActivity(new Intent().setClassName(applicationState.picto_package_name,  applicationState.picto_package_name + ".MainActivity"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ABOUT, Menu.NONE, "About");


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {

            case (MENU_ABOUT):
            {

                DisplayAlertOKDialog("Picto version 1.0\n" +
                        "Developed for CS 381\n\n" +
                        "Development team:\n\n" );

                return true;
            }


        }
        return false;
    }

/*
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
            //Toast.makeText(MainActivity.this, "settings clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_messages)
        {
            //Toast.makeText(MainActivity.this, "messages clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MessagesListActivity.class));
            return true;
        }
        else if (id == R.id.action_pictures)
        {
            //Toast.makeText(MainActivity.this, "messages clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, PictureListActivity.class));
            return true;
        }
        else if (id == R.id.action_contacts)
        {
            startActivity(new Intent(this, ContactListActivity.class));
            return true;
        }
        // action_pictures
        else if (id == R.id.action_take_picture) {
            //Toast.makeText(MainActivity.this, "Search clicked", Toast.LENGTH_LONG).show();
            takePicture();
            return true;
        }
        else if (id == R.id.action_login)
        {
            //Toast.makeText(MainActivity.this, "messages clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

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

    void DisplayAlertOKDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
