package com.picto.ycpcs.myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
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
public class PictoClient extends Thread {

	PictoServer parent = null;
	protected Socket socket; // gets passed in by the server
	InputStream in_stream = null;
    BufferedInputStream buf_in_stream = null;
    BufferedOutputStream buf_out_stream = null;
    byte[] readHeaderBuffer = null;
    boolean io_running = true;
    ApplicationState appstate = null;
    PictoDB database = null;
    String username = null; // this gets set when user logs in
    String password = null;
    int clientID = 0;
    Date activityTime = null;
	
	public PictoClient(PictoServer parent, Socket clientSocket,PictoDB database,ApplicationState appstate) throws Exception
	{
		// TODO Auto-generated constructor stub
		//appstate = new ApplicationState();
		activityTime = new Date();
		this.parent = parent;
		this.appstate = appstate;
		this.socket = clientSocket;
		this.database = database;
		readHeaderBuffer = new byte[CommandHeader.HEADER_SIZE];
		
		try 
		{
			in_stream = socket.getInputStream();
			buf_in_stream = new BufferedInputStream(in_stream);
           
			buf_out_stream = new BufferedOutputStream(socket.getOutputStream());
        } 
		catch (Exception e) 
		{
            //return;
			disconnect();
			throw e;
        }
		
		// read all messages from the DB that need to be sent to this client
		// write these messages to the send message Q
		// we will not delete the message from the DB but will instead mark it as
		// delivered. The message will be deleted by the server when it's life timer expires
	}
	
	public synchronized boolean disconnect()
	{
		boolean rv = false;
		io_running = false; // end thread loop
		System.out.println("Picto Client disconnect" );
		try 
		{
			// close BufferedInputStream
			if(buf_in_stream != null)
			{
				buf_in_stream.close();
			}
			// close BufferedOutputStream
			if(buf_out_stream != null)
			{
				buf_out_stream.close();
			}
			// close socket
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

	public synchronized void clientID(int clientID)
	{
		this.clientID = clientID;
	}
	
	public synchronized int clientID()
	{
		return clientID;
	}
	
	public synchronized void username(String username)
	{
		this.username = username;
	}
	
	public synchronized String username()
	{
		return username;
	}
	
	// the run thread does the following
	// 1. Looks for any inactive clients and removes them from the servers client list
	// 2. check for any received message requests
	// 3. if there is a READ request within ReceiveMsg() we send any stored messages
	public void run()
	{
		PictoMessage message;
		// check send message Q for messages to send
		// send any messages
		//
		while(io_running == true)
		{
			if(inactivityTimeout() == true)
			{				
				parent.removeClient(this.clientID);
				break;
			}
			try 
			{
				Thread.sleep(500);
				
				// read messages from socket
				//message = ReceiveMsg(); //check for a message
				ProcessCommand();
	
				
			}
			catch (Exception e)
			{
				io_running = false;
				//parent.removeClient(this.clientID);	   
			}
		}
		
		
	}
	public synchronized boolean inactivityTimeout()
	{
		boolean rv = false;
		Date currentTime = new Date();
		long diffMs = currentTime.getTime() - activityTime.getTime();
		long diffSec = diffMs / 1000;
		//long min = diffSec / 60;
		if(diffSec > 120)
		{			
			rv = true;
		}
		return rv;
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
	public synchronized boolean Send(PictoMessage msg)
	{
		boolean rv = false;
		try 
        {
			activityTime = new Date();
			byte[] messageBytes = msg.pictoMessageToBytes(msg);
			//byte[] messageBytes = PictoMessage.pictoMessageToBytes(msg);
			
			CommandHeader header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_SEND_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			
			byte[] headerBytes = header.exportHeader();
			buf_out_stream.write(headerBytes);
			buf_out_stream.flush();
        	
			buf_out_stream.write(messageBytes);
			buf_out_stream.flush();
			
        	// wait for server ack
			//System.out.println("server send Message " + msg.imageFileName());
			
			
			System.out.println("Send Message - From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image settings: " + msg.contentSettings());
			if(msg.content() != null)
			{
			    System.out.println("Send Message content size = " + Integer.toString(msg.content().length));
			}
			else
			{
				System.out.println("Send Message content is NULL!!!!! " );
			}
        	rv = true;
                                               
        } 
        catch (Exception e) 
        {
            //e.printStackTrace();
        	io_running = false;
			parent.removeClient(this.clientID);
            
        }	
		
		return rv;
	}
	
	public synchronized String getCommandString(int type)
	{
		String rv = "";
		
		switch(type)
		{
			case CommandHeader.CMD_LOGIN_MSG:
				rv = "LOGIN";
				break;
			case CommandHeader.CMD_SEND_MSG:
				rv = "SEND";
				break;
			case CommandHeader.CMD_READ_MSG:
				rv = "READ";
				break;
			case CommandHeader.CMD_PING_MSG:
				rv = "PING";
				break;
			default:
				rv = "UNKNOWN = " + Integer.toString(type);
				
				break;
		}
		return rv;
		
	}
	
	public synchronized boolean isValidCommand(int type)
	{
		boolean rv = true;
		
		switch(type)
		{
			case CommandHeader.CMD_LOGIN_MSG:				
			case CommandHeader.CMD_SEND_MSG:				
			case CommandHeader.CMD_READ_MSG:				
			case CommandHeader.CMD_PING_MSG:
				rv = true;
				break;
			default:
				rv = false;				
				break;
		}
		return rv;
		
	}
	
	/*
	 * This method processes a single command header and command if bytes are detected in the socket
	 * stream and then returns
	 * 
	1. Wait for command header
	2. Check if command header size or more bytes is available. If not then sleep for 100ms and recheck
	3. Read command header size bytes for header
	4. Validate header. if invalid then throw away all bytes in the stream and goto step 1
	5. Get payload size from header
	6. Check if payload size is available. If not then sleep for 100ms and recheck
	7. Read payload
	8. The command type is part of the command header to process the payload based on it's type
	9. goto step 1

	 */
	
	public synchronized PictoMessage ProcessCommand()
	{				
		PictoMessage msg = null;
		byte[] receivedMessage;
		CommandHeader header;
		int readsize;
		int availSize;
		
		try 
		{	
				        		        	
        	if(buf_in_stream.available() > 0)
        	{
        		while((availSize = buf_in_stream.available()) < CommandHeader.HEADER_SIZE)
        		{
        			System.out.println("!!!!!!!!!!!!! COMMAND header not enough bytes, available size = " + availSize + " expecting size = " + CommandHeader.HEADER_SIZE);
        			Thread.sleep(100);
        		}
        		activityTime = new Date();
        		// get the header
        		readsize = buf_in_stream.read(readHeaderBuffer);
	        	header = new CommandHeader(0,0,0,0,0);
	        	header.importHeader(readHeaderBuffer);
	        	int commandType = header.commandType();
	        	String byteString = appstate.bytesToHex(readHeaderBuffer);
	        	System.out.println("Read COMMAND Header buffer: " + byteString);
	        	System.out.println("Read COMMAND Header - Type: " + getCommandString(commandType)  + " Readsize = " + Integer.toString(readsize));
	        	System.out.println("PictoClient ID = " + Integer.toString(clientID)  + " ClientList size = " + Integer.toString(parent.getClientList().size()));
	        	if((header.signature() != CommandHeader.SIGNATURE) || (isValidCommand(commandType) == false))
                {
	        		System.out.println("Invalid COMMAND Header or signature found Type: " + Integer.toString(commandType) + " flushing stream");
                    // corrupted header so flush the input stream.
	        		System.out.println(", signature expected " + Integer.toHexString(CommandHeader.SIGNATURE));
	        		System.out.println(", signature found " + Integer.toHexString(header.signature()));
                    readsize = buf_in_stream.available();
                    if(readsize  > 0)
                    {
                    	System.out.println("corrupted stream not empty " + Integer.toString(readsize));
                        byte[] tempbuf = new byte[readsize];
                        buf_in_stream.read(tempbuf);
                        System.out.println("corrupted stream read " );

                    }
                    msg = null;
                    return(msg);

                }
	        	else
	        	{
	        		System.out.println("Read COMMAND Header - Type: " + getCommandString(commandType)  + " validated ");
	        	}
	        	
	        	// if we get to this point we have a header that looks valid
	        	if(header.payloadSize() > 0)
	        	{
	        		int trycount = 0;
	        		System.out.println("expecting payload of size = " + Integer.toString(header.payloadSize()));
	        		while((availSize = buf_in_stream.available()) < header.payloadSize())
	        		{
	        			System.out.println("!!!!!!!!!!!!! CMD payload not enough bytes, available size = " + availSize + " expected PAYLOAD size = " + header.payloadSize());
	        			Thread.sleep(500);
	        			trycount++;
	        			if(trycount > 5)
	        			{
	        				System.out.println("CMD payload wait failed." );
	        				msg = null;
	                        return(msg);
	        			}
	        		}
	        	}
	        	
	        	// GET THE TYPE AND DETERMINE WHAT TO DO NEXT
	        	
	        	if(commandType == CommandHeader.CMD_SEND_MSG)
	        	{
		        	// allocate buffer for the message object
	        		System.out.println("CMD_SEND_MSG PAYLOAD size = " + header.payloadSize());
/*	        		
	        		while((availSize = buf_in_stream.available()) < header.payloadSize())
	        		{
	        			System.out.println("!!!!!!!!!!!!! CMD_SEND_MSG not enough bytes, available size = " + availSize + " PAYLOAD size = " + header.payloadSize());
	        			Thread.sleep(100);
	        		}
	        		*/
		        	receivedMessage = new byte[header.payloadSize()];
		        	// get message
		        	readsize = buf_in_stream.read(receivedMessage);
		        	System.out.println("CMD_SEND_MSG Readsize = " + Integer.toString(readsize));
		        	// convert the message buffer to message object
		        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
		        	if(msg == null)
		        	{
		        		System.out.println("bytesToPictoMessage returns null, message ignored " );
		        		return null;
		        	}
		        	System.out.println("*******SEND Message: " + "From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image settings: " + msg.contentSettings());
		        	System.out.println("CMD_SEND_MSG content size = " + Integer.toString(msg.content().length));
		        	// write message from android client to DB
		        	database.storeUserMessage(msg);
	        	}
	        	else if(commandType == CommandHeader.CMD_READ_MSG)
	        	{
	        		// allocate buffer for the message object
	        		System.out.println("CMD_READ_MSG PAYLOAD size = " + header.payloadSize());
		        	receivedMessage = new byte[header.payloadSize()];
		        	// get message
		        	readsize = buf_in_stream.read(receivedMessage);
		        	System.out.println("CMD_READ_MSG Readsize = " + Integer.toString(readsize));
		        	// convert the message buffer to message object
		        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
		        	if(msg == null)
		        	{
		        		System.out.println("bytesToPictoMessage returns null, message ignored " );
		        		return null;
		        	}
		        	System.out.println("READ Message: " + "From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image settings: " + msg.contentSettings());
		        	
		        	// CHECK DB for message for this user
		        	System.out.println("check DB for user: " + msg.fromAddress());
		        	List<PictoMessage> foundlist = database.getUserMessages(msg.fromAddress());
	        		if(foundlist.size() > 0)
	        		{
			        	// User requested their mail to iterate any messages
	        			// in the users mailbox and send them.
						while(foundlist.size() > 0)
						{
							// remove the message from the list and send it
							msg = foundlist.remove(0);
							Send(msg);
							
						}
	        		}
	        	}
	        	else if(commandType == CommandHeader.CMD_PING_MSG)
	        	{
	        		header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_PING_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,0);
	    			
	    			byte[] headerBytes = header.exportHeader();
	    			buf_out_stream.write(headerBytes);
	    			buf_out_stream.flush();		    		
	            	
	            	System.out.println("Ping Reply ");
	        	}
	        	else if(commandType == CommandHeader.CMD_LOGIN_MSG)
	        	{
	        		// allocate buffer for the message object
		        	receivedMessage = new byte[header.payloadSize()];
		        	// get message
		        	buf_in_stream.read(receivedMessage);
		        	// convert the message buffer to message object
		        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
		        	if(msg == null)
		        	{
		        		System.out.println("bytesToPictoMessage returns null, message ignored " );
		        		return null;
		        	}
		        	System.out.println("lOGIN Message: " + "From: " + msg.fromAddress() + " Password: " + msg.toAddress() + " create account:  "+ msg.textMessage() );
		        	username = msg.fromAddress(); // username is in the from address.
		        	password = msg.toAddress(); // password is in the to address.
		        	int createAccount = 0;
		        	if(msg.textMessage().equals("1") == true)
		        	{
		        		createAccount = 1;
		        	}
		        	
		        	int loginResponse; 
		        	if(database.isValidUserAccount(username) == true)
		        	{
		        		if(createAccount == 0)
		        		{
		        		loginResponse = CommandHeader.STATUS_SUCCESS;
		        		}
		        		else
		        		{
		        			// account already exists and create flag was set
		        			loginResponse = CommandHeader.STATUS_ERROR_LOGIN_USERNAME_USED;
		        		}
		        	}
		        	else
		        	{
		        		// if client wants to create a new account
		        		if(createAccount == 1)
		        		{
		        			// create a new user account
		        			database.createUserAccount(username);
		        			loginResponse = CommandHeader.STATUS_SUCCESS;
		        		}
		        		else
		        		{
		        			// no such user account so return error
		        			loginResponse = CommandHeader.STATUS_ERROR_USER_NOT_FOUND;
		        		}
		        	}
	        		header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_LOGIN_MSG,CommandHeader.VERSION,loginResponse,0);
	    			
	    			byte[] headerBytes = header.exportHeader();
	    			buf_out_stream.write(headerBytes);
	    			buf_out_stream.flush();		    		
	    			msg = null;
	            	System.out.println("LOGIN Reply ");
	            	
	            	// if this was a successful login
	            	// remove any other connections with the same username but 
	            	// a different client ID.
	            	if(loginResponse == CommandHeader.STATUS_SUCCESS)
	            	{
	            		parent.removeOtherClients(username, clientID);
	            	}
	        	}
	        	else
	        	{
	        		// garbage found in stream so clear the stream by reading it.
	        		readsize = buf_in_stream.available();
	        		if(readsize  > 0)
	        		{
	        			System.out.println("UNKNOWN stream not empty " + Integer.toString(readsize));
	        			byte[] tempbuf = new byte[readsize];
	        			buf_in_stream.read(tempbuf);
	        			System.out.println("UNKNOWN stream read " );
	        		}
	        		else
	        		{
	        			System.out.println("UNKNOWN msg remove client, kill thread " );
		        		// unknown message type so disconnect the connection
		        		io_running = false;
						parent.removeClient(this.clientID);
	        		}
					
		        	msg = null;
	        	}
	        	
        	       	
        	}
        	else
        	{
        		// no bytes detected in the stream so we are done
        		
        	}
        	
        	
                                               
        } 
        catch (Exception e) 
        {
        	io_running = false;
			parent.removeClient(this.clientID);
			
        	msg = null;
            
        }
		
		
		return msg;
	}
	
	public synchronized PictoMessage ReceiveMsg()
	{				
		PictoMessage msg = null;
		byte[] receivedMessage;
		CommandHeader header;
		int readsize;
				
			try 
	        {	
					        		        	
	        	if(buf_in_stream.available() > 0)
	        	{
	        		activityTime = new Date();
	        		// get the header
	        		readsize = buf_in_stream.read(readHeaderBuffer);
		        	header = new CommandHeader(0,0,0,0,0);
		        	header.importHeader(readHeaderBuffer);
		        	int commandType = header.commandType();
		        	String byteString = appstate.bytesToHex(readHeaderBuffer);
		        	System.out.println("Read COMMAND Header buffer: " + byteString);
		        	System.out.println("Read COMMAND Header - Type: " + getCommandString(commandType)  + " Readsize = " + Integer.toString(readsize));
		        	System.out.println("PictoClient ID = " + Integer.toString(clientID)  + " ClientList size = " + Integer.toString(parent.getClientList().size()));
		        	// GET THE TYPE AND DETERMINE WHAT TO DO NEXT
		        	
		        	if(commandType == CommandHeader.CMD_SEND_MSG)
		        	{
			        	// allocate buffer for the message object
		        		System.out.println("CMD_SEND_MSG PAYLOAD size = " + header.payloadSize());
		        		int availSize;
		        		while((availSize = buf_in_stream.available()) < header.payloadSize())
		        		{
		        			System.out.println("!!!!!!!!!!!!! CMD_SEND_MSG not enough bytes, available size = " + availSize + " PAYLOAD size = " + header.payloadSize());
		        			Thread.sleep(100);
		        		}
			        	receivedMessage = new byte[header.payloadSize()];
			        	// get message
			        	readsize = buf_in_stream.read(receivedMessage);
			        	System.out.println("CMD_SEND_MSG Readsize = " + Integer.toString(readsize));
			        	// convert the message buffer to message object
			        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
			        	if(msg == null)
			        	{
			        		System.out.println("bytesToPictoMessage returns null, message ignored " );
			        		return null;
			        	}
			        	System.out.println("*******SEND Message: " + "From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.contentSettings());
			        	System.out.println("CMD_SEND_MSG content size = " + Integer.toString(msg.content().length));
			        	// write message from android client to DB
			        	database.storeUserMessage(msg);
		        	}
		        	else if(commandType == CommandHeader.CMD_READ_MSG)
		        	{
		        		// allocate buffer for the message object
		        		System.out.println("CMD_READ_MSG PAYLOAD size = " + header.payloadSize());
			        	receivedMessage = new byte[header.payloadSize()];
			        	// get message
			        	readsize = buf_in_stream.read(receivedMessage);
			        	System.out.println("CMD_READ_MSG Readsize = " + Integer.toString(readsize));
			        	// convert the message buffer to message object
			        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
			        	if(msg == null)
			        	{
			        		System.out.println("bytesToPictoMessage returns null, message ignored " );
			        		return null;
			        	}
			        	System.out.println("READ Message: " + "From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image settings: " + msg.contentSettings());
			        	
			        	// CHECK DB for message for this user
			        	System.out.println("check DB for user: " + msg.fromAddress());
			        	List<PictoMessage> foundlist = database.getUserMessages(msg.fromAddress());
		        		if(foundlist.size() > 0)
		        		{
				        	// User requested their mail to iterate any messages
		        			// in the users mailbox and send them.
							while(foundlist.size() > 0)
							{
								// remove the message from the list and send it
								msg = foundlist.remove(0);
								Send(msg);
								
							}
		        		}
		        	}
		        	else if(commandType == CommandHeader.CMD_PING_MSG)
		        	{
		        		header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_PING_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,0);
		    			
		    			byte[] headerBytes = header.exportHeader();
		    			buf_out_stream.write(headerBytes);
		    			buf_out_stream.flush();		    		
		            	
		            	System.out.println("Ping Reply ");
		        	}
		        	else if(commandType == CommandHeader.CMD_LOGIN_MSG)
		        	{
		        		// allocate buffer for the message object
			        	receivedMessage = new byte[header.payloadSize()];
			        	// get message
			        	buf_in_stream.read(receivedMessage);
			        	// convert the message buffer to message object
			        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
			        	if(msg == null)
			        	{
			        		System.out.println("bytesToPictoMessage returns null, message ignored " );
			        		return null;
			        	}
			        	System.out.println("lOGIN Message: " + "From: " + msg.fromAddress() + " Password: " + msg.toAddress() + " create account:  "+ msg.textMessage() );
			        	username = msg.fromAddress(); // username is in the from address.
			        	password = msg.toAddress(); // password is in the to address.
			        	int createAccount = 0;
			        	if(msg.textMessage().equals("1") == true)
			        	{
			        		createAccount = 1;
			        	}
			        	
			        	int loginResponse; 
			        	if(database.isValidUserAccount(username) == true)
			        	{
			        		if(createAccount == 0)
			        		{
			        		loginResponse = CommandHeader.STATUS_SUCCESS;
			        		}
			        		else
			        		{
			        			// account already exists and create flag was set
			        			loginResponse = CommandHeader.STATUS_ERROR_LOGIN_USERNAME_USED;
			        		}
			        	}
			        	else
			        	{
			        		// if client wants to create a new account
			        		if(createAccount == 1)
			        		{
			        			// create a new user account
			        			database.createUserAccount(username);
			        			loginResponse = CommandHeader.STATUS_SUCCESS;
			        		}
			        		else
			        		{
			        			// no such user account so return error
			        			loginResponse = CommandHeader.STATUS_ERROR_USER_NOT_FOUND;
			        		}
			        	}
		        		header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_LOGIN_MSG,CommandHeader.VERSION,loginResponse,0);
		    			
		    			byte[] headerBytes = header.exportHeader();
		    			buf_out_stream.write(headerBytes);
		    			buf_out_stream.flush();		    		
		    			msg = null;
		            	System.out.println("LOGIN Reply ");
		            	
		            	// if this was a successful login
		            	// remove any other connections with the same username but 
		            	// a different client ID.
		            	if(loginResponse == CommandHeader.STATUS_SUCCESS)
		            	{
		            		parent.removeOtherClients(username, clientID);
		            	}
		        	}
		        	else
		        	{
		        		// garbage found in stream so clear the stream by reading it.
		        		readsize = buf_in_stream.available();
		        		if(readsize  > 0)
		        		{
		        			System.out.println("UNKNOWN stream not empty " + Integer.toString(readsize));
		        			byte[] tempbuf = new byte[readsize];
		        			buf_in_stream.read(tempbuf);
		        			System.out.println("UNKNOWN stream read " );
		        		}
		        		else
		        		{
		        			System.out.println("UNKNOWN msg remove client, kill thread " );
			        		// unknown message type so disconnect the connection
			        		io_running = false;
							parent.removeClient(this.clientID);
		        		}
						
			        	msg = null;
		        	}
		        	
	        	       	
	        	}
	        	else
	        	{
	        		//tryCount++;
	        		
	        	}
	        	
	        	//Thread.sleep(500);
	                                               
	        } 
	        catch (Exception e) 
	        {
	        	io_running = false;
				parent.removeClient(this.clientID);
				
	        	msg = null;
	            
	        }
		
		
		return msg;
	}

}
