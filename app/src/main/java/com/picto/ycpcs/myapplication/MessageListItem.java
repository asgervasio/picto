package com.picto.ycpcs.myapplication;

import java.io.Serializable;


public class MessageListItem implements Serializable{
    private String name = null;
    private String fileName = null;
    private String type = "";
    private String contentSettings = "";
    private byte[] content;
    private String fromAddress = "";

    public MessageListItem(String name,String filename)
    {
        name( name);
        filename(filename);
        type("");

    }

    // get the fromAddress
    public  String fromAddress()
    {
        return this.fromAddress;
    }

    // get the fromAddress
    public  void fromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }

    // get the info we want to display for our view
    public  String name()
    {
        return this.name;
    }

    // get the info we want to display for our view
    public  void name(String name)
    {
        this.name = name;
    }

    // type of message, SEND,RECV
    public  String type()
    {
        return this.type;
    }

    // type of message, SEND,RECV
    public  void type(String type)
    {
        this.type = type;
    }

    // get the info we want to display for our view
    public  String filename()
    {
        return this.fileName;
    }

    // get the info we want to display for our view
    public  void filename(String filename)
    {
        this.fileName = filename;

    }

    // contentSettings of message
    public  String contentSettings()
    {
        return this.contentSettings;
    }

    // contentSettings of message
    public  void contentSettings(String contentSettings)
    {
        this.contentSettings = contentSettings;
    }


    // get the content we want to display for our view
    public  byte[] content()
    {
        return this.content;
    }

    // get the content we want to display for our view
    public  void content(byte[] content)
    {
        this.content = content;
    }

    @Override
    public  String toString()
    {
        if(name() == null)
        {
            return ("NULL");
        }
        else
        {
            if((type().equals( ApplicationState.picto_msg_type_Sent) == true) || (type().equals( ApplicationState.picto_msg_type_Received) == true))
            {
                return (type() + " - " + name());
            }
            else
            {
                return ( name());
            }
        }

    }
}
