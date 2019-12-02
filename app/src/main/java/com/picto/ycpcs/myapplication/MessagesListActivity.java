package com.picto.ycpcs.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class MessagesListActivity extends AppCompatActivity {

    static final private int MENU_VIEW = Menu.FIRST;
    static final private int MENU_DELETE = Menu.FIRST+1;


    static boolean activityRunning = true;

    static ListView messageListView;
    static ArrayAdapter<MessageListItem> messageArrayAdapter;
    ArrayList<MessageListItem> messageCache = new ArrayList<MessageListItem>();

    ApplicationState applicationState = null;
    String filesSelected;
    Intent intent;
    int selectedCount;
    MessagesListActivity ourself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        ourself = this;

        messageListView = (ListView)this.findViewById(R.id.listViewMessage);
        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        messageListView.setItemsCanFocus(false);
        messageListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // CHOICE_MODE_MULTIPLE

        FloatingActionButton fabtrash = (FloatingActionButton) findViewById(R.id.fab_trash);
        fabtrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCount = getMessageSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A message item must be selected to Delete it!");
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ourself);
                    builder.setMessage("Are you sure you want to delete the selected file(s)?\n")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DeleteMessageItem();
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
                selectedCount = getMessageSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A message item must be selected to View it!");
                }
                else if(selectedCount > 1)
                {
                    // not allowed dialog
                    DisplayAlertOKDialog("You have multiples message items selected. Only single message view is supported!");
                }
                else {
                    SetMessageToView();
                    intent = new Intent();
                    intent.setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MessageViewActivity");
                    startActivity(intent);
                }
            }
        });

        applicationState.LoadMessageList();

        Vector messageList = applicationState.getMessageList();
        int sizeToRead = messageList.size();

        synchronized(messageList)
        {
            for(int x= 0; x<sizeToRead; x++)
                messageCache.add((MessageListItem)messageList.get(x) );

        }

        int layoutID = android.R.layout.simple_list_item_multiple_choice;
        messageArrayAdapter = new ArrayAdapter<MessageListItem>(this, layoutID , messageCache);
        messageListView.setAdapter(messageArrayAdapter);

        activityRunning = true;

        String filename = applicationState.messageToDelete();
        if(filename.length() > 0)
        {
            DeleteMessageItem(filename);
            applicationState.messageToDelete("");
            sizeToRead = messageList.size();
            startActivity(new Intent().setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MainActivity"));
        }



        if(sizeToRead == 0)
        {
            DisplayAlertOKDialog("Message list is empty.");

        }
        else
        {
            if(applicationState.getNewMessagesCount() > 0)
            {
                DisplayAlertOKDialog("You have " + String.valueOf(applicationState.getNewMessagesCount() + " new message(s)"));
                applicationState.setNewMessagesCount(0); // cleare the new message counter
            }
        }
    }


    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        //startActivity(new Intent().setClassName("com.cs381.picto", "com.cs381.picto.MainActivity"));
        startActivity(new Intent().setClassName(applicationState.picto_package_name,  applicationState.picto_package_name + ".MainActivity"));
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_VIEW, Menu.NONE, "View");
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

                selectedCount = getHistorySelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A message item must be selected to View it!");
                }
                else if(selectedCount > 1)
                {
                    // not allowed dialog
                    DisplayAlertOKDialog("You have multiples message items selected. Only single message view is supported!");
                }
                else {
                    SetMessageToView();
                    intent = new Intent();
                    intent.setClassName(applicationState.picto_package_name, applicationState.picto_package_name + ".MessageViewActivity");
                    startActivity(intent);
                }
                return true;
            }

            case (MENU_DELETE):
            {

                selectedCount = getHistorySelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A message item must be selected to Delete it!");
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to delete the selected file(s)?\n")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DeleteHistoryItem();
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

    // deletes any history items that are currently checked off.
    public void DeleteMessageItem()
    {

        MessageListItem theItem = null;

        int listCount = messageListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(messageListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getMessageList().get(index);
                String filename = theItem.filename();

                applicationState.delete_File(filename);
                applicationState.getMessageList().remove(index);
                messageCache.remove(index);

            }
        }

        runOnUiThread(refreshEventsView);

    }

    // deletes any history items that are currently checked off.
    public void DeleteMessageItem(String filenameToDelete)
    {

        MessageListItem theItem = null;

        int listCount = messageListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            // if(messageListView.isItemChecked(index) == true)


            theItem = (MessageListItem)applicationState.getMessageList().get(index);
            String filename = theItem.filename();
            if(filename.equals(filenameToDelete)  == true)
            {
                applicationState.delete_File(filename);
                applicationState.getMessageList().remove(index);
                messageCache.remove(index);
                break;

            }
        }

        runOnUiThread(refreshEventsView);

    }

    public int getMessageSelectedCount()
    {
        MessageListItem theItem = null;
        int selectedCount = 0;
        filesSelected = "";

        int listCount = messageListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(messageListView.isItemChecked(index) == true)
            {
                theItem = (MessageListItem)applicationState.getMessageList().get(index);
                String filename = theItem.filename();
                filesSelected = filesSelected + "\n" + theItem.filename();
                selectedCount++;

            }
        }

        return(selectedCount);

    }

    // this just save the current history to view to global memory
    public void SetMessageToView()
    {

        MessageListItem theItem = null;

        int listCount = messageListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(messageListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getMessageList().get(index);
                applicationState.messageToView(theItem);

            }
        }


    }




    private Runnable refreshEventsView = new Runnable() {

        @Override
        public void run()
        {

            if(activityRunning == false)
                return;

            messageListView.clearChoices();  // clear list choices
            messageArrayAdapter.notifyDataSetChanged();

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
