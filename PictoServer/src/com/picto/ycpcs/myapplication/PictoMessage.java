package com.picto.ycpcs.myapplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class PictoMessage implements Serializable{
	/**
	 * 
	 */
	
	//private static final long serialVersionUID = 1L;
	private static final long serialVersionUID = -5099372983329922303L;
	private String fromAddress;
	private String toAddress;
    private String textMessage = null;
    private String contentFileName = null;
    //private Date creationDate = null;
    private byte[] content;

    public PictoMessage(String fromAddress,String toAddress,String textMessage,String contentFileName)
    {
    	//creationDate = new Date();
    	fromAddress(fromAddress);
    	toAddress(toAddress);
    	textMessage( textMessage);
    	contentFileName(contentFileName);

    }
    
 // get the message from: address 
    public String fromAddress()
    {
        return this.fromAddress;
    }

    // set the message from: address
    public void fromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }
    
    // get the message to: address 
    public String toAddress()
    {
        return this.toAddress;
    }

    // set the message to: address
    public void toAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }

    // get the users message text 
    public String textMessage()
    {
        return this.textMessage;
    }

    // set the users message text
    public void textMessage(String textMessage)
    {
        this.textMessage = textMessage;
    }

    // get the filename of the content
    public String contentFileName()
    {
        return this.contentFileName;
    }

    // set the filename of the content
    public void contentFileName(String contentFileName)
    {
        this.contentFileName = contentFileName;

    }

    // get the byte array containing the content 
    public byte[] content()
    {
        return this.content;
    }

    // set the byte array containing the content
    public void content(byte[] content)
    {
        this.content = content;
    }

    @Override
    public String toString()
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
	   static byte[] pictoMessageToBytes(PictoMessage item)
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
	            } catch (IOException ex)
	            {
	                // ignore close exception

	            }
	        }
	        return(resultBytes);
	    }

	// convert the byte array to a message
	   static PictoMessage bytesToPictoMessage(byte[] itemBytes)
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
	            } catch (IOException ex) {
	                // ignore close exception
	            }
	        }
	        return item;
	    }
  
}
