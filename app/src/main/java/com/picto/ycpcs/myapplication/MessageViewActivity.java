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

public class MessageViewActivity extends AppCompatActivity {

    ImageView imageView ;

    ApplicationState applicationState = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        MessageListItem theMessageItem = null;

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        EditText editView = (EditText)findViewById(R.id.editTextHistoryView);
        imageView = (ImageView)findViewById(R.id.imageViewHistory);

        theMessageItem = applicationState.historyToView();

        if(theMessageItem == null) {
            DisplayAlertOKDialog("A message must be selected to View it!");
            return;
        }

        editView.setText(theMessageItem.toString());
        byte[] pngImage = theMessageItem.content();

        //DECODE PNG to Bitmap and display image
        pngToBitmap(pngImage);

    }

    public void  pngToBitmap(final byte[] pngImage)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        runOnUiThread(new Runnable() {
            public void run() {
                //DECODE
                Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(pngImage,0,pngImage.length);
                //Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
                byte[] bitmapdata = blob.toByteArray();

                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                imageView.setImageBitmap(bitmap);

                applicationState.historyToView(null);
            }
        });



    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

        startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.MessagesListActivity"));
    }

    void DisplayAlertOKDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication","com.picto.ycpcs.myapplication.MessagesListActivity"));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
