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
        byte[] byteMsg = PictoMessage.pictoMessageToBytes(message);
        PictoMessage returnMessage = PictoMessage.bytesToPictoMessage(byteMsg);
        assertEquals(message.fromAddress(), returnMessage.fromAddress());
        assertEquals(message.toAddress(), returnMessage.toAddress());
        assertEquals(message.textMessage(), returnMessage.textMessage());
        assertEquals(message.contentFileName(), returnMessage.contentFileName());
    }

}
