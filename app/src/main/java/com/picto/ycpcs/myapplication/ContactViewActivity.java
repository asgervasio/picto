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

public class ContactViewActivity extends AppCompatActivity {

    ApplicationState applicationState = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        MessageListItem theMessageItem = null;

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        EditText editView = (EditText)findViewById(R.id.editTextContactView);

        theMessageItem = applicationState.contactToView();

        if(theMessageItem == null) {
            DisplayAlertOKDialog("A contact must be selected to View it!");
            return;
        }

        editView.setText(theMessageItem.toString());


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

        startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactListActivity"));
    }

    void DisplayAlertOKDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactListActivity"));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
