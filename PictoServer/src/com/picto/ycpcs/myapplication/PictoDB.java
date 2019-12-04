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
	   
	   static final String DB_URL = "jdbc:mysql://pictodb.cduhxy1ichau.us-east-2.rds.amazonaws.com:3306";
	   static final String PICTO_DB_URL = "jdbc:mysql://pictodb.cduhxy1ichau.us-east-2.rds.amazonaws.com:3306/picto_db_schema";

	   //  Database credentials
	   static final String USER = "admin"; //"username";
	   static final String PASS = "password";
	   Connection conn = null;
	   Statement stmt = null;
	   String pictoServerFolderName = "pictoserver";
	   String pictoDBFolderName = "pictodb";
	   String currentUsersHomeDir = "";
	   String serverFolder = "";
	   String serverDBFolder = "";
	   int db_user_id = 0;
	
	   
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
		openPictoDB();
		/*
		int user_id = isValidUserAccount_pictodb("bob");
		user_id = isValidUserAccount_pictodb("ted");
		user_id = isValidUserAccount_pictodb("patrick");
		user_id = isValidUserAccount_pictodb("bobb");
		user_id = isValidUserAccount_pictodb("dave");
		
		createUserAccount_pictodb("bart", "password_bart");
		
		PictoMessage message = new PictoMessage("Ed","bob","message for you","5");

		try
		{
		storeUserMessage_pictodb(message);
		}
		catch(Exception e)
		{
			
		}
		*/
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
	
	// open Mysql DB and test the connection
		private synchronized void openPictoDB()
		{
			closeDB(); // close the DB in case it is already opened
			
			conn = null;
			stmt = null;
			   try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");

			      //STEP 3: Open a connection
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(PICTO_DB_URL,USER,PASS);

			      //STEP 4: Execute a query
			      System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      String sql;
			      sql = "SELECT * FROM picto_db_schema.message_table";
			      //sql = "SELECT User, Host, plugin FROM mysql.user";
			      
			      ResultSet rs = stmt.executeQuery(sql);
			      
			      //STEP 5: Extract data from result set
			      while(rs.next()){
			    	 
			    	  int message_id = rs.getInt("message_id");
			    	  int user_id = rs.getInt("user_id");
			    	 String caption = rs.getString("caption");
			    	 String from_address = rs.getString("from_address");
			    	 String to_address = rs.getString("to_address");
			    	 String content_settings = rs.getString("content_settings");
			    	 String content_filename = rs.getString("content_filename");
			    	 
			    	//Display values
			    	 System.out.print("message_id: " + Integer.toString(message_id));
			    	 System.out.print(", user_id: " + Integer.toString(user_id));
			         System.out.print(", caption: " + caption);
			         System.out.print(", from_address: " + from_address);
			         System.out.println(", to_address: " + to_address);
			         System.out.println(", content_settings: " + content_settings);
			         System.out.println(", content_filename: " + content_filename);

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
	
	public synchronized int getUserID()
	{			
		return(db_user_id);
	}
	
	public synchronized int isValidUserAccount(String user, String password)
	{	
		//return(isValidUserAccount_file(user));
		return(isValidUserAccount_pictodb(user,password));
	}
	
	public synchronized int isValidUserAccount_file(String user)
	{	
		
		/*
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		return(folder.exists());
		*/	
		int rv = 0;
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		if(folder.exists() == true)
		{
			rv = 1;
		}
		
		return(rv);
	}
	
	public synchronized int isValidUserAccount_pictodb(String user, String password)
	{				
		boolean valid_user = false;
		int user_id = 0;
		
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		if(folder.exists() == false)
		{
			return 0;
		}
		
		try{
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT * FROM picto_db_schema.user_table where name = '" + user + "'";
		      //sql = "SELECT User, Host, plugin FROM mysql.user";
		      
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next())
		      {
		    	 valid_user = true;
		    	  
		    	 db_user_id = user_id = rs.getInt("user_id");
		    	 String name = rs.getString("name");
		    	 String db_password = rs.getString("password");
		    	 if(password.equals(db_password) == true)
		    	 {
		    		 valid_user = true;
		    	 }
		    	 else
		    	 {
		    		 return(-1);
		    	 }
		    	 
		    	 
		    	//Display values
		    	 
		    	 System.out.print(", user_id: " + Integer.toString(user_id));
		         System.out.print(", name: " + name);
		         System.out.print(", password: " + password + ", db_password: " + db_password);


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
		
		
		
		
		return(user_id);		
	}
	
	public synchronized boolean createUserAccount(String user, String password)
	{		
		createUserAccount_file(user, password);  
		return ( createUserAccount_pictodb(user, password));
	}
	
	public synchronized boolean createUserAccount_file(String user, String password)
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
	
	public synchronized boolean createUserAccount_pictodb(String user, String password)
	{
		boolean rv = false;
			
		// INSERT INTO picto_db_schema.user_table (name, password)
		//VALUES ('doug','password_doug')
		
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
		
		
		try
		{

		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO picto_db_schema.user_table (name, password) " +
		                "VALUES ('" + user.toLowerCase() + "','" + password + "')";
		      
		      stmt.executeUpdate(sql);
		      
		      rv = true;
		      
		      if(stmt!=null)
		      {
		         stmt.close();
		         stmt = null;
		      }
		     
		}
		catch(Exception e)
		{
		      //Handle errors for Class.forName
			  e.printStackTrace();
			  rv = false;
		}
		finally
		{
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
				
		
		return rv;
	}
	
	

	// after message is copied from user inbox it is removed from the server
	// the server doesn't keep copies of user messages
	
	public synchronized List<PictoMessage> getUserMessages(String user)
	{
		//return(getUserMessages_file(user));
		return(getUserMessages_pictodb(user));
		
	}
	
	// after message is copied from user inbox it is removed from the server
	// the server doesn't keep copies of user messages
	
	public synchronized List<PictoMessage> getUserMessages_file(String user)
	{
		List<PictoMessage> listOfMessages = new ArrayList<PictoMessage>();
		PictoMessage msg;
		// for now go to disk to get any messages for user message and image
		//
		
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		if(folder.exists() == false)
		{		
			//createUserAccount(user,"");
			
			// protocol error, cannot store a message for a non existing user
			// this could only occur if we receive a receive command before a valid login
			return listOfMessages;
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
	
	public synchronized List<PictoMessage> getUserMessages_pictodb(String user)
	{
		List<PictoMessage> listOfMessages = new ArrayList<PictoMessage>();
		PictoMessage msg;
		int message_id;
		// for now go to disk to get any messages for user message and image
		//
		List<Integer> message_id_list = new ArrayList<>();		
		
		String userFolder = serverDBFolder + user.toLowerCase()  + "/";
		File folder = new File(userFolder);
		
		if(folder.exists() == false)
		{		
			//createUserAccount(user, "");
			// protocol error, cannot store a message for a non existing user
			// this could only occur if we receive a receive command before a valid login
			return listOfMessages;
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
		int user_id = getUserID(); //isValidUserAccount_pictodb(user.toLowerCase());
		
		if(user_id > 0)
		{
		// get DB message list
		try{
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT * FROM picto_db_schema.message_table where user_id = " + Integer.toString(user_id);		      
		      
		      ResultSet rs = stmt.executeQuery(sql);
		     
		      
		      //STEP 5: Extract data from result set
		      while(rs.next())
		      {
		    	 
		    	 message_id = rs.getInt("message_id");
		    	 user_id = rs.getInt("user_id");
		    	 String caption = rs.getString("caption");
		    	 String from_address = rs.getString("from_address");
		    	 String to_address = rs.getString("to_address");
		    	 String content_settings = rs.getString("content_settings");
		    	 String content_filename = rs.getString("content_filename");
		    	 
		    	//Display values
		    	 System.out.print("message_id: " + Integer.toString(message_id));
		    	 System.out.print(", user_id: " + Integer.toString(user_id));
		         System.out.print(", caption: " + caption);
		         System.out.print(", from_address: " + from_address);
		         System.out.println(", to_address: " + to_address);
		         System.out.println(", content_settings: " + content_settings);
		         System.out.println(", content_filename: " + content_filename);

		         // create picto message and load the properties
		         // read the picture image from file
		         // store it into the picto message content
		         
		         message_id_list.add(message_id);

		      }
		      //close connection
		      rs.close();
		      if(stmt!=null)
		      {
		         stmt.close();
		         stmt = null;
		      }
		     
		   }
		catch(Exception e)
		{
		      //Handle errors 
		}
		finally
		{
		      //finally block used to close resources
			   
		      try
		      {
		         if(stmt!=null)
		         {
		            stmt.close();
		         }
		      }
		      catch(SQLException e)
		      {
		      }// nothing we can do
		     
		   }
		}
		
		// DELETE FROM `picto_db_schema`.`message_table` WHERE `message_id`='101';
		int message_id_list_size = message_id_list.size();
		
		for(int index = 0; index < message_id_list_size; index++)
		{
			message_id =  message_id_list.get(index);
			try
			{
			  stmt = conn.createStatement();
		      String sql;
		      sql = "DELETE FROM picto_db_schema.message_table where message_id = " + Integer.toString(message_id);		      
		      
		      stmt.executeUpdate(sql);
		      
		     
		      
		      if(stmt!=null)
		      {
		         stmt.close();
		         stmt = null;
		      }
			}
			catch(Exception e)
			{
				
			}
			
		}
		return listOfMessages;
	}
	
	
	
	public synchronized void storeUserMessage(PictoMessage msg) throws Exception
	{		
		//storeUserMessage_file(msg);
		storeUserMessage_pictodb(msg);
		
		/*
		String userFolder = serverDBFolder + msg.toAddress().toLowerCase() +  "/";
		
		File theUserpath = new File(userFolder);
		if(theUserpath.exists() == false)
		{
			createUserAccount(msg.toAddress().toLowerCase(), "");
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
		*/
		
	}
	
	public synchronized void storeUserMessage_file(PictoMessage msg) throws Exception
	{		
		String userFolder = serverDBFolder + msg.toAddress().toLowerCase() +  "/";
		
		File theUserpath = new File(userFolder);
		if(theUserpath.exists() == false)
		{
			//createUserAccount(msg.toAddress().toLowerCase(), "");
			
			// protocol error, cannot store a message for a non existing user
			// this could only occur if we receive a send command before a valid login
			return;
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
	
	public synchronized void storeUserMessage_pictodb(PictoMessage msg) throws Exception
	{		
		
		String userFolder = serverDBFolder + msg.toAddress().toLowerCase() +  "/";
		
		File theUserpath = new File(userFolder);
		if(theUserpath.exists() == false)
		{
			//createUserAccount(msg.toAddress().toLowerCase(), "");
			
			// protocol error, cannot store a message for a non existing user
			// this could only occur if we receive a send command before a valid login
			return; 
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
		
		
		// get UUID and remove the '-' chars to create a unique filename	
		//String uuid = UUID.randomUUID().toString().replace("-", "");
		
		String username = msg.toAddress().toLowerCase();
		
		int user_id = getUserID(); //isValidUserAccount_pictodb(username);
		
		try{
			// INSERT INTO picto_db_schema.message_table (user_id, caption,from_address,to_address,content_settings,content_filename)
			// VALUES (101,'tree','bob','dave','5','test.txt')

		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "INSERT INTO picto_db_schema.message_table (user_id, caption,from_address,to_address,content_settings,content_filename) " +
		                "VALUES (" + Integer.toString(user_id) + ",'" + msg.textMessage() + "','" + msg.fromAddress() + "','" + msg.toAddress() + "','" + msg.contentSettings() + "','" + uuid +  "')";
		      
		      stmt.executeUpdate(sql);
		      
		      
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

}
