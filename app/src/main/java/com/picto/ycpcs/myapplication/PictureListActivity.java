package com.picto.ycpcs.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class PictureListActivity extends AppCompatActivity {

    static final private int MENU_VIEW = Menu.FIRST;
    static final private int MENU_SEND = Menu.FIRST+1;
    static final private int MENU_DELETE = Menu.FIRST+2;


    static boolean activityRunning = true;

    static ListView pictureListView;
    static ArrayAdapter<MessageListItem> pictureArrayAdapter;
    ArrayList<MessageListItem> pictureCache = new ArrayList<MessageListItem>();

    ApplicationState applicationState = null;
    String filesSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);


        pictureListView = (ListView)this.findViewById(R.id.listViewPicture);
        // get global data reference
        applicationState = ((ApplicationState)getApplicationContext());

        pictureListView.setItemsCanFocus(false);
        pictureListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // CHOICE_MODE_MULTIPLE


        //applicationState.LoadPictureList();

        Vector pictureList = applicationState.getPictureList();
        int sizeToRead = pictureList.size();

        synchronized(pictureList)
        {
            for(int x= 0; x<sizeToRead; x++)
                pictureCache.add((MessageListItem)pictureList.get(x) );

        }

        int layoutID = android.R.layout.simple_list_item_multiple_choice;
        pictureArrayAdapter = new ArrayAdapter<MessageListItem>(this, layoutID , pictureCache);
        pictureListView.setAdapter(pictureArrayAdapter);

        activityRunning = true;

        if(sizeToRead == 0)
        {
            DisplayAlertOKDialog("Picture list is empty.");

        }
        else
        {
            if(applicationState.getNewPicturesCount() > 0)
            {
                DisplayAlertOKDialog("You have " + String.valueOf(applicationState.getNewPicturesCount() + " new picture(s)"));
                applicationState.setNewPicturesCount(0); // cleare the new message counter
            }
        }
    }


    @Override
    public void  onBackPressed()
    {
        super.onBackPressed();

        startActivity(new Intent().setClassName("com.cs381.picto", "com.cs381.picto.MainActivity"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_VIEW, Menu.NONE, "View");
        menu.add(0, MENU_SEND, Menu.NONE, "Send");
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

                selectedCount = getPictureSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A picture item must be selected to View it!");
                }
                else if(selectedCount > 1)
                {
                    // not allowed dialog
                    DisplayAlertOKDialog("You have multiples picture items selected. Only single picture view is supported!");
                }
                else {
                    SetPictureToView();
                    intent = new Intent();
                    intent.setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.PictureViewActivity");
                    startActivity(intent);
                }
                return true;
            }

            case (MENU_SEND):
            {

                selectedCount = getPictureSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A picture item must be selected to Send it!");
                }
                else if(selectedCount > 1)
                {
                    // not allowed dialog
                    DisplayAlertOKDialog("You have multiples Picture items selected. Only single picture view is supported!");
                }
                else {
                    SetPictureToSend();
                    intent = new Intent();
                    //intent.setClassName("com.cs381.picto", "com.cs381.picto.PictureViewActivity");
                    intent.setClassName("com.picto.ycpcs.myapplication", "com.picto.ycpcs.myapplication.MessageSendSaveActivity");
                    startActivity(intent);
                }
                return true;
            }

            case (MENU_DELETE):
            {

                selectedCount = getPictureSelectedCount();
                if(selectedCount == 0)
                {
                    // no item selected message
                    DisplayAlertOKDialog("A picture item must be selected to Delete it!");
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

        int listCount = pictureListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(pictureListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getPictureList().get(index);
                String filename = theItem.filename();

                applicationState.delete_File(filename);
                applicationState.getPictureList().remove(index);
                pictureCache.remove(index);

            }
        }

        runOnUiThread(refreshEventsView);



    }

    public int getPictureSelectedCount()
    {
        MessageListItem theItem = null;
        int selectedCount = 0;
        filesSelected = "";

        int listCount = pictureListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(pictureListView.isItemChecked(index) == true)
            {
                theItem = (MessageListItem)applicationState.getPictureList().get(index);
                String filename = theItem.filename();
                filesSelected = filesSelected + "\n" + theItem.filename();
                selectedCount++;

            }
        }

        return(selectedCount);

    }

    // this just save the current picture to view to global memory
    public void SetPictureToView()
    {

        MessageListItem theItem = null;

        int listCount = pictureListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(pictureListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getPictureList().get(index);
                applicationState.pictureToView(theItem);

            }
        }


    }


    // setup this picture for sending
    public void SetPictureToSend()
    {

        MessageListItem theItem = null;

        int listCount = pictureListView.getCount();
        for(int index = listCount-1; index>=0; index--)
        {
            if(pictureListView.isItemChecked(index) == true)
            {

                theItem = (MessageListItem)applicationState.getPictureList().get(index);
                byte[] pngImage = theItem.content();

                applicationState.setLastMessage(theItem);
                //DECODE PNG to Bitmap and set last picture
                pngToBitmapSetLastPicture(pngImage);
                //applicationState.setLastPicture(bitmap);
                //applicationState.pictureToView(theItem);

            }
        }


    }

    public void  pngToBitmapSetLastPicture(final byte[] pngImage)
    {
        // this needs to be run in its own thread because the compression can take some time and
        // we don't want to stall the GUI thread.
        runOnUiThread(new Runnable() {
            public void run() {
                //DECODE
                Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(pngImage,0,pngImage.length);
                //Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
                byte[] bitmapdata = blob.toByteArray();

                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                applicationState.setLastPicture(bitmap);


            }
        });



    }




    private Runnable refreshEventsView = new Runnable() {

        @Override
        public void run()
        {

            if(activityRunning == false)
                return;

            pictureListView.clearChoices();  // clear list choices
            pictureArrayAdapter.notifyDataSetChanged();

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
