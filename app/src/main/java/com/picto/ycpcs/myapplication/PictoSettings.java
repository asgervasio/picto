package com.picto.ycpcs.myapplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PictoSettings implements Serializable {

    private String ipAddress;
    private String username;

    public PictoSettings(String ipAddress,String username)
    {
        ipAddress(ipAddress);
        username(username);


    }

    // get the ipAddress
    public String ipAddress()
    {
        return this.ipAddress;
    }

    // set the ipAddress
    public void ipAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    // get the username
    public String username()
    {
        return this.username;
    }

    // set the username
    public void username(String username)
    {
        this.username = username;
    }

    // convert the PictoSettings to a byte array
    static byte[] pictoSettingsToBytes(PictoSettings item)
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

    // convert the byte array to a PictoSettings
    static PictoSettings bytesToPictoSettings(byte[] itemBytes)
    {
        PictoSettings item = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(itemBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            //Object o = in.readObject();
            item = (PictoSettings)in.readObject();

        }
        catch(Exception e)
        {

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
