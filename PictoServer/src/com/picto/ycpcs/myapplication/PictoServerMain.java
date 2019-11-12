package com.picto.ycpcs.myapplication;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;


public class PictoServerMain 
{ 	
	private static boolean server_running = true;
	private static BufferedReader reader = null;
	
	public static void main(String[] args) 
	{
	   PictoServer server = null; 
	   
	   // get buffered reader for keyboard input to be used to exit the server		   
	   reader =  new BufferedReader(new InputStreamReader(System.in)); 
	 
	   // get the process ID of this java instance so we can 
	   // use the process ID to kill server process
	   String procid = getProcessID();   
	   System.out.println("PID = " + procid);
	    		   
	// new thread for a server
	   server = new PictoServer();	
	   server.start();
	   
	   while(server_running == true)
	   {		   
		   try 
		   {
			   
			   Thread.sleep(2000);
			   
			   if(reader.ready())
		       {
				   System.out.println("User exit requested." );
				   server_running = false;				   
			   }
			  
		   }
		   catch (Exception e)
		   {
			   
		   }
	   }
	   
	   System.out.println("Picto Server stopped PID = " + procid);
	   System.exit(0);
		   

	}//end main		
	   
   public static String getProcessID()
   {
	   try
	   {
		   Vector<String> commands=new Vector<String>();
		   commands.add("/bin/bash");
		   commands.add("-c");
		   commands.add("echo $PPID");
		   ProcessBuilder pb=new ProcessBuilder(commands);

		   Process pr=pb.start();
		   pr.waitFor();
		   if (pr.exitValue()==0) 
		   {
		     BufferedReader outReader=new BufferedReader(new InputStreamReader(pr.getInputStream()));
		     return outReader.readLine().trim();
		   } 
		   else 
		   {
		     System.out.println("Error while getting PID");
		     return "";
		   }
		  
	   }
	   catch(Exception e)
	   {
		   
	   }
	   return "";
   }
	   
	   	   
	
} 
