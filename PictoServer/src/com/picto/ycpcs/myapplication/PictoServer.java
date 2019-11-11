package com.picto.ycpcs.myapplication;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
*/
/*
 * 
Server thread

Wait for new client Accept 

If new client accept
Create new PictoClient object
Start client thread



 */
public class PictoServer extends Thread {

	static final int PORT = 6970; // set port for server to listen on
	ServerSocket serverSocket = null;
    Socket socket = null;
    PictoDB database = null; 
    ApplicationState appstate = null;
    static final String pictoServerFolderName = "pictoserver";
    static final String pictoDBFolderName = "pictodb";
    static boolean server_thread_running = true;
    String currentUsersHomeDir = "";
	String serverFolder = "";
	String serverDBFolder = "" ;
	ArrayList<PictoClient> clientList;
	int clientCount = 0; // increments each time server accepts a client socket.
	 
    
	public PictoServer() {
		//CreateTestReadMessage("bob");
		// TODO Auto-generated constructor stub
		// create ApplicationState object to hold all message queues and
		// global variables
		appstate = new ApplicationState(); 
		
		clientList = new ArrayList<PictoClient>(); // Create an ArrayList PictoClients
		
		if(createServerFolders() == true)
		{
			System.out.println("Server attaching to DB ");
			database = new PictoDB(pictoServerFolderName,pictoDBFolderName);
			database.open();
		}
		else
		{
			System.out.println("Server folder creation failed. Aborting... ");
			server_thread_running = false;
		}
	}
	
	public PictoMessage CreateTestReadMessage(String inboxName)
	{
		PictoMessage message = new PictoMessage("Ed",inboxName,"message for you","messagetest.txt");		
		PictoMessage message2 = null;
/*		
		byte[] image1 = new byte[10];
		
		for(int x = 0; x<10; x++)
		{
			image1[x] = 'C';
		}		
		
		message.content(image1);
*/		
		try 
		{
		byte[] buffer = PictoMessage.pictoMessageToBytes(message);
		//byte[] buffer = message.pictoMessageToBytes();
		message2 = PictoMessage.bytesToPictoMessage(buffer);
		System.out.println("new message from = " + message2.fromAddress() );
		}
		catch (Exception e)
		{
			
		}
		
		return message;
	}
	
	public synchronized ArrayList<PictoClient> getClientList()
	{
		return(clientList);
	}
	
	public synchronized void removeClient(int clientID)
	{
		System.out.println("Server removing PictoClient ID = " + Integer.toString(clientID) );
		// look up the client based on its ID
		for(int index = 0; index < clientList.size(); index++)
		{
			if(clientList.get(index).clientID() == clientID)
			{
				clientList.get(index).disconnect();
				clientList.remove(index);
				System.out.println("Server removed PictoClient ID = " + Integer.toString(clientID)  + " ClientList size = " + Integer.toString(clientList.size()));
				break;
			}
		}
		// close the connection 
		// remove the client object form the list
	}
	
	
	public synchronized void removeOtherClients(String username, int clientID)
	{
		System.out.println("Server try removing PictoClient username = " + username + " ID != " + Integer.toString(clientID) );
		// look up the client based on its ID
		for(int index = 0; index < clientList.size(); index++)
		{
			if(clientList.get(index).username().equals(username) == true)
			{
				if(clientList.get(index).clientID() != clientID)
				{
					System.out.println("Server removed PictoClient username = " + username + " ID = " + Integer.toString(clientList.get(index).clientID())  + " ClientList size = " + Integer.toString(clientList.size() - 1));
				
					clientList.get(index).disconnect();
					clientList.remove(index);
					
					return;
				}
			}
		}
		System.out.println("Server no duplicates found for PictoClient username = " + username + " ID != " + Integer.toString(clientID) );
		// close the connection 
		// remove the client object form the list
	}
	
	public void run()
	{
		try 
		{
            serverSocket = new ServerSocket(PORT);
        } 
		catch (IOException e) 
		{
            e.printStackTrace();

        }
        while (server_thread_running) 
        {
            try 
            {
            	System.out.println("Server waiting for client accept ");
                socket = serverSocket.accept();
            } 
            catch (IOException e) 
            {
                System.out.println("I/O error: " + e);
            }
            System.out.println("Server created new Client thread ");
            try
            {
	            // create new object with thread to service the new client
	            PictoClient newClient = new PictoClient(this,socket,database,appstate);
	            
	            newClient.clientID(++clientCount); // give the client a unique ID 
	            
	            clientList.add(newClient); // the the client to our list
	            
	            newClient.start(); // start the client thread
	            System.out.println("Server added PictoClient ID = " + Integer.toString(clientCount) );
            }
            catch(Exception e)
            {
            	System.out.println("Server PictoClient create exception");           	
            }
        }
	}
	
	public synchronized void cleanup()
	{
		database.close();
	}
	
	//
	// creates Picto Server and DB folders needed at runtime
	//
	public synchronized boolean createServerFolders()
	{
		boolean rv = false;
		
		currentUsersHomeDir = System.getProperty("user.home");
		serverFolder = currentUsersHomeDir + File.separator + pictoServerFolderName + "/";
		serverDBFolder = serverFolder + pictoDBFolderName + "/" ;
		
		File server_folder = new File(serverFolder);
		if(server_folder.exists() == false)
		{
			rv = server_folder.mkdir();
		}
		else
		{
			rv = true;
		}
		
		if(rv == true)
		{
			File serverDB_folder = new File(serverDBFolder);
			
			if(serverDB_folder.exists() == false)
			{
				rv = serverDB_folder.mkdir();
			}
			else
			{
				rv = true;
			}
		}
				
		
		return rv;
	}
	

}
