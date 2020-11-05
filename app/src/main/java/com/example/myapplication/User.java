package com.example.myapplication;

public class User {

    public String fullname, email, petname, petbreed, petage;

    public User()
    {

    }

    public User(String fullname, String email, String petname, String petbreed, String petage)
    {
        this.fullname = fullname;
        this.email = email;
        this.petname = petname;
        this.petbreed = petbreed;
        this.petage = petage;
    }
}
