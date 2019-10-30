package com.picto.ycpcs.myapplication;

public class PictoServerMain 
{ 
		   
	   public static void main(String[] args) {
		   PictoServer server = null;   
		// new thread for a server
		   server = new PictoServer();	
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
