package com.grocery.app.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.grocery.app.GroceryApplication;
import com.grocery.app.ImageLoader;
import com.grocery.app.R;
import com.grocery.app.model.Customer;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerDashboardActivity extends AppCompatActivity {

    LinearLayout llShop;
    LinearLayout llMyOrders;
    LinearLayout llMembership;
    LinearLayout llEditProfile;
    CircleImageView profileImage;

    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        customer = ((GroceryApplication) getApplicationContext()).customer;

        setTitle("Welcome, " + customer.getName());

        llShop = findViewById(R.id.ll_shop);
        llMyOrders = findViewById(R.id.ll_my_orders);
        llMembership = findViewById(R.id.ll_membership);
        llEditProfile = findViewById(R.id.ll_edit_profile);
        profileImage = findViewById(R.id.profile_image);

        ImageLoader.loadImageFromUrl(customer.getAvatarUrl(), CustomerDashboardActivity.this, profileImage);

        llShop.setOnClickListener((v) -> startActivity(new Intent(CustomerDashboardActivity.this, CustomerShopActivity.class)));

        llMyOrders.setOnClickListener((v) -> startActivity(new Intent(
                CustomerDashboardActivity.this,
                CustomerMyOrdersActivity.class)));

        llMembership.setOnClickListener((v) ->
                startActivity(new Intent(CustomerDashboardActivity.this,
                        CustomerMembershipActivity.class)));

        llEditProfile.setOnClickListener((v) -> startActivity(new Intent(CustomerDashboardActivity.this,
                CustomerEditProfileActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed(); // Handle the up button press
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(CustomerDashboardActivity.this, CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_cart) {
            Intent intent = new Intent(CustomerDashboardActivity.this, CustomerCartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }
}
