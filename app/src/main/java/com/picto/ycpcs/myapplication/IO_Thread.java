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
    boolean loggedin = false;
    
	public IO_Thread() {
		// TODO Auto-generated constructor stub
		appstate = ApplicationState.getApplicationStateInstance();
		readHeaderBuffer = new byte[CommandHeader.HEADER_SIZE];

	}

    //
    // Create a new socket and connect to the server
	public synchronized boolean connect(String ip_address)
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
            appstate.addStatusMessage(", iothread connect exception ");
            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", iothread connect exception ";
            rv = false;
            disconnect(true);
        }
		
		return rv;
	}

    // disconnect the socket
	public synchronized boolean disconnect(boolean showLogin)
	{
		boolean rv = false;

        loggedin = false; // we are tearing done the connection so clear the login

        //SoundConnectionLost();
        /*
        try {
            Beep();
            Thread.sleep(500);
            Beep();
            Thread.sleep(500);
            Beep();

        }
        catch(Exception e)
        {

        }

*/
        try
        {
            // close BufferedInputStream
            if(brinp != null)
            {
                brinp.close();
            }
            // close BufferedOutputStream
            if(out != null)
            {
                out.close();
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

        if(showLogin == true)
        {
            appstate.parent.startLoginActivity();
            Beep();
        }
        else
        {
            //Beep();
        }
		
		return rv;
	}
	
	// create header
	// send header
	// flush send
	
	// send serialized message
	// flush send
	public synchronized boolean SendMsgToServer(PictoMessage msg) throws Exception
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
        catch (Exception e)
        {
            //e.printStackTrace();
            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", SEND MSG exception ";
            appstate.addStatusMessage(", SEND MSG exception ");
            disconnect(true);
            //rv = false;
            throw e;
            
        }	
		
		return rv;
	}
	
	public synchronized PictoMessage ReceiveMsgFromServer() throws Exception
	{
		
		int tryCount = 0;
		PictoMessage msg = null;
		byte[] receivedMessage;
		CommandHeader header;
		
		try 
        {
			// CREATE a message read request
            appstate.addStatusMessage(", create READ MSG req ");
			// Create a read message object, it is just an empty message with the from field set to the username requesting the mail
            PictoMessage message = new PictoMessage(appstate.username(),"","","");
            appstate.addStatusMessage(", bfr pictoMessageToBytes ");
			byte[] messageBytes = PictoMessage.pictoMessageToBytes(message);
            appstate.addStatusMessage(", after pictoMessageToBytes ");
            // create the read message command header
			header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_READ_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
			byte[] headerBytes = header.exportHeader();
            appstate.addStatusMessage(", after exportHeader ");
            // write the header
			out.write(headerBytes);  // exception occurs here...
	    	out.flush();
            appstate.addStatusMessage(", after Header fl ");
	    	// write the message
	    	out.write(messageBytes);
	    	out.flush();
            appstate.addStatusMessage(", after msg fl ");
	    	
	    	Thread.sleep(100);
            appstate.addStatusMessage(", after sleep ");
        }
		catch (Exception e) 
        {
            appstate.addStatusMessage(", READ MSG request exception ");
            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", READ MSG request exception ";
            disconnect(true);
            throw e;
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
                    if(header.signature() != CommandHeader.SIGNATURE)
                    {
                        // corrupted header so flush the input stream.
                        appstate.addStatusMessage(", sig expected " + Integer.toHexString(CommandHeader.SIGNATURE));
                        appstate.addStatusMessage(", sig found " + Integer.toHexString(header.signature()));
                        int readsize = brinp.available();
                        if(readsize  > 0)
                        {
                            appstate.addStatusMessage("UNKNOWN stream not empty " + Integer.toString(readsize));
                            byte[] tempbuf = new byte[readsize];
                            brinp.read(tempbuf);
                            appstate.addStatusMessage("UNKNOWN stream read " );

                        }
                        msg = null;
                        break;

                    }
		        	// allocate buffer for the message object
		        	receivedMessage = new byte[header.payloadSize()];
		        	// get message data from the stream
		        	brinp.read(receivedMessage);
                    appstate.addStatusMessage(", before btm " + Integer.toString(receivedMessage.length));
                    //appstate.lastStatusMessage = appstate.lastStatusMessage + ", before btm " + Integer.toString(receivedMessage.length);
		        	// convert the message buffer to message object
		        	msg =  PictoMessage.bytesToPictoMessage(receivedMessage);
                    appstate.addStatusMessage(", after btm ");
                   // appstate.lastStatusMessage = appstate.lastStatusMessage + ", after btm ";
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
                appstate.addStatusMessage(", READ MSG receive exception ");
                //appstate.lastStatusMessage = appstate.lastStatusMessage + ", READ MSG receive exception ";
                disconnect(true);
                //break;
                throw e;
	            
	        }
		}
		
		return msg;
	}


    public synchronized boolean PingServer() throws Exception
    {
        boolean rv = false;
        int tryCount = 0;
        try
        {

            //byte[] messageBytes = PictoMessage.pictoMessageToBytes(msg);
            //CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_SEND_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
            CommandHeader header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_PING_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,0);

            byte[] headerBytes = header.exportHeader();
            out.write(headerBytes);
            out.flush();

            System.out.println("Ping Server ");


            // wait for server PING reply
            while(tryCount < 10)
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
                        switch(header.commandType())
                        {
                            case CommandHeader.CMD_PING_MSG:
                                System.out.println("PING reply");
                                break;
                            case CommandHeader.CMD_READ_MSG:
                                System.out.println("READ reply");
                                break;
                            case CommandHeader.CMD_SEND_MSG:
                                System.out.println("SEND reply");
                                break;
                            case CommandHeader.CMD_LOGIN_MSG:
                                System.out.println("LOGIN reply");
                                break;
                            default:
                                System.out.println("UNKNOWN reply = " + Integer.toString(header.commandType()));
                                break;
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
                    disconnect(true);
                    throw e;
                    //rv = false;
                    //break;

                }
            }

            rv = true;

        }
        catch (IOException e)
        {
            //e.printStackTrace();
            disconnect(true);
            //rv = false;
            throw e;

        }

        return rv;
    }

    // returns status
    public synchronized int LoginToServer(String username, String password,boolean newAccount) throws Exception
    {
        String  newAccountStr = "";
        int tryCount = 0;
        PictoMessage msg = null;
        //byte[] receivedMessage;
        CommandHeader header;
        int status = CommandHeader.STATUS_SUCCESS;
        loggedin = false;
        try
        {
            // CREATE a LOGIN request
            if(newAccount == true)
            {
                newAccountStr = "1";
            }
            else
            {
                newAccountStr = "0";
            }
            PictoMessage message = new PictoMessage(username,password,newAccountStr,"");
            byte[] messageBytes = PictoMessage.pictoMessageToBytes(message);
            header = new CommandHeader(CommandHeader.SIGNATURE,CommandHeader.CMD_LOGIN_MSG,CommandHeader.VERSION,CommandHeader.STATUS_SUCCESS,messageBytes.length);
            byte[] headerBytes = header.exportHeader();
            out.write(headerBytes);
            out.flush();

            out.write(messageBytes);
            out.flush();

            Thread.sleep(100);
        }
        catch (Exception e)
        {
            disconnect(true);
            throw e;
        }

        while(tryCount < 4)
        {
            try
            {

                // check for Login response message

                if(brinp.available() > 0)
                {
                    // get the header
                    brinp.read(readHeaderBuffer);
                    header = new CommandHeader(0,0,0,0,0);
                    header.importHeader(readHeaderBuffer);
                    switch(header.commandType())
                    {
                        case CommandHeader.CMD_LOGIN_MSG:

                            status = header.commandStatus();
                            System.out.println("LOGIN reply, status = " + String.valueOf(status));
                            if(status == CommandHeader.STATUS_SUCCESS)
                            {
                                loggedin = true;
                                appstate.username(username);
                            }
                            break;
                        case CommandHeader.CMD_PING_MSG:
                            System.out.println("PING reply");
                            break;
                        case CommandHeader.CMD_READ_MSG:
                            System.out.println("READ reply");
                            break;
                        case CommandHeader.CMD_SEND_MSG:
                            System.out.println("SEND reply");
                            break;
                        default:
                            System.out.println("UNKNOWN reply");
                            break;
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
                disconnect(true);
                //break;
                throw e;
            }
        }

        return status;
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
        int readcounter = 0;
		
		while(io_running == true)
		{
			try 
			{
				//Thread.sleep(500);

                if(loggedin == true)
                {
                    // get next message from inbound message queue
                    message = appstate.getNextSendMessageItem();
                    if(message != null)
                    {
                        // send the message to socket
                        SendMsgToServer(message);
                    }


                    //Thread.sleep(500);

                    if(readcounter > 3) // this slows the READ poll rate to the server
                    {
                        readcounter = 0;
                        // check if messages to receive
                        message = ReceiveMsgFromServer(); // receive messages from server to android client
                        if (message != null) {
                            // if messages to receive
                            // copy message to received message List
                            appstate.addStatusMessage(", add msg ");
                            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", add msg ";
                            appstate.addMessage(message.content(), message.textMessage(),appstate.picto_msg_type_Received,message.contentSettings());
                            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", aft add msg ";
                            appstate.addStatusMessage(", aft add msg ");
                            appstate.addStatusMessage(", msgtimeout =  " + message.contentSettings());
                            // increment the new message counter
                            appstate.setNewMessagesCount(appstate.getNewMessagesCount() + 1);
                            appstate.addStatusMessage(", set m cnt ");
                            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", set m cnt ";
                            Beep(); // alert user that a new message has been received
                            //appstate.lastStatusMessage = appstate.lastStatusMessage + ", aft bep ";
                            appstate.addStatusMessage(", aft beep ");
                        }

                    }
                    readcounter++;
                }
                else
                {

                }
								
				Thread.sleep(500);
						
			}
			catch (Exception e)
			{
                appstate.addStatusMessage(", IO thread exception ");
                //appstate.lastStatusMessage = appstate.lastStatusMessage + ", IO thread exception ";
				//return;
                break;
			}
		}
        // if the thread exits then there must of been a problem communicating with the server.
        appstate.addStatusMessage(", IO thread ended ");
        //appstate.lastStatusMessage = appstate.lastStatusMessage + ", IO thread ended ";
        disconnect(true); // disconnect and show login activty
		
	}

	
	public synchronized void cleanup()
	{
		
	}

    // plays a single beep to the android speaker
    public synchronized static void Beep() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_PROP_BEEP);

    }
/*
    // plays a single beep to the android speaker
    public static void SoundConnectionLost() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        try {
            toneG.startTone(ToneGenerator.TONE_DTMF_D);
            Thread.sleep(100);
            toneG.startTone(ToneGenerator.TONE_DTMF_3);
            Thread.sleep(100);
            toneG.startTone(ToneGenerator.TONE_DTMF_A);
            Thread.sleep(100);
            toneG.startTone(ToneGenerator.TONE_DTMF_D);
        }
        catch(Exception e)
        {

        }

    }
    */

}
