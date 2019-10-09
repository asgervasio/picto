package com.example.dev.simplescan;

public class AndroidClientMain 
{ 
	static ApplicationState appstate;
	   
	   public static void main(String[] args) 
	   {
		   
		   AndroidClient client = null; 
		   int msgCount = 1;
		   
		   appstate = new ApplicationState();
/*		   
		   // create IO_Thread
		   IO_Thread ioThread = new IO_Thread();
		   ioThread.connect("127.0.0.1");
		   System.out.println("started Client thread ");
		   ioThread.start();
*/		   
		   // new thread for a client
		   client = new AndroidClient();
		   client.connect("127.0.0.1");
		   client.login("bob", "password");
		   
		   //client.testAll();
		   
		   while(true)
		   {
			   try 
			   {
				   Thread.sleep(10000);
				   AndroidMessage message = new AndroidMessage("Ed", "bob","message" + String.valueOf(msgCount) + " for bob","message" + String.valueOf(msgCount) + ".txt");		
					
				   byte[] image1 = new byte[10]; // 100000
					
				   for(int x = 0; x<10; x++) // 100000
				   {
						image1[x] = 'A';
				   }
					
				   message.image(image1);
				   msgCount++;
				   
				   if(client.sendMessageToServer(message) == true)
				   {
					   System.out.println("Android Client Main. sendMessageToServer() true ");
				   }
				   else
				   {
					   System.out.println("Android Client Main. sendMessageToServer() false ");
				   }
				   message = client.readMessageFromServer();
				   while(message != null)
				   {
					   System.out.println("Android Client Main. readMessageFromServer() image size " + message.image().length);
					   System.out.println("Android Client Main. readMessageFromServer() From: " + message.fromAddress() + " To: " + message.toAddress() + " Message text:  "+ message.textMessage() + " image filename: " + message.imageFileName());
					   message = client.readMessageFromServer();
				   }
				   
				   //client.testAll();
			   }
			   catch (Exception e)
			   {
				   
			   }
			   if(msgCount >= 20)
			   {
				   break;
			   }
		   }
		   
		   System.out.println("Android Client Main. disconnect " );
		   
		   client.disconnect();
		   
		   try 
		   {
			   Thread.sleep(1000);
		   }
		   catch (Exception e)
		   {
			   
		   }
		   

	}//end main		   
} 
