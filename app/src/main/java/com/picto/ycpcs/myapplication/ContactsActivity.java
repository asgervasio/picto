package com.picto.ycpcs.myapplication;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity { // issue here some where
    EditText nameText, emailText;
    ArrayList<Contact> contacts = new ArrayList<Contact>();
    ListView contactListView;
    static final private int MENU_ADD = Menu.FIRST;
    static final private int MENU_DELETE = Menu.FIRST+1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        nameText = (EditText) findViewById(R.id.ContactName);
        emailText = (EditText) findViewById(R.id.Email);
        contactListView = (ListView)  findViewById(R.id.listView); //editListView in layout xml
        final Button addBtn = (Button)  findViewById(R.id.button);
       /* TabHost tabHost = (TabHost) findViewById(R.id.TabHost);

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
        tabHost.addTab(tabSpec);*/ // Get rid of tab stuff

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addContact(nameText.getText().toString(), emailText.getText().toString());
                //populateList(); // issue here
                Toast.makeText(getApplicationContext(), nameText.getText().toString() + "has been added to your contacts!", Toast.LENGTH_SHORT).show();
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

    private void populateList() {
        ArrayAdapter<Contact> adapter = new ContactListAdaptor();
        contactListView.setAdapter(adapter); // issue here
    }
    private class ContactListAdaptor extends ArrayAdapter<Contact> {
        public ContactListAdaptor() {
            super (ContactsActivity.this, R.layout.listview_item, contacts);
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            }

            Contact currentContact = contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.ContactName);
            name.setText(currentContact.getName());
            TextView email = (TextView) view.findViewById(R.id.Email);
            email.setText(currentContact.getEmail());

            return view;
        }
    }

    private void addContact(String name, String email) {
        contacts.add(new Contact(name, email));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ADD, Menu.NONE, "Add");
        menu.add(0, MENU_DELETE, Menu.NONE, "Delete");

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



    public void deleteContact() {

    }
}
