package com.example.dev.simplescan;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IO_Thread extends Thread {

	static final int PORT = 6970; 
	ServerSocket serverSocket = null;
    Socket socket = null;
    
    InputStream inp = null;
    BufferedInputStream brinp = null;
    BufferedOutputStream out = null;
    byte[] readHeaderBuffer = null;
    
    ApplicationState appstate = null;
    boolean io_running = true;
    
	public IO_Thread() {
		// TODO Auto-generated constructor stub
		appstate = ApplicationState.getApplicationStateInstance();
		readHeaderBuffer = new byte[CommandHeader.HEADER_SIZE];
	}
	
	public boolean connect(String ip_address)
	{
		boolean rv = false;
		
		try 
		{
        	socket = new Socket(ip_address, PORT);
        	
        	inp = socket.getInputStream();
            brinp = new BufferedInputStream(inp);
            //brinp.read(readBuffer);
            out = new BufferedOutputStream(socket.getOutputStream());
            rv = true;
        } 
        catch (IOException e) 
        {
        	
        }
		
		return rv;
	}
	
	public boolean disconnect()
	{
		boolean rv = false;
		
		try 
		{
			if(socket != null)
			{
				socket.close();
			}
            rv = true;
        } 
        catch (IOException e) 
        {
        	
        }
		
		io_running = false;
		
		return rv;
	}
	
	// create header
	// send header
	// flush send
	
	// send serialized message
	// flush send
	public boolean SendMsgToServer(AndroidMessage msg)
	{
		boolean rv = false;
		try 
        {
			
			byte[] messageBytes = AndroidMessage.androidMessageToBytes(msg);
			//CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_SEND_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			CommandHeader header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_SEND_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			
			byte[] headerBytes = header.exportHeader();
			out.write(headerBytes);
        	out.flush();
        	
        	out.write(messageBytes);
        	out.flush();
        	
        	//System.out.println("Send Message " + msg.imageFileName());
        	System.out.println("Send Message - From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.imageFileName());
        	
			
        	// wait for server ack
    	
        	rv = true;
                                               
        } 
        catch (IOException e) 
        {
            //e.printStackTrace();
            
        }	
		
		return rv;
	}
	
	public AndroidMessage ReceiveMsgFromServer()
	{
		
		int tryCount = 0;
		AndroidMessage msg = null;
		byte[] receivedMessage;
		CommandHeader header;
		
		try 
        {
			// CREATE a message read request
			AndroidMessage message = new AndroidMessage("Bob","","","");	
			byte[] messageBytes = AndroidMessage.androidMessageToBytes(message);
			header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_READ_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			byte[] headerBytes = header.exportHeader();
			out.write(headerBytes);
	    	out.flush();
	    	
	    	out.write(messageBytes);
	    	out.flush();
	    	
	    	Thread.sleep(100);
        }
		catch (Exception e) 
        {
            
            
        }
    	
		while(tryCount < 3)
		{
			try 
	        {	
				
	        	// check for any recieved messages
	        	
	        	if(brinp.available() > 0)
	        	{
	        		// get the header
		        	brinp.read(readHeaderBuffer);
		        	header = new CommandHeader(0,0,0,0,0);
		        	header.importHeader(readHeaderBuffer);
		        	// allocate buffer for the message object
		        	receivedMessage = new byte[header.payloadSize()];
		        	// get message
		        	brinp.read(receivedMessage);
		        	// convert the message buffer to message object
		        	msg =  AndroidMessage.bytesToAndroidMessage(receivedMessage);
		        	
		        	//System.out.println("client read Message " + msg.imageFileName());
		        	System.out.println("Read - From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.imageFileName());
		        	
		        	break;
	        	       	
	        	}
	        	else
	        	{
	        		tryCount++;
	        		
	        	}
	        	
	        	Thread.sleep(500);
	                                               
	        } 
	        catch (Exception e) 
	        {
	            //e.printStackTrace();
	            
	        }
		}
		
		return msg;
	}
	
	public void run()
	{
		AndroidMessage message;
		// check send message Q for messages to send
		// send any messages
		//
		//S1. Get Message from Send Q
		//S2. Create header
		//S3. Create message
		//S4. Send Header over socket
		//S5. Send message over socket
		//S6. Wait for Server Ack
		//S7. If messages in vector left to send Goto S1
		
		// R1 Create Request Message command
		// R2. Send Request received message 
		// R3. Wait for Server response containing Receive Header
		// R4. Received header – contains number of messages in queue and size of next messages. If message count is zero then we are done so sleep
		// R5. else receive the message
		// R6. Decrement receive count, if > 0 then wait for Receive header (R4)
		// Else we are done so sleep

		
		while(io_running == true)
		{
			try 
			{
				Thread.sleep(500);
				
	
				// get next message from inbound message queue
				message = appstate.getNextSendMessageItem();
				if(message != null)
				{
					// send the message to socket	
					SendMsgToServer(message);
				}
				
					
				Thread.sleep(500);					
				
				// check if messages to receive				
				message = ReceiveMsgFromServer(); // receive messages from server to android client
				if(message != null)
				{
					// if messages to receive
					// copy message to received message Q
					appstate.addReceiveMessageItem(message);
				}
								
				Thread.sleep(2000);	
						
			}
			catch (Exception e)
			{
					   
			}
		}
		
		
	}
	
	public void cleanup()
	{
		
	}
	

}