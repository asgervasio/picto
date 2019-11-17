package com.picto.ycpcs.myapplication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Aaron on 10/15/2019.
 */

public class PictoMessageUnitTest {
    @Test
    public void test_byteConversion(){

        PictoMessage message = new PictoMessage("from", "to", "test", "test");
        byte[] byteMsg;
        PictoMessage returnMessage = new PictoMessage("fake","fake","fake","fake");

        try {
            byteMsg = PictoMessage.pictoMessageToBytes(message);
            returnMessage = PictoMessage.bytesToPictoMessage(byteMsg);
        }
        catch(Exception e){

        }
        assertEquals(message.fromAddress(), returnMessage.fromAddress());
        assertEquals(message.toAddress(), returnMessage.toAddress());
        assertEquals(message.textMessage(), returnMessage.textMessage());
        assertEquals(message.contentSettings(), returnMessage.contentSettings());
    }

}