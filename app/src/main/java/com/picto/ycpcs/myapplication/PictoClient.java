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
	public synchronized boolean connect(String ip_address)
	{
		boolean rv = true;
		// create IO_Thread to handle sockets sends and receives
		   ioThread = new IO_Thread();
		// connect to the specified server ip address
		   rv = ioThread.connect(ip_address);
		if(rv == true)
		{
			//System.out.println("started Client thread ");
			// start the IO thread
			ioThread.start();
		}
		   
		   return rv;
	}
	
	public synchronized boolean disconnect(boolean showLogin)
	{
		boolean rv = false;
		
		rv = ioThread.disconnect(showLogin);
		
		return rv;
	}

	public synchronized boolean pingServer() throws Exception
	{
		boolean rv = false;

        try {
            rv = ioThread.PingServer();
        }
        catch(Exception e)
        {
            throw e;
        }

		return rv;
	}
	/*
	public boolean login(String username, String password)
	{
		boolean rv = true;
		
		this.username = username.toLowerCase();
		this.password = password;

		// need to implement this
		return rv;
	}
	*/
	public synchronized int login(String username, String password,boolean newAccount)
	{
		int rv = CommandHeader.STATUS_SUCCESS;

		this.username = username.toLowerCase();
		this.password = password;

        try {
            rv = ioThread.LoginToServer(this.username, this.password, newAccount);
        }
        catch(Exception e)
        {

        }

		return rv;
	}
	
	public synchronized boolean sendMessageToServer(PictoMessage msg)
	{
		boolean rv = true;
		
		// add messages to queue
		appstate.addSendMessageItem(msg);
		
		return rv;
	}
	
	public synchronized PictoMessage readMessageFromServer()
	{
		PictoMessage msg = null;
		
		msg = appstate.getNextReceiveMessageItem();
		
		return msg;
	}


    synchronized boolean getMessages()
	{
		// create Request_Message 
		// submit message to the out bound message queue
		
		// if there are any inbound messages they will be returned
		// to the user via a message listener
				
		
		return true;
	}

}
