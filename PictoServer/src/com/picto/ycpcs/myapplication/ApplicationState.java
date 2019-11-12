package com.picto.ycpcs.myapplication;
import java.util.Vector;


public class ApplicationState 
{
    private static ApplicationState singleton;

    static boolean serviceRunning = false;
   
    static Vector<PictoMessage> messageListSend = new Vector<PictoMessage>();
    static Vector<PictoMessage> messageListReceive = new Vector<PictoMessage>();
    
    static Vector<PictoMessage> selectedMessages = new Vector<PictoMessage>();
    
    public ApplicationState()
    {
    	singleton = this;
    }
    
    public synchronized static void setServiceRunning(boolean running)
    {
        serviceRunning = running;
    }

    public synchronized static boolean getServiceRunning()
    {
        return(serviceRunning);
    }

  
    public static ApplicationState getApplicationStateInstance() {return singleton;}

   

    // Send Message list
    public synchronized Vector<PictoMessage>  getSendMessageList()
    {
        return messageListSend;
    }
    public synchronized void  addSendMessageItem(PictoMessage  item)
    {   	
    	messageListSend.add(item); //add message to the end of the list
    	System.out.println("addSendMessageItem: " + item + " vsize = " + messageListSend.size()); 
    }

    public synchronized PictoMessage  getNextSendMessageItem()
    {
    	PictoMessage  item;
    	if(messageListSend.isEmpty() == true)
    	{
    		item = null; 
    	}
    	else
    	{
        	item = messageListSend.remove(0); //Get message from the beginning of the list
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
        	item = messageListReceive.remove(0); //Get message from the beginning of the list
        	System.out.println("getNextReceiveMessageItem: " + item + " vsize = " + messageListReceive.size()); 
        }
    	return item;
    }
    
    
    
    
    //Selected Message list
    public synchronized Vector<PictoMessage>  getSelectedMessages()
    {
        return selectedMessages;
    }
    public synchronized void  addSelectedMessages(PictoMessage  item)
    {
    	selectedMessages.addElement(item);
    }
    public synchronized void  clearSelectedMessages()
    {
    	selectedMessages.clear();
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
}
