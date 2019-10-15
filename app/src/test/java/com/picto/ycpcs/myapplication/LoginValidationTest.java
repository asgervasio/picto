package com.picto.ycpcs.myapplication;


import org.junit.Test;

import static org.junit.Assert.*;

public class LoginValidationTest {
    @Test
    public void assertLoginInputValid(){
        assertEquals(true, LoginActivity.isValidInput("username", "password"));
    }
    @Test
    public void assertLoginInputInvalid(){
        assertEquals(false, LoginActivity.isValidInput("", ""));
    }
    @Test
    public void assertIsAdminLogin(){
        assertEquals(true, LoginActivity.isAdminLogin("admin", "admin"));
    }
    @Test
    public void assertIsUserLogin(){
        assertEquals(false, LoginActivity.isAdminLogin("a", "b"));
    }
}
