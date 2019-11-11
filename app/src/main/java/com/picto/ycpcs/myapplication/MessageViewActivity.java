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

import static com.picto.ycpcs.myapplication.MessagesListActivity.messageListView;
import static java.lang.Thread.sleep;

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

        theMessageItem = applicationState.messageToView();

        if(theMessageItem == null) {
            DisplayAlertOKDialog("A message must be selected to View it!");
            return;
        }

        editView.setText(theMessageItem.toString());
        byte[] pngImage = theMessageItem.content();

        ////viewTimedImage(pngImage,theMessageItem);
        displayMessage(pngImage,theMessageItem);
        //pngToBitmapOld(pngImage);
        /*
        //DECODE PNG to Bitmap and display image
        pngToBitmap(pngImage,theMessageItem); // pngToBitmap(pngImage);


        delayAndDelete(theMessageItem);
        //DisplayAlertOKDialog("message timeout = " + theMessageItem.contentSettings());
        */

    }
    /*
    public void  viewTimedImage(final byte[] pngImage,final MessageListItem theMessageItem)
    {
        Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(pngImage,0,pngImage.length);
        if(compressed_bitmap != null)
        {
            //Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 , blob);
            byte[] bitmapdata = blob.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

            imageView.setImageBitmap(bitmap);

            applicationState.messageToView(null);
            delayAndDelete(theMessageItem,true);
        }
        else
        {
            DisplayAlertOKDialog("Message restore error. ");
            applicationState.messageToView(null);
            delayAndDelete(theMessageItem,false);


        }

    }

    public void  pngToBitmapOld(final byte[] pngImage)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        //runOnUiThread(new Runnable() {
        //    public void run() {
                //DECODE
                Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(pngImage,0,pngImage.length);
                if(compressed_bitmap != null)
                {
                    //Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 , blob);
                    byte[] bitmapdata = blob.toByteArray();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                    imageView.setImageBitmap(bitmap);
                    applicationState.messageToView(null);
                    //delayAndDelete(theMessageItem,true); // enable delay
                }
                else
                {
                    applicationState.messageToView(null);
                    //delayAndDelete(theMessageItem,false);  // disable delay
                }


        //    }
       // });



    }
*/
    public void  displayMessage(final byte[] pngImage,final MessageListItem theMessageItem)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        //runOnUiThread(new Runnable() {
         //   public void run() {
                //DECODE
                Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(pngImage,0,pngImage.length);
                if(compressed_bitmap != null) {
                    //Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
                    byte[] bitmapdata = blob.toByteArray();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                    imageView.setImageBitmap(bitmap);
                    applicationState.messageToView(null);

                    ////delayAndDeleteOld(theMessageItem,true); // enable delay

                    delayAndDelete(theMessageItem,true);
                }
                else
                {
                    DisplayAlertOKDialog("message deleted. picture image could not be recovered, caption: " + theMessageItem.name());
                    applicationState.messageToView(null);
                    ////delayAndDeleteOld(theMessageItem,false);  // disable delay

                    //delayAndDelete(theMessageItem,false);
                    applicationState.messageToDelete(theMessageItem.filename());



                }


          //  }
       // });



    }



    void delayAndDelete( MessageListItem theMessageItem,boolean allowDelay)
    {
        // class that creates a thread which only runs once and terminates
        class OneTimeTask implements Runnable
        {
            MessageListItem theMessageItem;
            boolean allowDelay;
            OneTimeTask(MessageListItem theMessageItem,boolean allowDelay)
            {
                this.theMessageItem = theMessageItem;
                this.allowDelay = allowDelay;
            }
            public void run()
            {
                if(allowDelay)
                {
                    int sleeptime = Integer.parseInt(theMessageItem.contentSettings()) * 1000;
                    if ((sleeptime < 1000) || (sleeptime > 10000)) {
                        sleeptime = 5000;
                    }
                    try
                    {
                        sleep(sleeptime); // the sleep the amount of time the user can view the message
                    }
                    catch(Exception e)
                    {

                    }
                }
                // delete the message
                applicationState.messageToDelete(theMessageItem.filename());
                // switch to the message list activity
                startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication","com.picto.ycpcs.myapplication.MessagesListActivity"));

            }
        }
        Thread t = new Thread(new OneTimeTask(theMessageItem,allowDelay));
        t.start();
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
