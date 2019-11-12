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
    EditText editView;

    ApplicationState applicationState = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        MessageListItem theMessageItem = null;

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        editView = (EditText)findViewById(R.id.editTextHistoryView);
        imageView = (ImageView)findViewById(R.id.imageViewHistory);

        theMessageItem = applicationState.messageToView();

        if(theMessageItem == null) {
            DisplayAlertOKDialog("A message must be selected to View it!");
            return;
        }

        editView.setText(theMessageItem.toString() + " (timeout " + theMessageItem.contentSettings() + " sec)");
        byte[] pngImage = theMessageItem.content();

        ////viewTimedImage(pngImage,theMessageItem);
        displayMessage(pngImage,theMessageItem);
    }
    public void  displayMessage(final byte[] pngImage,final MessageListItem theMessageItem)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        //runOnUiThread(new Runnable() {
         //   public void run() {

        //byte[] key = applicationState.AES_key_128;
        byte[] key = applicationState.makeEncryptKey(theMessageItem.fromAddress(),theMessageItem.name());
        String byteString = applicationState.bytesToHex(key);
        applicationState.addStatusMessage(",displayMessage username =" + theMessageItem.fromAddress() + ", caption =" + theMessageItem.name() + ", key = " + byteString);

        try {
            Bitmap bitmap = applicationState.decryptBitmap(pngImage, key);

            imageView.setImageBitmap(bitmap);
            applicationState.messageToView(null);

            delayAndDelete(this,theMessageItem,true);
        }
        catch(Exception e)
        {
            DisplayAlertOKDialog("message deleted. picture image could not be recovered, caption: " + theMessageItem.name());
            applicationState.messageToView(null);

            applicationState.messageToDelete(theMessageItem.filename());
        }
    }



    void delayAndDelete(MessageViewActivity guiActivity, MessageListItem theMessageItem,boolean allowDelay)
    {
        // class that creates a thread which only runs once and terminates
        class OneTimeTask implements Runnable
        {
            MessageViewActivity guiActivity;
            MessageListItem theMessageItem;
            boolean allowDelay;
            OneTimeTask(MessageViewActivity guiActivity,MessageListItem theMessageItem,boolean allowDelay)
            {
                this.guiActivity = guiActivity;
                this.theMessageItem = theMessageItem;
                this.allowDelay = allowDelay;
            }
            public void run()
            {
                if(allowDelay)
                {
                    /*
                    int sleeptime = Integer.parseInt(theMessageItem.contentSettings());
                    if ((sleeptime < 1) || (sleeptime > 10)) {
                        sleeptime = 5;
                    }
                    */

                    int sleeptime = Integer.parseInt(theMessageItem.contentSettings()) * 1000;
                    if ((sleeptime < 1000) || (sleeptime > 10000)) {
                        sleeptime = 5000;
                    }

                    try
                    {
                        sleep(sleeptime); // the sleep the amount of time the user can view the message
                        /*
                        for(int sleepCount = 0; sleepCount < sleeptime; sleepCount++) {
                            guiActivity.editView.setText(theMessageItem.toString() + " (timeout " + Integer.toString(sleeptime - sleepCount) + " sec)");
                            sleep(1000); // the sleep the amount of time the user can view the message

                        }
                        */
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
        Thread t = new Thread(new OneTimeTask(guiActivity,theMessageItem,allowDelay));
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
