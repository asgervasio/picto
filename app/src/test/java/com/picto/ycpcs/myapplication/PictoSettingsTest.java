package com.picto.ycpcs.myapplication;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PictoSettingsTest {

    @Test
    public void testSettings(){
        PictoSettings actualSettings = new PictoSettings("ipAddress","username");
        byte[] byteSettings = PictoSettings.pictoSettingsToBytes(actualSettings);
        PictoSettings returnSettings = PictoSettings.bytesToPictoSettings(byteSettings);
        assertEquals(actualSettings.ipAddress(),returnSettings.ipAddress());
        assertEquals(actualSettings.username(),returnSettings.username());
    }
}
