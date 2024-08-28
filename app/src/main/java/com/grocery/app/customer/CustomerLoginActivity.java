package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.regex.Pattern;

public class CustomerLoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_login;
    TextView tvRegisterButton;

    TextView tv_forgotPassword;

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    DatabaseReference dbInstance;

    Pattern pat = Pattern.compile(emailRegex);

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        setTitle("Customer");

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        tvRegisterButton = findViewById(R.id.tv_registerButton);

        tv_forgotPassword.setOnClickListener(v -> startActivity(new Intent(CustomerLoginActivity.this, CustomerSignUpActivity.class)));

        progressDialog = new ProgressDialog(this);

        dbInstance = FirebaseDatabase.getInstance().getReference();

        btn_login.setOnClickListener(v -> performLogin());

        tvRegisterButton.setOnClickListener(v -> startActivity(new Intent(CustomerLoginActivity.this, CustomerSignUpActivity.class)));

        tv_forgotPassword.setOnClickListener(v -> startActivity(new Intent(CustomerLoginActivity.this, CustomerPasswordResetActivity.class)));

    }

    private void performLogin() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (email.isEmpty()) {
            et_email.setError("Please Enter Email");
        } else if (!pat.matcher(email).matches()) {
            et_email.setError("Please Enter a valid Email");
        } else if (password.isEmpty()) {
            et_password.setError("Please input Password");
        } else if (password.length() < 6) {
            et_password.setError("Password too short");
        } else {
            progressDialog.setMessage("Login in to your Account....");
            progressDialog.setTitle("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // validate credentials with email and password
            Query emailQuery = dbInstance.child("customers").orderByChild("email").equalTo(email);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Customer customer = snapshot.getValue(Customer.class);
                            if (customer != null && password.equals(customer.getPassword())) {
                                // check if password is valid
                                Toast.makeText(CustomerLoginActivity.this, "SignIn success.", Toast.LENGTH_SHORT).show();
                                sendUserToDashboard(customer);
                                return; // Exit once a match is found
                            }
                        }
                        Toast.makeText(CustomerLoginActivity.this, "Invalid password..!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CustomerLoginActivity.this, "Email is not registered..!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Log.e("Firebase", "Database error", databaseError.toException());
                    Toast.makeText(CustomerLoginActivity.this, "Error validating credentials, " + "try again later..!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // if password change required send student to password update screen
    private void sendUserToDashboard(Customer customer) {
        ((GroceryApplication) getApplicationContext()).customer = customer;
        Intent intent = new Intent(CustomerLoginActivity.this, CustomerDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// Handle the back button action
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}