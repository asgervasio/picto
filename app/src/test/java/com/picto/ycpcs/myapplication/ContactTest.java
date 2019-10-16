package com.picto.ycpcs.myapplication;

import com.picto.ycpcs.myapplication.Contact;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class ContactTest {
    private Contact  contact1, contact2;
    private String name1, name2, email1,email2;

    @Before
    public void setUp() {
        name1 = "Aaron Gervasio";
        name2 = "Nathan Hays";
        email1 = "agervasio@ycp.edu";
        email2 = "nhays@ycp.edu";

        contact1 = new Contact(name1, email1);
        contact2 = new Contact(name2, email2);
    }

    @Test
    public void testSetAndGetName() {
        contact1.setName(name1);
        contact2.setName(name2);

        assertTrue(contact1.getName().equals(name1));
        assertTrue(contact2.getName().equals(name2));
    }

    @Test
    public void testSetAndGetEmail() {
        contact1.setEmail(email1);
        contact2.setEmail(email2);

        assertTrue(contact1.getEmail().equals(email1));
        assertTrue(contact2.getEmail().equals(email2));
    }
}
