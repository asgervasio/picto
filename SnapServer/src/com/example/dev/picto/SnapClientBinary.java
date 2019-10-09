package com.example.dev.picto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

/*
 * 


Client thread
Check DB for message destined to this Android client. 
R1. Receive message header (contains inbound message count (android to server)
R2. Receive message
R3. Decrement count. If count > 0 goto R1 else R4.

R4. Receive Header ( request for messages from server to this client)
S1. Send header (containing number of messages for this client)
S2. Send Message
Wait for R4.

 */
public class SnapClientBinary extends Thread {

	protected Socket socket;
	InputStream in_stream = null;
    BufferedInputStream buf_in_stream = null;
    BufferedOutputStream buf_out_stream = null;
    byte[] readHeaderBuffer = null;
    boolean io_running = true;
    ApplicationState appstate = null;
    SnapDB database = null;
	
	public SnapClientBinary(Socket clientSocket,SnapDB database) {
		// TODO Auto-generated constructor stub
		appstate = new ApplicationState();
		this.socket = clientSocket;
		this.database = database;
		readHeaderBuffer = new byte[CommandHeader.HEADER_SIZE];
		
		try {
			in_stream = socket.getInputStream();
			buf_in_stream = new BufferedInputStream(in_stream);
            //brinp.read(readBuffer);
			buf_out_stream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            //return;
        }
		
		// read all messages from the DB that need to be sent to this client
		// write these messages to the send message Q
		// we will not delete the message from the DB but will instead mark it as
		// delivered. The message will be deleted by the server when it's life timer expires
	}


	
	public void run()
	{
		AndroidMessage message;
		// check send message Q for messages to send
		// send any messages
		//
		while(io_running == true)
		{
		try 
		{
			Thread.sleep(1000);
			
			// read messages from socket
			message = ReceiveMsg(); //appstate.getNextReceiveMessageItem();

			
		}
		catch (Exception e)
		{
				   
		}
		}
		
		
	}
	/*
	public AndroidMessage CreateTestReadMessage(String inboxName)
	{
		AndroidMessage message = new AndroidMessage("Ed",inboxName,"message for you","messagetest.txt");		
		
		byte[] image1 = new byte[100000];
		
		for(int x = 0; x<100000; x++)
		{
			image1[x] = 'C';
		}
		
		
		message.image(image1);
		return message;
	}
	*/
	public boolean Send(AndroidMessage msg)
	{
		boolean rv = false;
		try 
        {
			
			byte[] messageBytes = AndroidMessage.androidMessageToBytes(msg);
			CommandHeader header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_SEND_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			
			byte[] headerBytes = header.exportHeader();
			buf_out_stream.write(headerBytes);
			buf_out_stream.flush();
        	
			buf_out_stream.write(messageBytes);
			buf_out_stream.flush();
			
        	// wait for server ack
			//System.out.println("server send Message " + msg.imageFileName());
			
			
			System.out.println("Send Message - From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.imageFileName());
        	
        	rv = true;
                                               
        } 
        catch (IOException e) 
        {
            //e.printStackTrace();
            
        }	
		
		return rv;
	}
	
	public String getCommandString(int type)
	{
		String rv = "";
		
		switch(type)
		{
			case CommandHeader.CMD_SEND_MSG:
				rv = "SEND";
				break;
			case CommandHeader.CMD_READ_MSG:
				rv = "READ";
				break;
			default:
				rv = "UNKNOWN";
				break;
		}
		return rv;
		
	}
	
	public AndroidMessage ReceiveMsg()
	{
		
		int tryCount = 0;
		AndroidMessage msg = null;
		byte[] receivedMessage;
		
		
		while(tryCount < 6)
		{
			try 
	        {	
				
	        	// wait for server ack
	        	
	        	if(buf_in_stream.available() > 0)
	        	{
	        		// get the header
	        		buf_in_stream.read(readHeaderBuffer);
		        	CommandHeader header = new CommandHeader(0,0,0,0,0);
		        	header.importHeader(readHeaderBuffer);
		        	int commandType = header.commandType();
		        	System.out.println("Read Header - Type: " + getCommandString(commandType) );
		        	
		        	// GET THE TYPE AND DETERMINE WHAT TO DO NEXT
		        	
		        	if(commandType == CommandHeader.CMD_SEND_MSG)
		        	{
			        	// allocate buffer for the message object
			        	receivedMessage = new byte[header.payloadSize()];
			        	// get message
			        	buf_in_stream.read(receivedMessage);
			        	// convert the message buffer to message object
			        	msg =  AndroidMessage.bytesToAndroidMessage(receivedMessage);
			        	System.out.println("*******SEND Message: " + "From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.imageFileName());
			        	// write message from android client to DB
			        	database.storeUserMessage(msg);
		        	}
		        	else if(commandType == CommandHeader.CMD_READ_MSG)
		        	{
		        		// allocate buffer for the message object
			        	receivedMessage = new byte[header.payloadSize()];
			        	// get message
			        	buf_in_stream.read(receivedMessage);
			        	// convert the message buffer to message object
			        	msg =  AndroidMessage.bytesToAndroidMessage(receivedMessage);
			        	System.out.println("READ Message: " + "From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.imageFileName());
			        	
			        	// CHECK DB for message for this user
			        	System.out.println("check DB for user: " + msg.fromAddress());
			        	List<AndroidMessage> foundlist = database.getUserMessages(msg.fromAddress());
		        		if(foundlist.size() > 0)
		        		{
				        	// for now we just send test message
							//msg = CreateTestReadMessage(msg.fromAddress());
							while(foundlist.size() > 0)
							{
								// remove the message from the list and send it
								msg = foundlist.remove(0);
								Send(msg);
								
							}
		        		}
		        	}
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

}