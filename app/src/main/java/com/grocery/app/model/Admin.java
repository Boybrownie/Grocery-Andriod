package com.grocery.app.model;

public class Admin {
    private String email;
    private String password;
    private String id;


    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getId() {
        return id;
    }

    public Admin() { }

    public Admin(String email, String password, String id) {
        this.email = email;
        this.password = password;
        this.id = id;
    }
}
