package com.picto.ycpcs.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    ApplicationState applicationState = null;
    EditText editText_password;
    EditText  editText_username;
    TextView textview_status;
    CheckBox checkBox_CreateAccount;
    String username = "";
    String password = "";
    boolean createAccountChecked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        editText_password = (EditText)findViewById(R.id.editText_password);
        //editText_password.setFilters(new InputFilter[] { filter });

        editText_username = (EditText)findViewById(R.id.editText_username);

        textview_status = (TextView)findViewById(R.id.textViewStatus);
        editText_username.setText(applicationState.username());
        //editText_username.setEnabled(false);
        editText_username.setKeyListener(null); // set key listener to null to make it readonly
        editText_password.setText(applicationState.password());

        if(applicationState.username().length() > 0) {
            setTitle("Picto (" + applicationState.username() + ")");
        }

        if(applicationState.debugEnabled() == true)
        {
            textview_status.setVisibility(View.VISIBLE);
        }
        else
        {
            textview_status.setVisibility(View.GONE);
        }
        //applicationState.lastStatusMessage = "test Messsage";
        if(applicationState.lastStatusMessage.length() > 0)
        {
            textview_status.setText(applicationState.lastStatusMessage);
            //applicationState.addStatusMessage(" ,clear ");

        }

        // checkBox_CreateAccount
        checkBox_CreateAccount = (CheckBox)findViewById(R.id.checkBox_CreateAccount);

        Button button= (Button) findViewById(R.id.button_login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "login clicked", Toast.LENGTH_SHORT).show();

                // get username and IP address
                password = editText_password.getText().toString();
                username = (editText_username.getText().toString());
                if(username.length() == 0)
                {
                    showLoginDialogButtonClicked(view,"You must specify a username in settings before login");
                    return;
                }


                createAccountChecked = checkBox_CreateAccount.isChecked();

                LoginToServerOperation asyncTask=new LoginToServerOperation();
                asyncTask.execute("");


            }
        });


    }

    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        //startActivity(new Intent().setClassName("com.cs381.picto", "com.cs381.picto.MainActivity"));
        startActivity(new Intent().setClassName(applicationState.picto_package_name,  applicationState.picto_package_name + ".MainActivity"));
    }

    private class LoginToServerOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            PictoClient client = null;
            //int msgCount = 1;

            // new thread for a client
            client = new PictoClient();

            if(client.connect(applicationState.ipAddress()) == false)
            {
                toastOnGUI("Picto Client connect failed ");
            }
            else
            {
                toastOnGUI("Picto Client connect SUCCESS");
                int status = client.login(username, password, createAccountChecked);

                if(status== CommandHeader.STATUS_SUCCESS)
                {

                    toastOnGUI("Picto Client login SUCCESS");
                    if(applicationState.getPictoClient() != null)
                    {
                        // if there is a previous client connection to server, disconnect
                        applicationState.getPictoClient().disconnect(false);
                    }
                    applicationState.setPictoClient(client);
                    applicationState.username(username);
                    applicationState.password(password);
                    applicationState.loggedIn(true);
                    startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MainActivity"));


                }
                else
                {
                    toastOnGUI("Picto Client login failed " + getLoginStatusString(status));
                }
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

    public static String getLoginStatusString(int status)
    {
        String rv = "";

        switch(status)
        {
            case CommandHeader.STATUS_SUCCESS:
                rv = "SUCCESS";
                break;
            case CommandHeader.STATUS_ERROR_USER_NOT_FOUND:
                rv = "USER NOT FOUND";
                break;
            case CommandHeader.STATUS_ERROR_LOGIN_USERNAME_USED:
                rv = "USERNAME USED";
                break;
            case CommandHeader.STATUS_ERROR_UNAUTHORIZED:
                rv = "UNAUTHORIZED";
                break;
            default:
                rv = "UNKNOWN = " + Integer.toString(status);

                break;
        }
        return rv;

    }
    public void toastOnGUI(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                // use data here
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showLoginDialogButtonClicked(View view,String message) {

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
