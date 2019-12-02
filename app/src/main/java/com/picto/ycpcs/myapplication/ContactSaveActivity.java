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


public class ContactSaveActivity extends AppCompatActivity {


    ApplicationState applicationState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_save);

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        final EditText contactEditView = (EditText)findViewById(R.id.editTextContact);

        Button savebutton= (Button) findViewById(R.id.buttonSave);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MessageSendSaveActivity.this, "save clicked", Toast.LENGTH_SHORT).show();

                // save the contact to file and message list
                String contact = contactEditView.getText().toString();
                if(contact.length() == 0)
                {
                    showContactSavedDialogButtonClicked(view,"You must specify a caption before saving");
                    return;
                }
                // add the picture buffer to the message list
                applicationState.addContact(contact);

                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MainActivity"));
            }
        });

        Button trashbutton= (Button) findViewById(R.id.buttonTrash);
        trashbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MessageSendSaveActivity.this, "trash clicked", Toast.LENGTH_SHORT).show();

                // son't save the message
                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MainActivity"));
            }
        });


    }



    public void showContactSavedDialogButtonClicked(View view,String message) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save");
        builder.setMessage(message);

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
