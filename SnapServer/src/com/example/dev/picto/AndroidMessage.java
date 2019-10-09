package com.example.dev.picto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;


public class AndroidMessage implements Serializable{
	/**
	 * 
	 */
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private static final long serialVersionUID = -5099372983329922303L;
	private String fromAddress;
	private String toAddress;
    private String textMessage = null;
    private String imageFileName = null;
    //private Date creationDate = null;
    private byte[] image;

    public AndroidMessage(String fromAddress,String toAddress,String textMessage,String imageFileName)
    {
    	//creationDate = new Date();
    	fromAddress(fromAddress);
    	toAddress(toAddress);
    	textMessage( textMessage);
    	imageFileName(imageFileName);

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

    // get the filename of the user image
    public String imageFileName()
    {
        return this.imageFileName;
    }

    // set the filename of the user image
    public void imageFileName(String imageFileName)
    {
        this.imageFileName = imageFileName;

    }

    // get the byte array containing the image 
    public byte[] image()
    {
        return this.image;
    }

    // set the byte array containing the image
    public void image(byte[] image)
    {
        this.image = image;
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
	   static byte[] androidMessageToBytes(AndroidMessage item)
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
	   static AndroidMessage bytesToAndroidMessage(byte[] itemBytes)
	    {
		   String exception_string = "";
		   AndroidMessage item = null;
	        ByteArrayInputStream bis = new ByteArrayInputStream(itemBytes);
	        ObjectInput in = null;
	        try {
	            in = new ObjectInputStream(bis);
	            //Object o = in.readObject();
	            item = (AndroidMessage)in.readObject();

	        }
	        catch(Exception e)
	        {
	        	exception_string = e.toString();
	        	System.out.println("bytesToAndroidMessage exception: " + exception_string);
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
