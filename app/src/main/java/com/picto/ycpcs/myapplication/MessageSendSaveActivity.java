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
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static com.picto.ycpcs.myapplication.R.id.imageView;

public class MessageSendSaveActivity extends AppCompatActivity {

    ImageView imageView ;

    ApplicationState applicationState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send_save);

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        final EditText captionEditView = (EditText)findViewById(R.id.editTextCaption);
        final EditText toAddressEditView = (EditText)findViewById(R.id.editText_toAddress);
        final Spinner spinnerTimeout  = (Spinner)findViewById(R.id.spinnerTimeout);
        spinnerTimeout.setSelection(1);
        //captionEditView.setEnabled(false);
        captionEditView.setKeyListener(null); // set key listener to null to make it readonly
        imageView = (ImageView)findViewById(R.id.imageViewSendSave);

        Bitmap bitmap = applicationState.getLastPicture();
        MessageListItem lastMsg = applicationState.getLastMessage();
        if(lastMsg != null)
        {
            captionEditView.setText(lastMsg.name());
        }

        imageView.setImageBitmap(bitmap);

        if(applicationState.username().length() > 0) {
            setTitle("Picto (" + applicationState.username() + ")");
        }



        Button sendbutton= (Button) findViewById(R.id.buttonSend);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MessageSendSaveActivity.this, "send clicked", Toast.LENGTH_SHORT).show();

                // send the message to the destination
                // create client object
                // do connect
                // send message
                // disconnect
                // save the message to file and message list
                if(applicationState.loggedIn() == true)
                {
                    Bitmap bmp = applicationState.getLastPicture();
                    String caption = captionEditView.getText().toString();
                    String toAddress = toAddressEditView.getText().toString().toLowerCase();
                    String contentSettings = String.valueOf(spinnerTimeout.getSelectedItem());
                    if(caption.length() == 0)
                    {
                        showSettingsSavedDialogButtonClicked(view,"You must specify a caption before sending");
                        return;
                    }
                    if(toAddress.length() == 0)
                    {
                        showSettingsSavedDialogButtonClicked(view,"You must specify an address before sending");
                        return;
                    }
                    saveBitmapToFile(bmp, caption,contentSettings);
                    applicationState.toAddress(toAddress);
                    applicationState.caption(caption);
                    applicationState.contentSettings(contentSettings); // hardcode to 5 second timeout

                    SendMessageOperation asyncTask = new SendMessageOperation();
                    asyncTask.execute("");

                    startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.CameraActivity"));
                }
                else
                {
                    showSettingsSavedDialogButtonClicked(view,"You must Login before sending");
                    //Toast.makeText(MessageSendSaveActivity.this, "Login before sending", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void saveBitmapToFile(final Bitmap bmp,final String caption, final String contentSettings)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        runOnUiThread(new Runnable() {
            public void run() {
                //byte[] key = applicationState.AES_key_128;
                byte[] key = applicationState.makeEncryptKey(applicationState.username(),caption);
                String byteString = applicationState.bytesToHex(key);
                applicationState.addStatusMessage(",saveBitmapToFile username =" + applicationState.username() + ", caption =" + caption + ", key = " + byteString);

                try
                {
                    byte[] byteArray = applicationState.encryptBitmap(bmp, key);
                    // store the last compressed picture in global memeory
                    applicationState.setLastPictureCompressed(byteArray);
                }
                catch(Exception e)
                {

                }

            }
        });



    }

    private class SendMessageOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            PictoClient client = applicationState.getPictoClient();
            //int msgCount = 1;

            String caption = applicationState.caption();
            String toAddress = applicationState.toAddress();
            String contentSettings = applicationState.contentSettings();

            //PictoMessage message = new PictoMessage(applicationState.username(), toAddress,caption,"message" + String.valueOf(msgCount) + ".txt");
            PictoMessage message = new PictoMessage(applicationState.username(), toAddress,caption,contentSettings);

            byte[] content = applicationState.getLastPictureCompressed();

            message.content(content);
            //msgCount++;

            if(client.sendMessageToServer(message) == true)
            {
                //System.out.println("Picto Client Main. sendMessageToServer() true ");
                //Toast.makeText(MessageSendSaveActivity.this, "Picto Client Main. sendMessageToServer() true ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //System.out.println("Picto Client Main. sendMessageToServer() false ");
                //Toast.makeText(MessageSendSaveActivity.this, "Picto Client Main. sendMessageToServer() false ", Toast.LENGTH_SHORT).show();
            }


            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {



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
                Toast.makeText(MessageSendSaveActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showSettingsSavedDialogButtonClicked(View view,String message) {

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
