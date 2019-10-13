package com.picto.ycpcs.myapplication;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    EditText nameText, emailText;
    List<Contact> contacts = new ArrayList<Contact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        nameText = (EditText) findViewById(R.id.ContactName);
        emailText = (EditText) findViewById(R.id.Email);
        final Button addBtn = (Button)  findViewById(R.id.button);
        TabHost tabHost = (TabHost) findViewById(R.id.TabHost);

        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.ListTab);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("add");
        tabSpec.setContent(R.id.AddTab);
        tabSpec.setIndicator("Add");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("delete");
        tabSpec.setContent(R.id.DeleteTab);
        tabSpec.setIndicator("Delete");
        tabHost.addTab(tabSpec);

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Your contact has been created.", Toast.LENGTH_SHORT).show();
            }
        });


        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addBtn.setEnabled(!nameText.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private class ContactListAdaptor extends ArrayAdapter<Contact> {
        public ContactListAdaptor() {
            super (ContactsActivity.this, R.layout.listview_item, contacts);
        }
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
        /*int id = item.getItemId();
        if (id == R.id.action_contact_list) {
            addContact();
            deleteContact();
            return true;
        }*/

        return super.onOptionsItemSelected(item);

    }

    public void addContact() {

    }

    public void deleteContact() {

    }
}
