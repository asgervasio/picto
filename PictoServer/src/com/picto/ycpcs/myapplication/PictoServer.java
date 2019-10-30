package com.picto.ycpcs.myapplication;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
	
  
    
	public PictoServer() {
		// TODO Auto-generated constructor stub
		// create ApplicationState object to hold all message queues and
		// global variables
		appstate = new ApplicationState(); 
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
	
	public void run()
	{
		try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (server_thread_running) {
            try {
            	System.out.println("Server waiting for client accept ");
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            System.out.println("Server created new Client thread ");
            // create new object with thread to service the new client
            new PictoClient(socket,database,appstate).start();
        }
	}
	
	public void cleanup()
	{
		database.close();
	}
	
	//
	// creates Picto Server and DB folders needed at runtime
	//
	public boolean createServerFolders()
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
