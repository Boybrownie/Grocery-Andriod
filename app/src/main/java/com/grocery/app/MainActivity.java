package com.grocery.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.grocery.app.admin.AdminLoginActivity;
import com.grocery.app.customer.CustomerLoginActivity;

public class MainActivity extends AppCompatActivity {

    CardView btnAdmin;
    CardView btnCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdmin = findViewById(R.id.btnAdmin);
        btnCustomer = findViewById(R.id.btnCustomer);

        btnAdmin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                AdminLoginActivity.class)));

        btnCustomer.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                CustomerLoginActivity.class)));

    }
}