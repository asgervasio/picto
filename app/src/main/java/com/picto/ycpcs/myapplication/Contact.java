package com.picto.ycpcs.myapplication;

public class Contact {
    private String name, email;

    public Contact (String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }
}
