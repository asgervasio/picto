package com.example.dev.picto;

public class SnapMain 
{ 
		   
	   public static void main(String[] args) {
		   SnapServer server = null;   
		// new thread for a server
		   server = new SnapServer();	
		   server.start();
		   
		   while(true)
		   {
			   try {
			   Thread.sleep(2000);
			   }
			   catch (Exception e)
			   {
				   
			   }
		   }
		   

	}//end main		   
} 
