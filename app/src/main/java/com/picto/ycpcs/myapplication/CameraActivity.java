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

    public static int count = 0;
    final int TAKE_PHOTO_CODE = 5;
    ImageView imageView ;
    ApplicationState applicationState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.imageView);
        Toast.makeText(CameraActivity.this, "Picto app started", Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());
        applicationState.setParentActivity(this);

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
            Toast.makeText(CameraActivity.this, "settings clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_messages)
        {
            Toast.makeText(CameraActivity.this, "messages clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MessagesListActivity.class));
            return true;
        }
        else if (id == R.id.action_take_picture) {
            //Toast.makeText(MainActivity.this, "Search clicked", Toast.LENGTH_LONG).show();
            takePicture();
            return true;
        }
        else if (id == R.id.action_connect) {
            Toast.makeText(CameraActivity.this, "New Connect clicked", Toast.LENGTH_LONG).show();
            //connect();
            ConnectOperation asyncTask=new ConnectOperation();
            asyncTask.execute("");
            return true;
        }
        else if (id == R.id.action_contacts)
        {
            startActivity(new Intent(this, ContactsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void takePicture()
    {
        //testCompression();

        //final String picturesPath = getApplicationContext().getFilesDir().getPath().toString() + "/snap_it_picFolder/"; // Files dir
        Toast.makeText(CameraActivity.this, "take picture clicked", Toast.LENGTH_LONG).show();

        //Toast.makeText(MainActivity.this, "folder: " + picturesPath, Toast.LENGTH_LONG).show();

        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Toast.makeText(MainActivity.this, "folder: " + path.getPath(), Toast.LENGTH_LONG).show();

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
                    applicationState.setLastPicture(bitmap);
                    int size = bitmap.getByteCount();
                    Toast.makeText(CameraActivity.this, "picture taken, size is: " + size + " bytes", Toast.LENGTH_LONG).show();
                    imageView.setImageBitmap(bitmap);

                    //saveBitmapToFile(bitmap);

                    startActivity(new Intent(this, MessageSendSaveActivity.class));
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

    public void saveBitmapToFile(final Bitmap bmp)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        runOnUiThread(new Runnable() {
            public void run() {
                //Toast.makeText(MainActivity.this, "jpeg compressed message", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Toast.makeText(CameraActivity.this, "PNG COMPRESSED Size " + byteArray.length, Toast.LENGTH_LONG).show();

                addMessage(byteArray);

                //DECODE
                Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                //Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
                byte[] bitmapdata = blob.toByteArray();

                Bitmap newbitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                int size = newbitmap.getByteCount();
                Toast.makeText(CameraActivity.this, "recovered picture size is: " + size + " bytes", Toast.LENGTH_LONG).show();
            }
        });



    }

    public void addMessage(byte[] content)
    {
        MessageListItem newItem = null;
        Date date = new Date();
        String filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + applicationState.getPictoMsgFilePrefix() + Long.toString(date.getTime());

        newItem = new MessageListItem("test contents " + Long.toString(date.getTime()),filename); // create a new history item
        newItem.content(content);

        byte[] resultBytes = applicationState.historyItemToBytes(newItem);

        //byte[] resultBytes = historyItemToBytes(newItem); // serialise
        //HistoryItem resultItem = bytesToHistoryItem(resultBytes);  //deserilize

        applicationState.addHistoryItem(newItem); // add this new scan to the history list
        applicationState.createHistoryFile(filename,resultBytes);
        //applicationState.createHistoryFile(newItem.filename(), newItem.name()); // save this history information file


    }

    void testCompression()
    {
        try {
            // Encode a String into bytes
            String inputString = "blahblahblah000000000000000000000000000000";
            byte[] input = inputString.getBytes("UTF-8");

            // Compress the bytes
            byte[] output = new byte[100];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();

            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(output, 0, compressedDataLength);
            byte[] result = new byte[100];
            int resultLength = decompresser.inflate(result);
            decompresser.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
        } catch(java.io.UnsupportedEncodingException ex) {
            // handle
        } catch (java.util.zip.DataFormatException ex) {
            // handle
        }
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

    private class ConnectOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            //connect();  // old code

            PictoClient client = null;
            int msgCount = 1;

            // new thread for a client
            client = new PictoClient();
            client.connect("192.168.1.8");
            client.login("bob", "password");

            toastOnGUI("Picto Client Main. connect ");
            applicationState.setPictoClient(client);


            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {


            //TextView txt = (TextView) findViewById(R.id.output);
            //txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    public void toastOnGUI(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                // use data here
                Toast.makeText(CameraActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
