package com.picto.ycpcs.myapplication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CommandHeader {
	private int signature = 0; // unique signature to identify the header
    private int commandType = 0; // command type
    private int version = 0; // version of the header
    private int status = 0; // response status
    private int payloadSize = 0; // size of the next message payload
    public static final int HEADER_SIZE = 20;
    public static final int VERSION = 0x0000100;
    public static final int SIGNATURE = 0x01491625;
    public static final int CMD_SEND_MSG = 1;
    public static final int CMD_READ_MSG = 2;
    public static final int CMD_PING_MSG = 3;
    public static final int CMD_LOGIN_MSG = 4;
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR_USER_NOT_FOUND = 1;
    public static final int STATUS_ERROR_UNAUTHORIZED = 2;
    public static final int STATUS_ERROR_LOGIN_USERNAME_USED = 3;


    public CommandHeader(int signature,int commandType,int version,int status, int payloadSize)
    {
    	this.signature = signature;
    	this.commandType = commandType;
    	this.version = version;
    	this.status = status;
    	this.payloadSize = payloadSize;

    }

    // get the signature
    public synchronized int signature()
    {
        return this.signature;
    }

    // set the signature
    public synchronized void signature(int signature)
    {
        this.signature = signature;
    }
    
    // get the commandType
    public synchronized int commandType()
    {
        return this.commandType;
    }

    // set the commandType
    public synchronized void commandType(int commandType)
    {
        this.commandType = commandType;
    }
  
    // get the commandStatus
    public synchronized int commandStatus()
    {
        return this.status;
    }

    // set the commandStatus
    public synchronized void commandStatus(int status)
    {
        this.status = status;
    }
    
    // get the payloadSize
    public synchronized int payloadSize()
    {
        return this.payloadSize;
    }

    // set the commandType
    public synchronized void payloadSize(int payloadSize)
    {
        this.payloadSize = payloadSize;
    }
    
    // export to byte array
    public synchronized byte[] exportHeader()
    {
    	byte[] export = new byte[HEADER_SIZE];
    	byte[] signature_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(this.signature).array();
    	byte[] commandType_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(this.commandType).array();
    	byte[] version_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(this.version).array();
    	byte[] status_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(this.status).array();
    	byte[] payloadSize_bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(this.payloadSize).array();
    	
    	System.arraycopy(signature_bytes, 0, export, 0, 4);
    	System.arraycopy(commandType_bytes, 0, export, 4, 4);
    	System.arraycopy(version_bytes, 0, export, 8, 4);
    	System.arraycopy(status_bytes, 0, export, 12, 4);
    	System.arraycopy(payloadSize_bytes, 0, export, 16, 4);
    	
        return export;
    }
    public synchronized void importHeader(byte[] header)
    {
    	byte[] signature_bytes = new byte[4];
    	byte[] commandType_bytes = new byte[4];
    	byte[] version_bytes = new byte[4];
    	byte[] status_bytes = new byte[4];
    	byte[] payloadSize_bytes = new byte[4];
    	
    	System.arraycopy(header, 0, signature_bytes, 0, 4);
    	System.arraycopy(header, 4, commandType_bytes, 0, 4);
    	System.arraycopy(header, 8, version_bytes, 0, 4);
    	System.arraycopy(header, 12, status_bytes, 0, 4);
    	System.arraycopy(header, 16, payloadSize_bytes, 0, 4);
    	
    	this.signature = ByteBuffer.wrap(signature_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    	this.commandType = ByteBuffer.wrap(commandType_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    	this.version = ByteBuffer.wrap(version_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    	this.status = ByteBuffer.wrap(status_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    	this.payloadSize = ByteBuffer.wrap(payloadSize_bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    	
    }

   
}
