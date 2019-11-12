package com.picto.ycpcs.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import static com.picto.ycpcs.myapplication.R.id.imageView;

public class PictureSaveActivity extends AppCompatActivity {

    ImageView imageView ;

    ApplicationState applicationState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_save);

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        final EditText captionEditView = (EditText)findViewById(R.id.editTextCaption);
        //final EditText toAddressEditView = (EditText)findViewById(R.id.editText_toAddress);

        imageView = (ImageView)findViewById(R.id.imageViewSendSave);

        Bitmap bitmap = applicationState.getLastPicture();

        imageView.setImageBitmap(bitmap);




        Button savebutton= (Button) findViewById(R.id.buttonSave);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MessageSendSaveActivity.this, "save clicked", Toast.LENGTH_SHORT).show();

                // save the message to file and message list
                Bitmap bmp = applicationState.getLastPicture();
                String caption = captionEditView.getText().toString();
                if(caption.length() == 0)
                {
                    showPictureSavedDialogButtonClicked(view,"You must specify a caption before saving");
                    return;
                }
                saveBitmapToFile(bmp,caption);
                startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.CameraActivity"));
            }
        });

        Button trashbutton= (Button) findViewById(R.id.buttonTrash);
        trashbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MessageSendSaveActivity.this, "trash clicked", Toast.LENGTH_SHORT).show();

                // son't save the message
                startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.CameraActivity"));
            }
        });


    }

    public void saveBitmapToFile(final Bitmap bmp,final String caption)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        runOnUiThread(new Runnable() {
            public void run() {

                //byte[] key = applicationState.AES_key_128;
                byte[] key = applicationState.makeEncryptKey(applicationState.username(),caption);
                String byteString = applicationState.bytesToHex(key);
                applicationState.addStatusMessage(",saveBitmapToFile username =" + applicationState.username() + ", caption =" + caption + ", key = " + byteString);

                byte[] byteArray;
                try
                {
                    byteArray = applicationState.encryptBitmap(bmp, key);
                    // store the last compressed picture in global memeory
                    //applicationState.setLastPictureCompressed(byteArray);

                    // store the last compressed picture in global memeory
                    applicationState.setLastPictureCompressed(byteArray);

                    // add the picture buffer to the message list
                    applicationState.addPicture(byteArray,caption);
                }
                catch(Exception e)
                {

                }
  /*  old
                // compress the bimap image to a PNG byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                //Toast.makeText(MessageSendSaveActivity.this, "PNG COMPRESSED Size " + byteArray.length, Toast.LENGTH_LONG).show();

                // store the last compressed picture in global memeory
                applicationState.setLastPictureCompressed(byteArray);

                // add the picture buffer to the message list
                applicationState.addPicture(byteArray,caption);
 */



                /*
                // encrypt
                byte[] input = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56 };
                // 128 bit key
                byte[] key = new byte[] { 34, 35, 36, 37, 37, 37, 67, 68, 34, 35, 36, 37, 37, 37, 67, 68 };
                try
                {

                    toastOnGUI("original png buffer size = " + Integer.toString(byteArray.length));
                    //toastOnGUI(java.util.Arrays.toString(input));

                    byte[] encryptedbuf = applicationState.encrypt(byteArray, key);
                    toastOnGUI("encrypted png buffer size = " + Integer.toString(encryptedbuf.length));
                    //toastOnGUI(java.util.Arrays.toString(encryptedbuf));

                    byte[] decryptedbuf = applicationState.decrypt(encryptedbuf, key);
                    toastOnGUI("decrypted png buffer size = " + Integer.toString(decryptedbuf.length));
                    //toastOnGUI(java.util.Arrays.toString(decryptedbuf));
                }
                catch(Exception e)
                {

                }
                // encrypt
*/


            }
        });



    }


    public void toastOnGUI(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                // use data here
                Toast.makeText(PictureSaveActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPictureSavedDialogButtonClicked(View view,String message) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send");
        builder.setMessage(message);

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
