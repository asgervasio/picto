package com.example.dev.simplescan;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class AndroidClient  {

	protected Socket socket;
	static final int PORT = 6970;
	ApplicationState appstate = null;
	int msgCount = 1;
	IO_Thread ioThread = null;
	String username = "";
	String password = "";
	
	
	public AndroidClient() {
		// TODO Auto-generated constructor stub
		appstate = ApplicationState.getApplicationStateInstance();
		
	}
	public boolean connect(String ip_address)
	{
		boolean rv = true;
		// create IO_Thread
		   ioThread = new IO_Thread();
		   rv = ioThread.connect(ip_address);
		   System.out.println("started Client thread ");
		   ioThread.start();
		   
		   return rv;
	}
	
	public boolean disconnect()
	{
		boolean rv = false;
		
		rv = ioThread.disconnect();
		
		return rv;
	}
	
	public boolean login(String username, String password)
	{
		boolean rv = true;
		
		this.username = username.toLowerCase();
		this.password = password;
		   
		return rv;
	}
	
	public boolean sendMessageToServer(AndroidMessage msg)
	{
		boolean rv = true;
		
		// add messages to queue
		appstate.addSendMessageItem(msg);
		
		return rv;
	}
	
	public AndroidMessage readMessageFromServer()
	{
		AndroidMessage msg = null;
		
		msg = appstate.getNextReceiveMessageItem();
		
		return msg;
	}
	
	public void testAll()
	{
		AndroidMessage received_message;
		AndroidMessage message1 = new AndroidMessage("Ed", "bob","message" + String.valueOf(msgCount) + " for bob","message" + String.valueOf(msgCount) + ".txt");		
		
		byte[] image1 = new byte[10];
		//byte[] image2 = new byte[100000];
		for(int x = 0; x<10; x++)
		{
			image1[x] = 'A';
		}
		/*
		for(int x = 0; x<100000; x++)
		{
			image2[x] = 'B';
		}
		*/
		message1.image(image1);
		//message2.image(image2);
		
		appstate = ApplicationState.getApplicationStateInstance();
				

		// loop sending three different messages to the server
		//
		// add messages to queue
		appstate.addSendMessageItem(message1);
		//appstate.addSendMessageItem(message2);
		received_message = appstate.getNextReceiveMessageItem();
		while(received_message != null)
		{
			received_message = appstate.getNextReceiveMessageItem();
		}
			
		
		msgCount++;
	}
	

	boolean getMessages()
	{
		// create Request_Message 
		// submit message to the out bound message queue
		
		// if there are any inbound messages they will be returned
		// to the user via a message listener
				
		
		return true;
	}

}