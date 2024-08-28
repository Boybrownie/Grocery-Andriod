package com.grocery.app;

import android.app.Application;

import com.grocery.app.model.Admin;
import com.grocery.app.model.Customer;

public class GroceryApplication extends Application {

    public Admin admin;
    public Customer customer;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
