package com.picto.ycpcs.myapplication;


import android.app.Application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.security.SecureRandom;
import java.util.Vector;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class ApplicationState extends Application
{
    private static ApplicationState singleton;

    static boolean serviceRunning = false;

    String messageFilenameToDelete = "";
    MessageListItem messageToView = null;
    MessageListItem pictureToView = null;
    CameraActivity parent;

    static Vector messageList = new Vector();
    static Vector selectedHistorys = new Vector();

    static Vector pictureList = new Vector();
    static Vector selectedPictures = new Vector();

    static Vector<PictoMessage> messageListSend = new Vector<PictoMessage>();
    static Vector<PictoMessage> messageListReceive = new Vector<PictoMessage>();

    static Bitmap bitmap_lastPicture = null;
    MessageListItem lastMessage = null;

    static PictoClient client = null;

    static final String picto_msg_file_prefix = "picto_msg_";  // message file prefix
    static final String picto_pic_file_prefix = "picto_pic_";  // picture file prefix
    static final String pictoSettingsFilename = "picto_settings.cfg";

    static final String picto_msg_type_Received = "Received";
    static final String picto_msg_type_Sent = "Sent";
    private String ipAddress;
    private String username = "";
    private String password = "";

    private String toAddress;
    private String caption;
    private String contentSettings;

    static byte[] bytes_lastPicture_compressed;
    static int new_messages_count = 0;
    static int new_pictures_count = 0;
    static boolean loggedIn = false;

    static String lastStatusMessage = "";
    static boolean debugEnabled = false;
    static final byte[] AES_key_128 = new byte[] { (byte)0xe4, (byte)0x35, (byte)0xa1, (byte)0x20, (byte)0x79, (byte)0x01, (byte)0x85, (byte)0xff, (byte)0x07, (byte)0xc2, (byte)0x7a, (byte)0x11, (byte)0xb9, (byte)0x22, (byte)0x3f, (byte)0x68 };
    static byte[] unique_key_128 = new byte[16];

    public void debugEnabled(boolean debugEnabled)
    {
        this.debugEnabled = debugEnabled;
    }

    public boolean debugEnabled()
    {
        return(debugEnabled);
    }

    public void addStatusMessage(String msg)
    {
        if( lastStatusMessage.length() > 280)
        {
            lastStatusMessage = lastStatusMessage.substring(200) + " ,TRUNC ";
        }
        lastStatusMessage = lastStatusMessage + msg;
    }

    public synchronized boolean  loggedIn()
    {
        return (this.loggedIn);
    }

    public synchronized void loggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

    public synchronized int  getNewMessagesCount()
    {
        return (this.new_messages_count);
    }

    public synchronized int  getNewPicturesCount()
    {
        return (this.new_pictures_count);
    }

    public synchronized void setNewMessagesCount(int count)
    {
        this.new_messages_count = count;
    }

    public synchronized void setNewPicturesCount(int count)
    {
        this.new_pictures_count = count;
    }

    // get the toAddress
    public synchronized String toAddress()
    {
        return this.toAddress;
    }

    // set the toAddress
    public synchronized void toAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }

    // get the caption
    public synchronized String caption()
    {
        return this.caption;
    }

    // set the caption
    public synchronized void caption(String caption)
    {
        this.caption = caption;
    }

    // get the content settings
    public synchronized String contentSettings()
    {
        return this.contentSettings;
    }

    // set the content settings
    public synchronized void contentSettings(String contentSettings)
    {
        this.contentSettings = contentSettings;
    }


    // get the ipAddress
    public synchronized String ipAddress()
    {
        return this.ipAddress;
    }

    // set the ipAddress
    public synchronized void ipAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    // get the username
    public synchronized String username()
    {
        return this.username;
    }

    // set the username
    public synchronized void username(String username)
    {
        this.username = username;
    }

    // get the password
    public synchronized String password()
    {
        return this.password;
    }

    // set the password
    public synchronized void password(String password)
    {
        this.password = password;
    }

    public synchronized String  getPictoPictureFilePrefix()
    {
        return (this.picto_pic_file_prefix);
    }

    public synchronized String  getPictoMsgFilePrefix()
    {
        return (this.picto_msg_file_prefix);
    }

    static public synchronized String  getPictoSettingsFilename()
    {
        return (pictoSettingsFilename); // return (this.pictoSettingsFilename);
    }

    public synchronized void  setPictoClient(PictoClient client)
    {
        this.client = client;
    }

    public synchronized PictoClient  getPictoClient()
    {
        return (this.client);
    }

    public synchronized void  setLastMessage(MessageListItem lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public synchronized MessageListItem  getLastMessage()
    {
        return (this.lastMessage);
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


    public synchronized void messageToDelete(String messageFilename)
    {
        this.messageFilenameToDelete = messageFilename;
    }
    public synchronized String messageToDelete()
    {
        return(this.messageFilenameToDelete);
    }

    public synchronized void messageToView(MessageListItem messageItem)
    {
        this.messageToView = messageItem;
    }
    public synchronized MessageListItem messageToView()
    {
        return(this.messageToView);
    }

    public synchronized void pictureToView(MessageListItem messageItem)
    {
        this.pictureToView = messageItem;
    }
    public synchronized MessageListItem pictureToView()
    {
        return(this.pictureToView);
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



    // Message list
    public synchronized Vector  getMessageList()
    {
        return messageList;
    }
    public synchronized void  addMessageItem(MessageListItem  item)
    {

        messageList.add(0,item); //add message to the top of the list
    }

    // Picture list
    public synchronized Vector  getPictureList()
    {
        return pictureList;
    }
    public synchronized void  addPictureItem(MessageListItem  item)
    {

        pictureList.add(0,item); //add picture to the top of the list
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

/*
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

                getMessageList().clear();

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

*/

    public synchronized void LoadPictureList()
    {
        File file = null;
        try
        {
            String path = getApplicationContext().getFilesDir().getPath().toString();
            ApplicationState.debugMessage( "Pictures files folder Path:(" + path + ")");
            file = new File(path);

            if(file.exists() == true)
            {
                File[] files = file.listFiles();
                ApplicationState.debugMessage( "Number of files :(" + files.length + ")");

                getPictureList().clear();

                for(int x = 0; x<files.length; x++)
                {
                    String filename = files[x].getName();
                    if(filename.startsWith(getPictoPictureFilePrefix())) {
                        ApplicationState.debugMessage("FileName:" + filename);
                        String fpath = files[x].getAbsolutePath();
                        long size = files[x].length();
                        ApplicationState.debugMessage("Filepath:" + fpath);
                        ApplicationState.debugMessage("Filesize:" + Long.toString(size));

                        // read the contents of the file
                        byte[] content = getByteArrayFromFile (fpath);

                        MessageListItem newItem = bytesToMessageItem(content);

                        /// add to top of the list
                        addPictureItem(newItem);
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

    public synchronized void LoadMessageList()
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

                getMessageList().clear();

                for(int x = 0; x<files.length; x++)
                {
                    String filename = files[x].getName();
                    if(filename.startsWith(getPictoMsgFilePrefix())) {

                        ApplicationState.debugMessage("FileName:" + filename);
                        String fpath = files[x].getAbsolutePath();

/*
                        File fileToDelete = new File(fpath);
                        if(fileToDelete.exists()) {
                            fileToDelete.delete();
                            continue;
                        }
*/
                        long size = files[x].length();
                        ApplicationState.debugMessage("Filepath:" + fpath);
                        ApplicationState.debugMessage("Filesize:" + Long.toString(size));

                        // read the contents of the file
                        byte[] content = getByteArrayFromFile (fpath);

                        MessageListItem newItem = bytesToMessageItem(content);

                        /// add to top of the list
                        addMessageItem(newItem);
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

    public synchronized void CleanupFiles()
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

                for(int x = 0; x<files.length; x++)
                {
                    String filename = files[x].getName();
                    if(filename.startsWith(getPictoMsgFilePrefix())  || filename.startsWith(getPictoPictureFilePrefix()))
                    {

                        //ApplicationState.debugMessage("FileName:" + filename);
                        String fpath = files[x].getAbsolutePath();

                        File fileToDelete = new File(fpath);
                        if(fileToDelete.exists())
                        {
                            fileToDelete.delete();
                            continue;
                        }

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

    public synchronized void addPicture(byte[] content, String caption)
    {
        MessageListItem newItem = null;
        Date date = new Date();
        String filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + getPictoPictureFilePrefix() + Long.toString(date.getTime());

        newItem = new MessageListItem(caption,filename); // create a new history item
        newItem.content(content);

        byte[] resultBytes = MessageItemToBytes(newItem);

        addPictureItem(newItem); // add this new scan to the picture list
        createFile(filename,resultBytes);

        //new_messages_count++;
    }

    public synchronized void addMessage(String fromAddress,byte[] content, String caption,String type,String contentSettings)
    {
        MessageListItem newItem = null;
        Date date = new Date();
        String filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + getPictoMsgFilePrefix() + Long.toString(date.getTime());

        newItem = new MessageListItem(caption,filename); // create a new history item
        newItem.content(content);
        newItem.type(type);
        newItem.fromAddress(fromAddress);
        newItem.contentSettings(contentSettings);

        byte[] resultBytes = MessageItemToBytes(newItem);

        addMessageItem(newItem); // add this new scan to the message list
        createFile(filename,resultBytes);

        //new_messages_count++;
    }

    public synchronized static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public synchronized static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public synchronized static byte[] getByteArrayFromFile (String filePath) throws Exception {
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

    synchronized byte[] MessageItemToBytes(MessageListItem item)
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

    synchronized  MessageListItem bytesToMessageItem(byte[] itemBytes)
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

    public synchronized void createFile(String filename, String content)
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

    public synchronized void createFile(String filename, byte[] content)
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

    public synchronized static void delete_File(String filename)
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

    public synchronized String getLoggedInName()
    {
        try {
            String settings_filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + getPictoSettingsFilename();
            File file = new File(settings_filename);

            if (file.exists() == true) {
                // read the contents of the file
                byte[] content = getByteArrayFromFile(settings_filename);

                PictoSettings newItem = PictoSettings.bytesToPictoSettings(content);

                if(newItem.username() != null)
                {
                    return(newItem.username());
                }

            }
        }
        catch(Exception e)
        {

        }
        return("");
    }

    //  exits activity.
    public synchronized void exitApplication()
    {
        // This will clear the activity list and put the main activity at the top.
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] makeEncryptKey(String username,String caption)
    {
        //AES_key_128
        byte[] usernameBytes = username.getBytes();
        byte[] captionBytes = caption.getBytes();

        int outputIndex = 0;
        int usernameIndex = 0;
        int captionIndex = 0;
        int keyIndex = 0;
        int field_selection = 0;

        while(outputIndex < 16)
        {
            switch(field_selection)
            {
                case 0:
                    if (usernameIndex < usernameBytes.length) {
                        unique_key_128[outputIndex++] = usernameBytes[usernameIndex++];

                    }
                    field_selection++;
                    continue;

                case 1:
                    if (captionIndex < captionBytes.length) {
                        unique_key_128[outputIndex++] = captionBytes[captionIndex++];

                    }
                    field_selection++;
                    continue;

                case 2:
                    if (keyIndex < AES_key_128.length) {
                        unique_key_128[outputIndex++] = AES_key_128[keyIndex++];

                    }
                    field_selection = 0;  // start from first field
                    continue;

            }


        }
        return(unique_key_128);
    }

    public static byte[] encryptBitmap(final Bitmap bmp, byte[] key) throws Exception
    {
        //byte[] input;
        // compress the bitmap image to a PNG byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        //return(byteArray);

        return(encrypt(byteArray, key));  // return the AES encrypted buffer
    }

    public static Bitmap decryptBitmap(final byte[] input, byte[] key) throws Exception
    {
        byte[] aes_decrypt_buf = null;

        try {
            // AES decrypt
            aes_decrypt_buf = decrypt(input, key);
        }
        catch(Exception e)
        {
            throw e;
        }

        /*
        // decrypt AES
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            aes_decrypt_buf = cipher.doFinal(input);
        }
        catch(Exception e)
        {
            throw e;
        }
        */

        //DECODE  PNG
        Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(aes_decrypt_buf,0,aes_decrypt_buf.length);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 , blob);
        byte[] bitmapdata = blob.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

        return(bitmap);
    }

    public static byte[] encryptBitmapWorks(final Bitmap bmp, byte[] key) throws Exception
    {
        byte[] input;
        // compress the bitmap image to a PNG byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return(byteArray);

        //return(encrypt(input, key));
    }

    public static Bitmap decryptBitmapWorks(final byte[] input, byte[] key) throws Exception
    {
        /*
        byte[] aes_decrypt_buf = null;
        // decrypt AES
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            aes_decrypt_buf = cipher.doFinal(input);
        }
        catch(Exception e)
        {
            throw e;
        }
        */
        //DECODE
        Bitmap compressed_bitmap = BitmapFactory.decodeByteArray(input,0,input.length);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        compressed_bitmap.compress(Bitmap.CompressFormat.PNG, 0 , blob);
        byte[] bitmapdata = blob.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

        return(bitmap);
    }

    public static byte[] encrypt(byte[] input, byte[] key) throws Exception
    {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    public static byte[] decrypt(byte[] input, byte[] key) throws Exception
    {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return cipher.doFinal(input);
        }
        catch(Exception e)
        {
            throw e;
        }
    }

}
