package com.picto.ycpcs.myapplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class PictoMessage implements Serializable{
	/**
	 * 
	 */
	
	//private static final long serialVersionUID = 1L;
	private static final long serialVersionUID = -5099372983329922303L;
	private String fromAddress;
	private String toAddress;
    private String textMessage = null;
    private String contentSettings = null;
    //private Date creationDate = null;
    private byte[] content;

    public PictoMessage(String fromAddress,String toAddress,String textMessage,String contentSettings)
    {
    	fromAddress(fromAddress);
    	toAddress(toAddress);
    	textMessage( textMessage);
        contentSettings(contentSettings);

    }
    
 // get the message from: address 
    public synchronized String fromAddress()
    {
        return this.fromAddress;
    }

    // set the message from: address
    public synchronized void fromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }
    
    // get the message to: address 
    public synchronized String toAddress()
    {
        return this.toAddress;
    }

    // set the message to: address
    public synchronized void toAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }

    // get the users message text 
    public synchronized String textMessage()
    {
        return this.textMessage;
    }

    // set the users message text
    public synchronized void textMessage(String textMessage)
    {
        this.textMessage = textMessage;
    }

 // get the settings of the content
    public synchronized String contentSettings()
    {
        return this.contentSettings;
    }

    // set the settings of the content
    public synchronized void contentSettings(String contentSettings)
    {
        this.contentSettings = contentSettings;

    }

    // get the byte array containing the content 
    public synchronized byte[] content()
    {
        return this.content;
    }

    // set the byte array containing the content
    public synchronized void content(byte[] content)
    {
        this.content = content;
    }

    @Override
    public synchronized String toString()
    {
        if(textMessage() == null)
        {
            return ("NULL");
        }
        else
        {
            return (textMessage());
        }

    }
    
    // convert the message to a byte array
	   public static synchronized byte[] pictoMessageToBytesSerial(PictoMessage item) throws Exception
	    {
	        byte[] resultBytes = null;
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutput out = null;
	        try {
	            out = new ObjectOutputStream(bos);
	            out.writeObject(item);
	            out.flush();
	            resultBytes = bos.toByteArray();


	        }
	        catch(Exception e)
	        {

	        }
	        finally
	        {
	            try
	            {
	                bos.close();
	            } 
	            catch (IOException ex)
	            {
	                // ignore close exception
	            	throw ex;

	            }
	        }
	        return(resultBytes);
	    }

	// convert the byte array to a message
	   public static synchronized PictoMessage bytesToPictoMessageSerial(byte[] itemBytes) throws Exception
	    {
		   String exception_string = "";
		   PictoMessage item = null;
	        ByteArrayInputStream bis = new ByteArrayInputStream(itemBytes);
	        ObjectInput in = null;
	        try {
	            in = new ObjectInputStream(bis);            
	            item = (PictoMessage)in.readObject();

	        }
	        catch(Exception e)
	        {
	        	exception_string = e.toString();
	        	System.out.println("bytesToPictoMessage exception: " + exception_string);
	        }
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception ex) {
	                // ignore close exception
	            	throw ex;
	            }
	        }
	        return item;
	    }
	   
	   // convert the message to a byte array non-serialized
	   //private String fromAddress;
		//private String toAddress;
	    //private String textMessage = null;
	    //private String contentFileName = null;
	    //private Date creationDate = null;
	    //private byte[] content;
	   //public static synchronized byte[] pictoMessageToBytes(PictoMessage item) throws Exception
	   public static synchronized byte[] pictoMessageToBytes(PictoMessage item) throws Exception
	    {
		   int fromAddressLen = item.fromAddress().length();
		   int toAddressLen = item.toAddress().length();
		   int textMessageLen = item.textMessage().length();
		   int contentSettingsLen = item.contentSettings().length();
		   int contentLen;
		   if(item.content() == null)
		   {
			   contentLen = 0;
		   }
		   else
		   {
			   contentLen = item.content().length;
		   }
		   
		   int exportBufferLen = 4 + // length of entire buffer
				   4 + fromAddressLen +  // 4 byte length of from address followed by from address
				   4 + toAddressLen + // 4 byte length of to: address followed by to: address
				   4 + textMessageLen + // 4 byte length of text message followed by text message
				   4 + contentSettingsLen + // 4 byte length of content filename followed by to content filename
				   4 + contentLen;
		   int export_buffer_len_index = 0;
		   int fromAddressLenIndex = 4;
		   int toAddressLenIndex = 4 + fromAddressLenIndex + fromAddressLen;
		   int textMessageLenIndex = 4 + toAddressLenIndex + toAddressLen;
		   int contentSettingsLenIndex = 4 + textMessageLenIndex + textMessageLen;
		   int contentLenIndex = 4 + contentSettingsLenIndex + contentSettingsLen;
				   
		   byte[] export = new byte[exportBufferLen];
		   byte[] export_buffer_len_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(exportBufferLen).array();
	    	byte[] from_address_len_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(fromAddressLen).array();
	    	byte[] to_address_len_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(toAddressLen).array();
	    	byte[] txt_msg_len_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(textMessageLen).array();
	    	byte[] content_settings_len_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(contentSettingsLen).array();
	    	byte[] content_len_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(contentLen).array();
	    	
	    	byte[] fromAddressBytes = item.fromAddress().getBytes();
	    	byte[] toAddressBytes = item.toAddress().getBytes();
	    	byte[] textMessageBytes = item.textMessage().getBytes();
	    	byte[] contentSettingsBytes = item.contentSettings().getBytes();
	    	
	    	System.arraycopy(export_buffer_len_bytes, 0, export, export_buffer_len_index, 4);
	    	// insert from: address field length bytes
	    	System.arraycopy(from_address_len_bytes, 0, export, fromAddressLenIndex, 4);
	    	// insert from: address field bytes
	    	System.arraycopy(fromAddressBytes, 0, export, fromAddressLenIndex + 4, fromAddressLen);
	    	// insert to: address field length bytes
	    	System.arraycopy(to_address_len_bytes, 0, export, toAddressLenIndex, 4);
	    	// insert to: address field bytes
	    	System.arraycopy(toAddressBytes, 0, export, toAddressLenIndex + 4, toAddressLen);
	    	
	    	System.arraycopy(txt_msg_len_bytes, 0, export, textMessageLenIndex, 4);
	    	// insert text message field bytes
	    	System.arraycopy(textMessageBytes, 0, export, textMessageLenIndex + 4, textMessageLen);
	    	
	    	System.arraycopy(content_settings_len_bytes, 0, export, contentSettingsLenIndex, 4);
	    	// insert content filename field bytes
	    	System.arraycopy(contentSettingsBytes, 0, export, contentSettingsLenIndex + 4, contentSettingsLen);
	    	System.arraycopy(content_len_bytes, 0, export, contentLenIndex, 4);
	    	// insert content field bytes
	    	if(contentLen > 0) 
	    	{
	    	System.arraycopy(item.content(), 0, export, contentLenIndex + 4, contentLen);
	    	}
	    	
	        return export;
	    }	
	   //public static synchronized PictoMessage bytesToPictoMessage(byte[] itemBytes) throws Exception
	   public static synchronized PictoMessage bytesToPictoMessage(byte[] itemBytes) throws Exception
	    {
		   PictoMessage msg = null;
		   
		   // allocate space for the field length
	    	byte[] export_buffer_len_bytes = new byte[4];
	    	byte[] from_address_len_bytes = new byte[4];
	    	byte[] to_address_len_bytes = new byte[4];
	    	byte[] txt_msg_len_bytes = new byte[4];
	    	byte[] content_settings_len_bytes = new byte[4];
	    	byte[] content_len_bytes = new byte[4];
	 	  
	    	
	    	// get the lengths and convert, use converted values to calculate other lengths
	    	System.arraycopy(itemBytes, 0, export_buffer_len_bytes, 0, 4);
	    	System.arraycopy(itemBytes, 4, from_address_len_bytes, 0, 4);
	    	
	    	int exportBufferLen = ByteBuffer.wrap(export_buffer_len_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
	    	int fromAddressLen = ByteBuffer.wrap(from_address_len_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
	    	
	    	
	    	//int export_buffer_len_index = 0;
			int fromAddressLenIndex = 4;
			
			// get to address length index and length of the to address field
			int toAddressLenIndex = 4 + fromAddressLenIndex + fromAddressLen;		
			System.arraycopy(itemBytes, toAddressLenIndex, to_address_len_bytes, 0, 4);	
			int toAddressLen = ByteBuffer.wrap(to_address_len_bytes).order(ByteOrder.BIG_ENDIAN).getInt();			
			
			// get text message length index and length of the to text message field
			int textMessageLenIndex = 4 + toAddressLenIndex + toAddressLen;			
			System.arraycopy(itemBytes, textMessageLenIndex, txt_msg_len_bytes, 0, 4);
			int textMessageLen = ByteBuffer.wrap(txt_msg_len_bytes).order(ByteOrder.BIG_ENDIAN).getInt();			
			
			// get content settings length index
			int contentSettingsLenIndex = 4 + textMessageLenIndex + textMessageLen;
			System.arraycopy(itemBytes, contentSettingsLenIndex, content_settings_len_bytes, 0, 4);			
			int contentSettingsLen = ByteBuffer.wrap(content_settings_len_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
			
			// get content length
			int contentLenIndex = 4 + contentSettingsLenIndex + contentSettingsLen;
			System.arraycopy(itemBytes, contentLenIndex, content_len_bytes, 0, 4);
			int contentLen = ByteBuffer.wrap(content_len_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
	    	
			
	    	
			// allocate space for from address
			// copy bytes from input buffer			
			byte[] from_address_bytes = new byte[fromAddressLen];
			System.arraycopy(itemBytes, 8, from_address_bytes, 0, fromAddressLen);
			String fromAddress = new String(from_address_bytes);
			
			byte[] to_address_bytes = new byte[toAddressLen];
			System.arraycopy(itemBytes, toAddressLenIndex + 4, to_address_bytes, 0, toAddressLen);
			String toAddress = new String(to_address_bytes);
			
			
			byte[] txt_msg_bytes = new byte[textMessageLen];
			System.arraycopy(itemBytes, textMessageLenIndex + 4, txt_msg_bytes, 0, textMessageLen);
			String textMessage = new String(txt_msg_bytes);
		   
			
			byte[] content_settings_bytes = new byte[contentSettingsLen];
			System.arraycopy(itemBytes, contentSettingsLenIndex + 4, content_settings_bytes, 0, contentSettingsLen);
			String contentSettings = new String(content_settings_bytes);
			
			msg =  new PictoMessage(fromAddress,toAddress,textMessage,contentSettings);	
			
			if(contentLen > 0)
			{
			byte[] content_bytes = new byte[contentLen];
			System.arraycopy(itemBytes, contentLenIndex + 4, content_bytes, 0, contentLen);
			msg.content(content_bytes);
			//String contentFileName = new String(content_filename_bytes);
			}
		   
		    //private Date creationDate = null;
		  					
	    		
	    	return (msg);
	    }	   
  
}
