package com.picto.ycpcs.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    ApplicationState applicationState = null;
    EditText  editText_IP_Address;
    EditText  editText_username;
    CheckBox checkBox_displayDebug;
    SettingsActivity settingsActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());
        settingsActivity =  this;

        // input filter for IP address
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };

        editText_IP_Address = (EditText)findViewById(R.id.editText_IP_Address);
        editText_IP_Address.setFilters(new InputFilter[] { filter });

        editText_username = (EditText)findViewById(R.id.editText_username);

        checkBox_displayDebug = (CheckBox)findViewById(R.id.checkBox_displayDebug);
        checkBox_displayDebug.setChecked(applicationState.debugEnabled());


        Button button= (Button) findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SettingsActivity.this, "save clicked", Toast.LENGTH_SHORT).show();

                if(editText_username.getText().toString().length() == 0)
                {
                    showSettingsSavedDialogButtonClicked(view,"You must specify a username");
                    return;
                }
                if(editText_IP_Address.getText().toString().length() == 0)
                {
                    showSettingsSavedDialogButtonClicked(view,"You must specify a server IP address");
                    return;
                }
               // get username and IP address
                applicationState.ipAddress(editText_IP_Address.getText().toString());
                applicationState.username(editText_username.getText().toString());
                applicationState.debugEnabled(checkBox_displayDebug.isChecked()); // this isn't saved, it's used during session.


                PictoSettings settings = new PictoSettings(editText_IP_Address.getText().toString(),editText_username.getText().toString());
                byte[] settingsBytes = settings.pictoSettingsToBytes(settings);
                // export to byte array
                // save byte array to file
                String filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + applicationState.getPictoSettingsFilename();

                applicationState.createFile(filename,settingsBytes);

                showSettingsSavedDialogButtonClicked(view,"Settings Saved");
            }
        });

        Button buttonCleanFiles= (Button) findViewById(R.id.button_clean_files);
        buttonCleanFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(settingsActivity);
                builder.setMessage("Are you sure you want to delete all messages and picture file(s)?\n")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                applicationState.CleanupFiles();
                                showSettingsSavedDialogButtonClicked(view,"Messages and pictures deleted");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();

            }
        });


        try {
            String settings_filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + applicationState.getPictoSettingsFilename();
            File file = new File(settings_filename);

            if (file.exists() == true) {
                // read the contents of the file
                byte[] content = applicationState.getByteArrayFromFile(settings_filename);

                PictoSettings newItem = PictoSettings.bytesToPictoSettings(content);
                editText_IP_Address.setText(newItem.ipAddress());
                editText_username.setText(newItem.username());

            }
        }
        catch(Exception e)
        {

        }
    }

    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MainActivity"));
    }

    public void showSettingsSavedDialogButtonClicked(View view,String message) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings");
        builder.setMessage(message);

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
