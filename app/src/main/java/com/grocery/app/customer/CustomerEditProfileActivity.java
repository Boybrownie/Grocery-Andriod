package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.grocery.app.GroceryApplication;
import com.grocery.app.R;
import com.grocery.app.model.Customer;

public class CustomerEditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etStreetAddress, etCity, etZipCode, etCountry;
    private Button btnSave;
    private DatabaseReference dbReference;
    private Customer currentCustomer;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please wait while we update your profile");
        progressDialog.setCancelable(false);

        // Initialize Firebase Database reference
        dbReference = FirebaseDatabase.getInstance().getReference("customers");

        // Initialize views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etStreetAddress = findViewById(R.id.et_street_address);
        etCity = findViewById(R.id.et_city);
        etZipCode = findViewById(R.id.et_zip_code);
        etCountry = findViewById(R.id.et_country);
        btnSave = findViewById(R.id.btn_update);

        // Get the current customer object (assuming it's stored in the application context)
        currentCustomer = ((GroceryApplication) getApplicationContext()).customer;

        // Pre-fill the fields with the current customer data or default values
        if (currentCustomer != null) {
            etName.setText(currentCustomer.getName() != null ? currentCustomer.getName() : "John Doe");
            etEmail.setText(currentCustomer.getEmail() != null ? currentCustomer.getEmail() : "");
            etPassword.setText(currentCustomer.getPassword() != null ? currentCustomer.getPassword() : "");
            etStreetAddress.setText(currentCustomer.getStreetAddress() != null ? currentCustomer.getStreetAddress() : "123 Default St");
            etCity.setText(currentCustomer.getCity() != null ? currentCustomer.getCity() : "Default City");
            etZipCode.setText(currentCustomer.getZipCode() != null ? currentCustomer.getZipCode() : "00000");
            etCountry.setText(currentCustomer.getCountry() != null ? currentCustomer.getCountry() : "Canada");
        } else {
            // Set default values if the currentCustomer is null
            etName.setText("John Doe");
            etEmail.setText("");
            etPassword.setText("");
            etStreetAddress.setText("123 Default St");
            etCity.setText("Default City");
            etZipCode.setText("00000");
            etCountry.setText("Canada");
        }

        // Save button click listener
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                updateCustomerProfile();
            }
        });
    }

    // Validate user input
    private boolean validateInputs() {
        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Password is required");
            return false;
        }
        if (TextUtils.isEmpty(etStreetAddress.getText().toString())) {
            etStreetAddress.setError("Street Address is required");
            return false;
        }
        if (TextUtils.isEmpty(etCity.getText().toString())) {
            etCity.setError("City is required");
            return false;
        }
        if (TextUtils.isEmpty(etZipCode.getText().toString())) {
            etZipCode.setError("ZIP Code is required");
            return false;
        }
        return true;
    }

    // Update customer profile
    private void updateCustomerProfile() {
        String newEmail = etEmail.getText().toString();
        String newPassword = etPassword.getText().toString();
        String newStreetAddress = etStreetAddress.getText().toString();
        String newCity = etCity.getText().toString();
        String newZipCode = etZipCode.getText().toString();
        String newCountry = etCountry.getText().toString();

        // Validate email and other inputs
        if (!validateInputs()) {
            return;
        }

        progressDialog.show();

        // Check if the new email is already used by another user
        Query emailQuery = dbReference.orderByChild("email").equalTo(newEmail);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean emailExists = false;
                progressDialog.dismiss();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer existingCustomer = snapshot.getValue(Customer.class);
                    if (existingCustomer != null && !existingCustomer.getId().equals(currentCustomer.getId())) {
                        emailExists = true;
                        break;
                    }
                }

                if (emailExists) {
                    Toast.makeText(CustomerEditProfileActivity.this, "Email is already in use by another account.", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the customer object with the new data
                    currentCustomer.setName(etName.getText().toString());
                    currentCustomer.setEmail(newEmail);
                    currentCustomer.setPassword(newPassword);
                    currentCustomer.setStreetAddress(newStreetAddress);
                    currentCustomer.setCity(newCity);
                    currentCustomer.setZipCode(newZipCode);
                    currentCustomer.setCountry(newCountry);

                    // Save the updated customer data to the Firebase Realtime Database
                    dbReference.child(currentCustomer.getId()).setValue(currentCustomer)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CustomerEditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();  // Close the activity after saving
                                } else {
                                    Toast.makeText(CustomerEditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CustomerEditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(CustomerEditProfileActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
