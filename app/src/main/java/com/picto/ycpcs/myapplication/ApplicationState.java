package com.picto.ycpcs.myapplication;


import android.app.Application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;


import java.io.BufferedReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import java.util.Vector;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class ApplicationState extends Application
{
    private static ApplicationState singleton;

    static boolean serviceRunning = false;

    MessageListItem historyToView = null;
    CameraActivity parent;

    static Vector historyList = new Vector();
    static Vector selectedHistorys = new Vector();

    static Vector<PictoMessage> messageListSend = new Vector<PictoMessage>();
    static Vector<PictoMessage> messageListReceive = new Vector<PictoMessage>();

    static Bitmap bitmap_lastPicture = null;

    static PictoClient client = null;

    static final String picto_msg_file_prefix = "picto_msg_";
    static final String pictoSettingsFilename = "picto_settings.cfg";

    private String ipAddress;
    private String username;

    private String toAddress;
    private String caption;
    static byte[] bytes_lastPicture_compressed;
    static int new_messages_count = 0;

    public synchronized int  getNewMessagesCount()
    {
        return (this.new_messages_count);
    }

    public synchronized void setNewMessagesCount(int count)
    {
        this.new_messages_count = count;
    }

    // get the toAddress
    public String toAddress()
    {
        return this.toAddress;
    }

    // set the toAddress
    public void toAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }

    // get the caption
    public String caption()
    {
        return this.caption;
    }

    // set the caption
    public void caption(String caption)
    {
        this.caption = caption;
    }


    // get the ipAddress
    public String ipAddress()
    {
        return this.ipAddress;
    }

    // set the ipAddress
    public void ipAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    // get the username
    public String username()
    {
        return this.username;
    }

    // set the username
    public void username(String username)
    {
        this.username = username;
    }

    public synchronized String  getPictoMsgFilePrefix()
    {
        return (this.picto_msg_file_prefix);
    }

    public synchronized String  getPictoSettingsFilename()
    {
        return (this.pictoSettingsFilename);
    }

    public synchronized void  setPictoClient(PictoClient client)
    {
        this.client = client;
    }

    public synchronized PictoClient  getPictoClient()
    {
        return (this.client);
    }

    public synchronized void  setLastPicture(Bitmap bitmap_lastPicture)
    {
        this.bitmap_lastPicture = bitmap_lastPicture;
    }

    public synchronized Bitmap  getLastPicture()
    {
        return (this.bitmap_lastPicture);
    }

    public synchronized void  setLastPictureCompressed(byte[] bytes_lastPicture_compressed)
    {
        this.bytes_lastPicture_compressed = bytes_lastPicture_compressed;
    }

    public synchronized byte[]  getLastPictureCompressed()
    {
        return (this.bytes_lastPicture_compressed);
    }

    public synchronized CameraActivity  getParentActivity()
    {
        return parent;
    }
    public synchronized void  setParentActivity(CameraActivity parent)
    {
        this.parent = parent;
    }

    public synchronized static void setServiceRunning(boolean running)
    {
        serviceRunning = running;
    }

    public synchronized static boolean getServiceRunning()
    {
        return(serviceRunning);
    }



    public synchronized void historyToView(MessageListItem historyItem)
    {
        this.historyToView = historyItem;
    }
    public synchronized MessageListItem historyToView()
    {
        return(this.historyToView);
    }

    public static ApplicationState getApplicationStateInstance() {return singleton;}

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;




        SystemClock.sleep(TimeUnit.SECONDS.toMillis(1));
    }

    static public synchronized void debugMessage(String message)
    {
        //System.out.println(message);
        //Log.i("Info",message);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    // Send Message list
    public synchronized Vector<PictoMessage>  getSendMessageList()
    {
        return messageListSend;
    }
    public synchronized void  addSendMessageItem(PictoMessage  item)
    {

        messageListSend.add(item); //add message to the top of the list
        System.out.println("addSendMessageItem: " + item + " vsize = " + messageListSend.size());
    }

    public synchronized PictoMessage  getNextSendMessageItem()
    {
        PictoMessage  item;
        if(messageListSend.isEmpty() == true)
        {
            item = null; //Get message from the top of the list
        }
        else
        {
            item = messageListSend.remove(0);
            System.out.println("getNextSendMessageItem: " + item + " vsize = " + messageListSend.size());
        }
        return item;
    }

    // Receive Message list
    public synchronized Vector<PictoMessage>  getReceiveMessageList()
    {
        return messageListReceive;
    }

    public synchronized void  addReceiveMessageItem(PictoMessage  item)
    {

        messageListReceive.add(item); //add message to the top of the list
        System.out.println("addReceiveMessageItem: " + item + " vsize = " + messageListReceive.size());
    }

    public synchronized PictoMessage  getNextReceiveMessageItem()
    {
        PictoMessage  item;
        if(messageListReceive.isEmpty() == true)
        {
            item = null; //Get message from the top of the list
        }
        else
        {
            item = messageListReceive.remove(0);
            System.out.println("getNextReceiveMessageItem: " + item + " vsize = " + messageListReceive.size());
        }
        return item;
    }



    // History list
    public synchronized Vector  getHistoryList()
    {
        return historyList;
    }
    public synchronized void  addHistoryItem(MessageListItem  item)
    {

        historyList.add(0,item); //add payload to the top of the list
    }

    //Selected History list
    public synchronized Vector  getSelectedHistorys()
    {
        return selectedHistorys;
    }
    public synchronized void  addSelectedHistorys(MessageListItem  item)
    {
        selectedHistorys.addElement(item);
    }
    public synchronized void  clearSelectedHistorys()
    {
        selectedHistorys.clear();
    }


    public void LoadHistoryList()
    {
        File file = null;
        try
        {
            String path = getApplicationContext().getFilesDir().getPath().toString();
            ApplicationState.debugMessage( "History files folder Path:(" + path + ")");
            file = new File(path);

            if(file.exists() == true)
            {
                File[] files = file.listFiles();
                ApplicationState.debugMessage( "Number of files :(" + files.length + ")");

                getHistoryList().clear();

                for(int x = 0; x<files.length; x++)
                {
                    String filename = files[x].getName();
                    if(filename.startsWith(getPictoMsgFilePrefix())) {
                        ApplicationState.debugMessage("FileName:" + filename);
                        String fpath = files[x].getAbsolutePath();
                        long size = files[x].length();
                        ApplicationState.debugMessage("Filepath:" + fpath);
                        ApplicationState.debugMessage("Filesize:" + Long.toString(size));

                        // read the contents of the file
                        String content = getStringFromFile(fpath);  //reads the entire file as a string.


                        ApplicationState.debugMessage("content:" + content);

                        ApplicationState.debugMessage("Add HistoryItem Filename:(" + fpath + ") Contents:" + content);
                        MessageListItem newItem = new MessageListItem(content, fpath);
                        /// add to top of the list
                        addHistoryItem(newItem);
                    }
                    else
                    {
                        ApplicationState.debugMessage("FileName IGNORED:" + filename);
                    }
                }

            }


        }

        catch (Exception e)
        {
            String error = e.toString();
            // do something with error
            if(error == null)
            {

            }

        }

    }


    public void LoadHistoryListNew()
    {
        File file = null;
        try
        {
            String path = getApplicationContext().getFilesDir().getPath().toString();
            ApplicationState.debugMessage( "Messages files folder Path:(" + path + ")");
            file = new File(path);

            if(file.exists() == true)
            {
                File[] files = file.listFiles();
                ApplicationState.debugMessage( "Number of files :(" + files.length + ")");

                getHistoryList().clear();

                for(int x = 0; x<files.length; x++)
                {
                    String filename = files[x].getName();
                    if(filename.startsWith(getPictoMsgFilePrefix())) {
                        ApplicationState.debugMessage("FileName:" + filename);
                        String fpath = files[x].getAbsolutePath();
                        long size = files[x].length();
                        ApplicationState.debugMessage("Filepath:" + fpath);
                        ApplicationState.debugMessage("Filesize:" + Long.toString(size));

                        // read the contents of the file
                        byte[] content = getByteArrayFromFile (fpath);

                        MessageListItem newItem = bytesToHistoryItem(content);

                        /// add to top of the list
                        addHistoryItem(newItem);
                    }
                    else
                    {
                        ApplicationState.debugMessage("FileName IGNORED:" + filename);
                    }
                }

            }


        }

        catch (Exception e)
        {
            String error = e.toString();
            // do something with error
            if(error == null)
            {

            }

        }

    }

    public void addMessage(byte[] content, String caption)
    {
        MessageListItem newItem = null;
        Date date = new Date();
        String filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + getPictoMsgFilePrefix() + Long.toString(date.getTime());

        newItem = new MessageListItem(caption,filename); // create a new history item
        newItem.content(content);

        byte[] resultBytes = historyItemToBytes(newItem);

        addHistoryItem(newItem); // add this new scan to the history list
        createHistoryFile(filename,resultBytes);

        //new_messages_count++;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static byte[] getByteArrayFromFile (String filePath) throws Exception {
        File file = new File(filePath);

        byte[] bFile = new byte[(int) file.length()];
        //read file into bytes[]
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);
        fileInputStream.close();

        //FileInputStream fin = new FileInputStream(fl);
        //String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        //fin.close();
        return bFile;
    }

    byte[] historyItemToBytes(MessageListItem item)
    {
        byte[] resultBytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(item);
            out.flush();
            resultBytes = bos.toByteArray();


        }
        catch(Exception e)
        {

        }
        finally
        {
            try
            {
                bos.close();
            } catch (IOException ex)
            {
                // ignore close exception

            }
        }
        return(resultBytes);
    }

    MessageListItem bytesToHistoryItem(byte[] itemBytes)
    {
        MessageListItem item = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(itemBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            //Object o = in.readObject();
            item = (MessageListItem)in.readObject();

        }
        catch(Exception e)
        {

        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return item;
    }

    public void createHistoryFile(String filename, String content)
    {
        File file;
        file = new File(filename);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(content);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
            ApplicationState.debugMessage("Filename:(" + filename + ") Contents:" + content);
        }

        catch (IOException e)
        {
            // do something with the error.
        }
        if(file.exists())
        {
            // handle case where file already exists
        }
    }

    public void createHistoryFile(String filename, byte[] content)
    {
        File file;
        file = new File(filename);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(content);
            fOut.close();

            ApplicationState.debugMessage("Filename:(" + filename + ") Contents:" + content);
        }

        catch (IOException e)
        {
            // do something with the error.
        }
        if(file.exists())
        {
            // handle case where file already exists
        }
    }

    public static void delete_File(String filename)
    {

        boolean deleted;
        File file = null;
        try
        {
            file = new File(filename);
            if(file.exists())
            {

                deleted = file.delete();
                ApplicationState.debugMessage("Filename:(" + filename + ")");
            }

        }
        catch (Exception ex)
        {
            // do something with error.
        }

    }


    //  exits activity.
    public void exitApplication()
    {
        // This will clear the activity list and put the main activity at the top.
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

}
