package com.picto.ycpcs.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PictureViewActivity extends AppCompatActivity {

    ImageView imageView ;

    ApplicationState applicationState = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        MessageListItem theMessageItem = null;

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        EditText editView = (EditText)findViewById(R.id.editTextPictureView);
        imageView = (ImageView)findViewById(R.id.imageViewPicture);

        theMessageItem = applicationState.pictureToView();

        if(theMessageItem == null) {
            DisplayAlertOKDialog("A picture must be selected to View it!");
            return;
        }

        editView.setText(theMessageItem.toString());
        byte[] pngImage = theMessageItem.content();

        //DECODE PNG to Bitmap and display image
        pngToBitmap(pngImage, theMessageItem);

    }

    public void  pngToBitmap(final byte[] pngImage, final MessageListItem theItem)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        runOnUiThread(new Runnable() {
            public void run() {

                //byte[] key = applicationState.AES_key_128;
                byte[] key = applicationState.makeEncryptKey(applicationState.username(),theItem.name());
                String byteString = applicationState.bytesToHex(key);
                applicationState.addStatusMessage(",pngToBitmap username =" + applicationState.username() + ", caption =" + theItem.name() + ", key = " + byteString);

                try {
                    Bitmap bitmap = applicationState.decryptBitmap(pngImage, key);

                    imageView.setImageBitmap(bitmap);

                    applicationState.pictureToView(null);
                }
                catch(Exception e)
                {

                }

            }
        });



    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

        startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".PictureListActivity"));
    }

    void DisplayAlertOKDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".PictureListActivity"));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
