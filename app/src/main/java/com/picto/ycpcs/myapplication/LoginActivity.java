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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailText, passwordText;
    String email, password;
    private FirebaseAuth mAuth;
    private static final String TAG = "CustomAuthActivity";
    ApplicationState applicationState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);

        // Buttons
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.createAccount).setOnClickListener(this);

        email = emailText.toString();
        password = passwordText.toString();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view){
        int i = view.getId();
        if (i == R.id.createAccount) {
            createAccount(emailText.getText().toString(), passwordText.getText().toString());
        } else if (i == R.id.login) {
            signIn(emailText.getText().toString(), passwordText.getText().toString());
        }
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void createAccount(String email, String password){
        //TODO Validate credentials are correct
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User created!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void signIn(String email, String password) {
        //TODO Validate credentials are correct
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User Authenticated",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
                            startActivity(intent);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        //TODO change UI when called

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
