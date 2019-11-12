package com.picto.ycpcs.myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PictoDB {
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   
	   static final String DB_URL = "jdbc:mysql://localhost/mysql";

	   //  Database credentials
	   static final String USER = "root"; //"username";
	   static final String PASS = "password";
	   Connection conn = null;
	   Statement stmt = null;
	   String pictoServerFolderName = "pictoserver";
	   String pictoDBFolderName = "pictodb";
	   String currentUsersHomeDir = "";
	   String serverFolder = "";
	   String serverDBFolder = "";
	
	   
	public PictoDB(String pictoServerFolderName,String pictoDBFolderName) {
		// TODO Auto-generated constructor stub
		this.pictoServerFolderName = pictoServerFolderName;
		this.pictoDBFolderName = pictoDBFolderName;
		
		currentUsersHomeDir = System.getProperty("user.home");
		serverFolder = currentUsersHomeDir + File.separator + pictoServerFolderName + "/";
		serverDBFolder = serverFolder + pictoDBFolderName + "/" ;
	
	}
	
	public void open()
	{
		//openDB();
	}
	
	public void close()
	{
		//closeDB();
	}
	
	// open Mysql DB and test the connection
	private synchronized void openDB()
	{
		closeDB(); // close the DB in case it is already opened
		
		conn = null;
		stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT User, Host, plugin FROM mysql.user";
		      
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		    	  
		    	 String user = rs.getString("User");
		    	 String host = rs.getString("Host");
		    	 String plugin = rs.getString("plugin");
		    	 
		    	//Display values
		         System.out.print("User: " + user);
		         System.out.print(", Host: " + host);
		         System.out.println(", plugin: " + plugin);

		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      if(stmt!=null)
		      {
		         stmt.close();
		         stmt = null;
		      }
		      //conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
			      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
			   
		      try
		      {
		         if(stmt!=null)
		            stmt.close();
		      }
		      catch(SQLException se2)
		      {
		      }// nothing we can do
		     
		   }//end try
		   
	}
	
	
	
	// close mysql DB
	private synchronized void closeDB()
	{
		try
		{
	       if(conn!=null)
	          conn.close();
	    }
		catch(SQLException se)
		{
	       // handle this error !!!
	    }//end finally try
	}
	
	public synchronized boolean isValidUserAccount(String user)
	{				
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		return(folder.exists());		
	}
	
	public synchronized boolean createUserAccount(String user)
	{
		boolean rv = false;
					
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		
		File folder = new File(userFolder);
		
		if(folder.exists() == false)
		{
			rv = folder.mkdir();
		}
		else
		{
			rv = true;
		}
		
		return rv;
	}
	
	

	// after message is copied from user inbox it is removed from the server
	// the server doesn't keep copies of user messages
	
	public synchronized List<PictoMessage> getUserMessages(String user)
	{
		List<PictoMessage> listOfMessages = new ArrayList<PictoMessage>();
		PictoMessage msg;
		// for now go to disk to get any messages for user message and image
		//
		
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		if(folder.exists() == false)
		{		
			createUserAccount(user);
		}
		
		
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			  if (listOfFiles[i].isFile()) 
			  {
			    System.out.println("File " + listOfFiles[i].getName());
			    Path thepath = Paths.get(listOfFiles[i].getAbsolutePath());
			    try
			    {
			    byte[] itemBytes = Files.readAllBytes(thepath);
			    msg = PictoMessage.bytesToPictoMessage(itemBytes);
			    listOfMessages.add(msg);
			    
			    // for now delete the file from disk but we might
			    // need to add it to a delete list in case the message
			    // send fails
			    Files.delete(thepath);
			    }
			    catch(Exception e)
			    {
			    	
			    }
			  } 
			  else if (listOfFiles[i].isDirectory()) 
			  {
			    System.out.println("Directory " + listOfFiles[i].getName());
			  }
			}
		// For DB add a entry for this file.
		
		return listOfMessages;
	}
	
	public synchronized void storeUserMessage(PictoMessage msg) throws Exception
	{		
		String userFolder = serverDBFolder + msg.toAddress().toLowerCase() +  "/";
		
		File theUserpath = new File(userFolder);
		if(theUserpath.exists() == false)
		{
			createUserAccount(msg.toAddress().toLowerCase());
		}
		
		// get UUID and remove the '-' chars to create a unique filename	
		String uuid = UUID.randomUUID().toString().replace("-", "");
		
		String full_path = userFolder + uuid;
		
		//byte[] msg_bytes = PictoMessage.pictoMessageToBytes(msg);
		try (FileOutputStream fos = new FileOutputStream(full_path)) 
		{
			byte[] msg_bytes = PictoMessage.pictoMessageToBytes(msg);
			fos.write(msg_bytes);
			   //There is no more need to call close() since 
			   //we created the instance of "fos" inside the try. 
			   // And this will automatically close the OutputStream
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}

}
