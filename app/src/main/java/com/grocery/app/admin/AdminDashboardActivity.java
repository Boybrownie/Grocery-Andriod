package com.grocery.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grocery.app.R;
import com.grocery.app.admin.fragment.AdminAddProductFragment;
import com.grocery.app.admin.fragment.AdminCategoryFragment;
import com.grocery.app.admin.fragment.AdminMenuFragment;
import com.grocery.app.admin.fragment.AdminViewOrderFragment;

import java.util.Objects;

public class AdminDashboardActivity extends AppCompatActivity implements
        AdminMenuFragment.OnFragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Load the default fragment
        if (savedInstanceState == null) {
            replaceFragment(new AdminMenuFragment());
        }
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commitNow();

        if (fragmentManager.findFragmentById(R.id.fragment_container) instanceof AdminMenuFragment) {
            setTitle("Admin Dashboard");
        } else if (fragmentManager.findFragmentById(R.id.fragment_container) instanceof AdminAddProductFragment) {
            setTitle("Add new product");
        } else if (fragmentManager.findFragmentById(R.id.fragment_container) instanceof AdminViewOrderFragment) {
            setTitle("Orders");
        } else if (fragmentManager.findFragmentById(R.id.fragment_container) instanceof AdminCategoryFragment) {
            setTitle("Add new Category");
        }

        boolean show = fragmentManager
                .findFragmentById(R.id.fragment_container) instanceof AdminMenuFragment;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment_container) instanceof AdminMenuFragment) {
            super.onBackPressed();
        } else {
            replaceFragment(new AdminMenuFragment());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed(); // Handle the up button press
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }
}
