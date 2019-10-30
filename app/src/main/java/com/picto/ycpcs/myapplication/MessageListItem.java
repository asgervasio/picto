package com.picto.ycpcs.myapplication;

import java.io.Serializable;


public class MessageListItem implements Serializable{
    private String name = null;
    private String fileName = null;
    private byte[] content;

    public MessageListItem(String name,String filename)
    {
        name( name);
        filename(filename);

    }

    // get the info we want to display for our view
    public String name()
    {
        return this.name;
    }

    // get the info we want to display for our view
    public void name(String name)
    {
        this.name = name;
    }

    // get the info we want to display for our view
    public String filename()
    {
        return this.fileName;
    }

    // get the info we want to display for our view
    public void filename(String filename)
    {
        this.fileName = filename;

    }

    // get the content we want to display for our view
    public byte[] content()
    {
        return this.content;
    }

    // get the content we want to display for our view
    public void content(byte[] content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        if(name() == null)
        {
            return ("NULL");
        }
        else
        {
            return (name());
        }

    }
}
