package com.milfhey.enterprisemanager;

public class User {
    public String email;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String email) {
        this.email = email;
    }
}
