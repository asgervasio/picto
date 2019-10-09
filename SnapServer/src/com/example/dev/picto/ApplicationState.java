package com.example.dev.picto;
import java.util.Vector;


public class ApplicationState 
{
    private static ApplicationState singleton;

    static boolean serviceRunning = false;
   
    static Vector<AndroidMessage> messageListSend = new Vector<AndroidMessage>();
    static Vector<AndroidMessage> messageListReceive = new Vector<AndroidMessage>();
    
    static Vector<AndroidMessage> selectedMessages = new Vector<AndroidMessage>();
    
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
    public synchronized Vector<AndroidMessage>  getSendMessageList()
    {
        return messageListSend;
    }
    public synchronized void  addSendMessageItem(AndroidMessage  item)
    {   	
    	messageListSend.add(item); //add message to the end of the list
    	System.out.println("addSendMessageItem: " + item + " vsize = " + messageListSend.size()); 
    }

    public synchronized AndroidMessage  getNextSendMessageItem()
    {
    	AndroidMessage  item;
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
    public synchronized Vector<AndroidMessage>  getReceiveMessageList()
    {
        return messageListReceive;
    }
    public synchronized void  addReceiveMessageItem(AndroidMessage  item)
    {   	 
    	messageListReceive.add(item); //add message to the top of the list
    	System.out.println("addReceiveMessageItem: " + item + " vsize = " + messageListReceive.size());
    }

    public synchronized AndroidMessage  getNextReceiveMessageItem()
    {
    	AndroidMessage  item;
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
    public synchronized Vector<AndroidMessage>  getSelectedMessages()
    {
        return selectedMessages;
    }
    public synchronized void  addSelectedMessages(AndroidMessage  item)
    {
    	selectedMessages.addElement(item);
    }
    public synchronized void  clearSelectedMessages()
    {
    	selectedMessages.clear();
    }


}
