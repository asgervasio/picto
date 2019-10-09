package com.example.dev.picto;

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

public class SnapDB {
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   
	   static final String DB_URL = "jdbc:mysql://localhost/mysql";

	   //  Database credentials
	   static final String USER = "root"; //"username";
	   static final String PASS = "password";
	   Connection conn = null;
	   Statement stmt = null;
	   
	public SnapDB() {
		// TODO Auto-generated constructor stub
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
	private void openDB()
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
		   System.out.println("Goodbye!");	
	}
	
	
	
	// close mysql DB
	private void closeDB()
	{
		try
		{
	       if(conn!=null)
	          conn.close();
	    }
		catch(SQLException se)
		{
	       //se.printStackTrace();
	    }//end finally try
	}
	
	public boolean isValidUserAccount(String user)
	{		
		
		String currentUsersHomeDir = System.getProperty("user.home");
		String userFolder = currentUsersHomeDir + File.separator + "snapserver/snapdb/" + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		return(folder.exists());		
	}
	
	public boolean createUserAccount(String user)
	{
		boolean rv = false;
		
		String currentUsersHomeDir = System.getProperty("user.home");
		String serverFolder = currentUsersHomeDir + File.separator + "snapserver/";
		String serverDBFolder = currentUsersHomeDir + File.separator + "snapserver/snapdb/" ;
		String userFolder = currentUsersHomeDir + File.separator + "snapserver/snapdb/" + user.toLowerCase()  + "/";
		
		File server_folder = new File(serverFolder);
		if(server_folder.exists() == false)
		{
			rv = server_folder.mkdir();
		}
		
		File serverDB_folder = new File(serverDBFolder);
		
		if(serverDB_folder.exists() == false)
		{
			rv = serverDB_folder.mkdir();
		}
		
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
	
	
	//
	// Folder: home/test/snapdb/username
	// contains 1 file per message
	// file 1: message file containing the message
	// after message is copied from user inbox it is removed from the server
	// the server doesn't keep copies of user messages
	
	public List<AndroidMessage> getUserMessages(String user)
	{
		List<AndroidMessage> listOfMessages = new ArrayList<AndroidMessage>();
		AndroidMessage msg;
		// for now go to disk to get any messages for user message and image
		//
		String currentUsersHomeDir = System.getProperty("user.home");
		String userFolder = currentUsersHomeDir + File.separator + "snapserver/snapdb/" + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		if(folder.exists() == false)
		{
			//folder.mkdir();
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
			    msg = AndroidMessage.bytesToAndroidMessage(itemBytes);
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
	
	public void storeUserMessage(AndroidMessage msg)
	{
		String currentUsersHomeDir = System.getProperty("user.home");
		String userFolder = currentUsersHomeDir + File.separator + "snapserver/snapdb/" + msg.toAddress().toLowerCase() +  "/";
		
		File theUserpath = new File(userFolder);
		if(theUserpath.exists() == false)
		{
			//theUserpath.mkdir();
			createUserAccount(msg.toAddress().toLowerCase());
		}
		
			
		String uuid = UUID.randomUUID().toString().replace("-", "");
		
		String full_path = userFolder + uuid;
		
		byte[] msg_bytes = AndroidMessage.androidMessageToBytes(msg);
		try (FileOutputStream fos = new FileOutputStream(full_path)) 
		{
			   fos.write(msg_bytes);
			   //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
		}
		catch(Exception e)
		{
			
		}
		
	}

}
