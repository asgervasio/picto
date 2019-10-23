package com.picto.ycpcs.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailText, passwordText;
    String email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);
        email = emailText.toString();
        password = passwordText.toString();
        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void createAccount(String email, String password){
        //TODO Validate credentials are correct
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User Authenticated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String password){
        //TODO Validate credentials are correct
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User Authenticated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void logIn(View v){
        signIn(email, password);
    }

    public void createAcct(View v){
        createAccount(email, password);
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

/*
    public void logInOld(View view) {
        if(isValidInput(emailText.getText().toString(), passwordText.getText().toString())){
            if(emailText.getText().toString().equals("admin") && passwordText.getText().toString().equals("admin")){
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
*/

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
