package com.picto.ycpcs.myapplication;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CommandHeaderTest {

    @Test
    public void importExportTest(){
        CommandHeader actualCommand = new CommandHeader(1,2,3,4,5);
        byte[] exportedCommand = actualCommand.exportHeader();
        CommandHeader returnCommand = new CommandHeader(0,0,0,0,0);
        returnCommand.importHeader(exportedCommand);
        assertEquals(actualCommand.signature(),returnCommand.signature());
        assertEquals(actualCommand.commandType(),returnCommand.commandType());
        assertEquals(actualCommand.payloadSize(),returnCommand.payloadSize());
    }
}
