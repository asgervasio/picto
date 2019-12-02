package com.picto.ycpcs.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class ContactListActivity extends AppCompatActivity {

    static final private int MENU_VIEW = Menu.FIRST;
    static final private int MENU_CREATE = Menu.FIRST+1;
    static final private int MENU_DELETE = Menu.FIRST+2;


    //static boolean activityRunning = true;

    static ListView contactListView;
    static ArrayAdapter<MessageListItem> contactArrayAdapter;
    ArrayList<MessageListItem> contactCache = new ArrayList<MessageListItem>();

    ApplicationState applicationState = null;
    String filesSelected;
    Intent intent;
    int selectedCount;
    ContactListActivity ourself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ourself = this;

        contactListView = (ListView)this.findViewById(R.id.listViewContact);
        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        contactListView.setItemsCanFocus(false);
        contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // CHOICE_MODE_MULTIPLE

        FloatingActionButton fabtrash = (FloatingActionButton) findViewById(R.id.fab_trash);
        fabtrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCount = getContactSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A contact item must be selected to Delete it!");
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ourself);
                    builder.setMessage("Are you sure you want to delete the selected contact(s)?\n")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DeleteContactItem();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        FloatingActionButton fabview = (FloatingActionButton) findViewById(R.id.fab_view);
        fabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCount = getContactSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A contact item must be selected to View it!");
                }
                else if(selectedCount > 1)
                {
                    // not allowed dialog
                    DisplayAlertOKDialog("You have multiples contact items selected. Only single contact view is supported!");
                }
                else {
                    SetContactToView();
                    intent = new Intent();
                    intent.setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactViewActivity");
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton fabcreate = (FloatingActionButton) findViewById(R.id.fab_create);
        fabcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent();
                intent.setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactSaveActivity");
                startActivity(intent);
            }
        });

        if(applicationState.username().length() > 0) {
            setTitle("Picto (" + applicationState.username() + ")");
        }
        applicationState.LoadContactList();

        Vector contactList = applicationState.getContactList();
        int sizeToRead = contactList.size();

        synchronized(contactList)
        {
            for(int x= 0; x<sizeToRead; x++)
                contactCache.add((MessageListItem)contactList.get(x) );

        }

        int layoutID = android.R.layout.simple_list_item_multiple_choice;
        contactArrayAdapter = new ArrayAdapter<MessageListItem>(this, layoutID , contactCache);
        contactListView.setAdapter(contactArrayAdapter);

        //activityRunning = true;

        if(sizeToRead == 0)
        {
            DisplayAlertOKDialog("Contact list is empty.");

        }

    }


    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        startActivity(new Intent().setClassName(applicationState.picto_package_name,  applicationState.picto_package_name + ".MainActivity"));
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_VIEW, Menu.NONE, "View");
        menu.add(0, MENU_CREATE, Menu.NONE, "Create");
        menu.add(0, MENU_DELETE, Menu.NONE, "Delete");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Intent intent;
        int selectedCount;
        switch (item.getItemId())
        {

            case (MENU_VIEW):
            {

                selectedCount = getContactSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A contact item must be selected to View it!");
                }
                else if(selectedCount > 1)
                {
                    // not allowed dialog
                    DisplayAlertOKDialog("You have multiples contact items selected. Only single contact view is supported!");
                }
                else {
                    SetContactToView();
                    intent = new Intent();
                    intent.setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactViewActivity");
                    startActivity(intent);
                }
                return true;
            }

            case (MENU_CREATE):
            {
                intent = new Intent();
                intent.setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".ContactSaveActivity");
                startActivity(intent);

                return true;
            }

            case (MENU_DELETE):
            {

                selectedCount = getContactSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A contact item must be selected to Delete it!");
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to delete the selected contact(s)?\n")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DeleteContactItem();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }


                return true;
            }
        }
        return false;
    }
*/
    // deletes any contact items that are currently checked off.
    public void DeleteContactItem()
    {

        MessageListItem theItem = null;

        int listCount = contactListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(contactListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getContactList().get(index);
                String filename = theItem.filename();

                applicationState.delete_File(filename);
                applicationState.getContactList().remove(index);
                contactCache.remove(index);

            }
        }

        runOnUiThread(refreshEventsView);



    }

    public int getContactSelectedCount()
    {
        MessageListItem theItem = null;
        int selectedCount = 0;
        filesSelected = "";

        int listCount = contactListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(contactListView.isItemChecked(index) == true)
            {
                theItem = (MessageListItem)applicationState.getContactList().get(index);
                String filename = theItem.filename();
                filesSelected = filesSelected + "\n" + theItem.filename();
                selectedCount++;

            }
        }

        return(selectedCount);

    }

    // this just save the current contact to view to global memory
    public void SetContactToView()
    {

        MessageListItem theItem = null;

        int listCount = contactListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(contactListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getContactList().get(index);
                applicationState.contactToView(theItem);

            }
        }


    }


    private Runnable refreshEventsView = new Runnable() {

        @Override
        public void run()
        {

            //if(activityRunning == false)
                //return;

            contactListView.clearChoices();  // clear list choices
            contactArrayAdapter.notifyDataSetChanged();

        }
    };



    void DisplayAlertOKDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
