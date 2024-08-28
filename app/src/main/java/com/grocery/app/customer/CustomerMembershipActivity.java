package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grocery.app.GroceryApplication;
import com.grocery.app.PaymentActivity;
import com.grocery.app.R;
import com.grocery.app.model.Customer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerMembershipActivity extends AppCompatActivity {

    private static final int REQUEST_PAYMENT = 1;

    Customer customer;
    DatabaseReference dbRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_membership);

        TextView tvMembershipExpiry = findViewById(R.id.tvMembershipExpiry);
        Button btnBuyMembership = findViewById(R.id.btnBuyMembership);
        Button btnCancelMembership = findViewById(R.id.btnCancelMembership);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        customer = ((GroceryApplication) getApplicationContext()).customer;

        dbRef = FirebaseDatabase.getInstance().getReference();

        if (customer.isMember()) {
            // Membership is active, show the expiry date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String expiryDate = sdf.format(new Date(customer.getMembershipExpiry()));
            tvMembershipExpiry.setText("Membership Expires On: " + expiryDate);
            btnBuyMembership.setVisibility(View.GONE);
        } else {
            // Membership has expired, hide expiry text and show Buy Membership button
            tvMembershipExpiry.setVisibility(View.GONE);
            btnBuyMembership.setVisibility(View.VISIBLE);
            btnCancelMembership.setVisibility(View.GONE);
        }

        btnBuyMembership.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerMembershipActivity.this, PaymentActivity.class);
            startActivityForResult(intent, REQUEST_PAYMENT);
        });

        btnCancelMembership.setOnClickListener(v -> cancelMemberShip());
    }

    private void cancelMemberShip() {
        progressDialog.setTitle("Canceling...");
        progressDialog.setMessage("Please wait while we cancel your membership");
        progressDialog.show();

        customer.setMembershipExpiry(System.currentTimeMillis());

        dbRef.child("customers").child(customer.getId()).setValue(customer)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(CustomerMembershipActivity.this, "Membership canceled successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CustomerMembershipActivity.this, "Error canceling membership..!",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerMembershipActivity.this, "Error, try again later..!",
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT) {
            if (resultCode == RESULT_OK && data != null &&
                    data.getBooleanExtra("payment_status", false)) {
                onSuccess();
            } else {
                Toast.makeText(this, "Payment was not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onSuccess() {
        progressDialog.setTitle("Processing");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait while we process..!");
        progressDialog.show();

        long futureTimeInMillis = System.currentTimeMillis() + 2629746000L;

        dbRef.child("customers").child(customer.getId()).child("membershipExpiry").setValue(futureTimeInMillis)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        goBackToLogin();
                    } else {
                        Toast.makeText(CustomerMembershipActivity.this, "Failed to buy membership", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goBackToLogin() {
        Toast.makeText(CustomerMembershipActivity.this, "Great you become member, Login to enjoy membership benefits!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CustomerMembershipActivity.this, CustomerLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed(); // Handle the up button press
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(CustomerMembershipActivity.this, CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_cart) {
            Intent intent = new Intent(CustomerMembershipActivity.this, CustomerCartActivity.class);
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
