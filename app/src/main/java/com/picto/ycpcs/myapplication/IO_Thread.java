package com.picto.ycpcs.myapplication;

import android.media.AudioManager;
import android.media.ToneGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class IO_Thread extends Thread {

	static final int PORT = 6970; // server port the client will connect with
    Socket socket = null;
    
    InputStream inp = null;
    BufferedInputStream brinp = null; // buffered input stream
    BufferedOutputStream out = null;  // buffered output stream
    byte[] readHeaderBuffer = null;  // byte buffer for the header used when reading messages
    
    ApplicationState appstate = null;  // reference to the applications global memory
    boolean io_running = true; // flag for the IO thread running status
    
	public IO_Thread() {
		// TODO Auto-generated constructor stub
		appstate = ApplicationState.getApplicationStateInstance();
		readHeaderBuffer = new byte[CommandHeader.HEADER_SIZE];

	}

    //
    // Create a new socket and connect to the server
	public boolean connect(String ip_address)
	{
		boolean rv = false;
		String errorStr = "";
		
		try 
		{
            // create a new socket
        	socket = new Socket(ip_address, PORT);

            // get input stream
        	inp = socket.getInputStream();
            brinp = new BufferedInputStream(inp);
            // get output stream
            out = new BufferedOutputStream(socket.getOutputStream());
            rv = true;
        } 
        catch (IOException e) 
        {
			errorStr = e.toString();
        }
		
		return rv;
	}

    // disconnect the socket
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
	public boolean SendMsgToServer(PictoMessage msg)
	{
		boolean rv = false;
		try 
        {
			// serialize the message to a byte array for sending
			byte[] messageBytes = PictoMessage.pictoMessageToBytes(msg);

            // create the SEND command header
            CommandHeader header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_SEND_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			// export and send the header bytes to the socket stream
			byte[] headerBytes = header.exportHeader();
			out.write(headerBytes);
        	out.flush();
        	// send the message bytes to the socket stream
        	out.write(messageBytes);
        	out.flush();

        	//System.out.println("Send Message - From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.contentFileName());

        	rv = true;
                                               
        } 
        catch (IOException e) 
        {
            //e.printStackTrace();
            
        }	
		
		return rv;
	}
	
	public PictoMessage ReceiveMsgFromServer()
	{
		
		int tryCount = 0;
		PictoMessage msg = null;
		byte[] receivedMessage;
		CommandHeader header;
		
		try 
        {
			// CREATE a message read request

			// Create a read message object, it is just an empty message with the from field set to the username requesting the mail
            PictoMessage message = new PictoMessage(appstate.username(),"","","");
			byte[] messageBytes = PictoMessage.pictoMessageToBytes(message);
            // create the read message command header
			header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_READ_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			byte[] headerBytes = header.exportHeader();
            // write the header
			out.write(headerBytes);
	    	out.flush();
	    	// write the message
	    	out.write(messageBytes);
	    	out.flush();
	    	
	    	Thread.sleep(100);
        }
		catch (Exception e) 
        {
            
            
        }

        // after we send the READ request, look for any received messages
		while(tryCount < 3)
		{
			try 
	        {
	        	// check for any recieved messages
	        	if(brinp.available() > 0)
	        	{
	        		// get the header data from the socket stread
		        	brinp.read(readHeaderBuffer);
                    // create a blank Command header object, it will be filled when we import the data we just read
		        	header = new CommandHeader(0,0,0,0,0);
                    // import the buffer we just read into the command header object
		        	header.importHeader(readHeaderBuffer);
		        	// allocate buffer for the message object
		        	receivedMessage = new byte[header.payloadSize()];
		        	// get message data from the stream
		        	brinp.read(receivedMessage);
		        	// convert the message buffer to message object
		        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);

		        	//System.out.println("Read - From: " + msg.fromAddress() + " To: " + msg.toAddress() + " Message text:  "+ msg.textMessage() + " image filename: " + msg.contentFileName());
		        	
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


    // SEND messages
    //S1. Get Message from Send Q
    //S2. Create header
    //S3. Create message
    //S4. Send Header over socket
    //S5. Send message over socket
    //S6. If messages in vector left to send Goto S1

    //  READ messages
    // request message from server
    // If message from server copy it to the message list
    // play beep to alert user of new message
    //

	public void run()
	{
		PictoMessage message;
		
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
					// copy message to received message List
                    appstate.addMessage(message.content(),message.textMessage());
                    // increment the new message counter
                    appstate.setNewMessagesCount(appstate.getNewMessagesCount() + 1);
                    Beep(); // alert user that a new message has been received
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

    // plays a single beep to the android speaker
    public static void Beep() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_PROP_BEEP);

    }

}
