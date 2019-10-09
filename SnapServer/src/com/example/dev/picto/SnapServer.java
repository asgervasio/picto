package com.example.dev.picto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * 
Server thread

Wait for new client Accept 

If new client accept
Create new AndroidClient object
Start client thread



 */
public class SnapServer extends Thread {

	static final int PORT = 6970; // set port for server to listen on
	ServerSocket serverSocket = null;
    Socket socket = null;
    SnapDB database = null; 
  
    
	public SnapServer() {
		// TODO Auto-generated constructor stub
		System.out.println("Server attaching to DB ");
		database = new SnapDB();
		database.open();
	}
	
	public void run()
	{
		try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
            	System.out.println("Server waiting for client accept ");
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            System.out.println("Server created new Client thread ");
            // new thread for a client
            new SnapClientBinary(socket,database).start();
        }
	}
	
	public void cleanup()
	{
		database.close();
	}
	

}