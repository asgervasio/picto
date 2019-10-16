package com.picto.ycpcs.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        if(isValidInput(username.getText().toString(), password.getText().toString())){
            if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                //correct password
                Toast toast = Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
            }
            else{
                //wrong password
                Toast toast = Toast.makeText(LoginActivity.this, "Incorrect credentials", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            //not valid input
            Toast toast = Toast.makeText(LoginActivity.this, "Invalid input", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static boolean isValidInput(String user, String pword){

        if(user.isEmpty() || pword.isEmpty()){
            return false;
        }
        return true;
    }

    public static boolean validAccountCredentials(String username, String email){
        if(accountExists(username, email)){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean accountExists(String username, String email){
        //TODO update when user account objects are checkable
        return false;
    }

    public static boolean isAdminLogin(String user, String pword){
        if(user == "admin" && pword == "admin"){
            return true;
        }
        else{
            return false;
        }
    }

}
