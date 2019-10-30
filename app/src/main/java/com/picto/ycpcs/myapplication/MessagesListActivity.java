package com.picto.ycpcs.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class MessagesListActivity extends AppCompatActivity {

    static final private int MENU_VIEW = Menu.FIRST;
    static final private int MENU_DELETE = Menu.FIRST+1;


    static boolean activityRunning = true;

    static ListView historyListView;
    static ArrayAdapter<MessageListItem> historyArrayAdapter;
    ArrayList<MessageListItem> historyCache = new ArrayList<MessageListItem>();

    ApplicationState applicationState = null;
    String filesSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);


        historyListView = (ListView)this.findViewById(R.id.listViewHistory);
        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        historyListView.setItemsCanFocus(false);
        historyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // CHOICE_MODE_MULTIPLE


        applicationState.LoadHistoryListNew();

        Vector historyList = applicationState.getHistoryList();
        int sizeToRead = historyList.size();

        synchronized(historyList)
        {
            for(int x= 0; x<sizeToRead; x++)
                historyCache.add((MessageListItem)historyList.get(x) );

        }

        int layoutID = android.R.layout.simple_list_item_multiple_choice;
        historyArrayAdapter = new ArrayAdapter<MessageListItem>(this, layoutID , historyCache);
        historyListView.setAdapter(historyArrayAdapter);

        activityRunning = true;

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

        startActivity(new Intent().setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.CameraActivity"));
    }

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
                    SetHistoryToView();
                    intent = new Intent();
                    intent.setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.MessageViewActivity");
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

    // deletes any history items that are currently checked off.
    public void DeleteHistoryItem()
    {

        MessageListItem theItem = null;

        int listCount = historyListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(historyListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getHistoryList().get(index);
                String filename = theItem.filename();

                applicationState.delete_File(filename);
                applicationState.getHistoryList().remove(index);
                historyCache.remove(index);

            }
        }

        runOnUiThread(refreshEventsView);



    }

    public int getHistorySelectedCount()
    {
        MessageListItem theItem = null;
        int selectedCount = 0;
        filesSelected = "";

        int listCount = historyListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(historyListView.isItemChecked(index) == true)
            {
                theItem = (MessageListItem)applicationState.getHistoryList().get(index);
                String filename = theItem.filename();
                filesSelected = filesSelected + "\n" + theItem.filename();
                selectedCount++;

            }
        }

        return(selectedCount);

    }

    // this just save the current history to view to global memory
    public void SetHistoryToView()
    {

        MessageListItem theItem = null;

        int listCount = historyListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(historyListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getHistoryList().get(index);
                applicationState.historyToView(theItem);

            }
        }


    }




    private Runnable refreshEventsView = new Runnable() {

        @Override
        public void run()
        {

            if(activityRunning == false)
                return;

            historyListView.clearChoices();  // clear list choices
            historyArrayAdapter.notifyDataSetChanged();

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
