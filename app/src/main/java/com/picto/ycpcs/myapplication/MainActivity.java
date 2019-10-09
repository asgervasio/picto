package com.picto.ycpcs.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    public void displayToastMsg(View v){
        toastMsg("Verify Credentials, then go to camera activity");
    }

    public void toastMsg(String msg){
        Context context = MainActivity.this;
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
     */

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

    //TODO: change placeholder to camera activity, also incorporate login controls
    public void gotoLogin(View view) {
        Toast toast = Toast.makeText(MainActivity.this, "Login Successfully hacked", Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent(this, PlaceholderActivity.class);
        startActivity(intent);
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast toast = new Toast(getApplicationContext());
                toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
    */

}
