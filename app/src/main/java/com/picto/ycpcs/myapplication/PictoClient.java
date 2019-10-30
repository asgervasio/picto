package com.picto.ycpcs.myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class PictoClient  {

	protected Socket socket;
	static final int PORT = 6970;
	ApplicationState appstate = null;
	int msgCount = 1;
	IO_Thread ioThread = null;
	String username = "";
	String password = "";
	
	
	public PictoClient() {
		// TODO Auto-generated constructor stub
		appstate = ApplicationState.getApplicationStateInstance();
		
	}
	public boolean connect(String ip_address)
	{
		boolean rv = true;
		// create IO_Thread to handle sockets sends and receives
		   ioThread = new IO_Thread();
		// connect to the specified server ip address
		   rv = ioThread.connect(ip_address);
		   //System.out.println("started Client thread ");
		// start the IO thread
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

		// need to implement this
		return rv;
	}
	
	public boolean sendMessageToServer(PictoMessage msg)
	{
		boolean rv = true;
		
		// add messages to queue
		appstate.addSendMessageItem(msg);
		
		return rv;
	}
	
	public PictoMessage readMessageFromServer()
	{
		PictoMessage msg = null;
		
		msg = appstate.getNextReceiveMessageItem();
		
		return msg;
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
