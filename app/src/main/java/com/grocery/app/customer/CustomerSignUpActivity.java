package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grocery.app.GroceryApplication;
import com.grocery.app.R;
import com.grocery.app.model.Customer;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText et_name, et_email, et_password,
            et_confirmPassword, et_streetAddress, et_city, et_zipCode;
    private CircleImageView ic_avatar;

    private final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern pat = Pattern.compile(emailRegex);

    private ProgressDialog progressDialog;
    private DatabaseReference dbReference;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        setTitle("Customer - new user");

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        dbReference = FirebaseDatabase.getInstance().getReference();

        // Initialize EditText fields and other views
        ic_avatar = findViewById(R.id.ic_avatar);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        et_streetAddress = findViewById(R.id.et_street_address);
        et_city = findViewById(R.id.et_city);
        et_zipCode = findViewById(R.id.et_zip_code);
        Button btn_Register = findViewById(R.id.btn_register);
        TextView tv_loginBtn = findViewById(R.id.tv_loginButton);

        progressDialog = new ProgressDialog(this);

        ic_avatar.setOnClickListener(v -> openFileChooser());
        tv_loginBtn.setOnClickListener(v -> startActivity(new Intent(CustomerSignUpActivity.this, CustomerLoginActivity.class)));
        btn_Register.setOnClickListener(v -> performAuth());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap = adjustImageOrientation(bitmap, imageUri);
                ic_avatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap adjustImageOrientation(Bitmap bitmap, Uri uri) {
        try {
            ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(uri));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap; // No rotation needed
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private void uploadAvatarAndCreateAccount(String name, String email, String password, String streetAddress, String city, String zipCode, String country) {
        progressDialog.setMessage("Creating your Account....");
        progressDialog.setTitle("Creating");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String customerId = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child("avatars/" + customerId + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String avatarUrl = uri.toString();
                    Customer customer = new Customer(name, email, password, customerId, System.currentTimeMillis(), streetAddress, city, zipCode, country, avatarUrl);
                    createCustomer(customer);
                }).addOnFailureListener(e -> {
                    Log.e("Grocery Error", "uploadAvatarAndCreateAccount: ", e);
                    progressDialog.dismiss();
                    Toast.makeText(CustomerSignUpActivity.this, "Error uploading avatar, try again later..!", Toast.LENGTH_SHORT).show();
                })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerSignUpActivity.this, "Error uploading avatar, try again later..!", Toast.LENGTH_SHORT).show();
                });
    }

    private void createCustomer(Customer customer) {
        dbReference.child("customers").child(customer.getId()).setValue(customer).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(CustomerSignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CustomerSignUpActivity.this, CustomerDashboardActivity.class);
                ((GroceryApplication) getApplicationContext()).customer = customer;
                startActivity(intent);
            } else {
                Toast.makeText(CustomerSignUpActivity.this, "Error registering user, try again later..!", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Registration failed", task.getException());
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(CustomerSignUpActivity.this, "Error registering user, try again later..!", Toast.LENGTH_SHORT).show();
        });
    }

    private void performAuth() {
        String name = et_name.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String confirmPassword = et_confirmPassword.getText().toString();
        String streetAddress = et_streetAddress.getText().toString();
        String city = et_city.getText().toString();
        String zipCode = et_zipCode.getText().toString();
        String country = "Canada"; // Default country

        if (name.isEmpty()) {
            et_name.setError("Please enter your name");
        } else if (email.isEmpty()) {
            et_email.setError("Please enter your email");
        } else if (!pat.matcher(email).matches()) {
            et_email.setError("Please enter a valid email");
        } else if (password.isEmpty()) {
            et_password.setError("Please input password");
        } else if (password.length() < 6) {
            et_password.setError("Password too short");
        } else if (!confirmPassword.equals(password)) {
            et_confirmPassword.setError("Passwords don't match");
        } else if (streetAddress.isEmpty()) {
            et_streetAddress.setError("Please enter your street address");
        } else if (city.isEmpty()) {
            et_city.setError("Please enter your city");
        } else if (zipCode.isEmpty()) {
            et_zipCode.setError("Please enter your ZIP code");
        } else if (imageUri == null) {
            Toast.makeText(this, "Please select an avatar image", Toast.LENGTH_SHORT).show();
        } else {
            verifyEmailExistenceAndCreateAccount(name, email, password, streetAddress, city, zipCode, country);
        }
    }

    private void verifyEmailExistenceAndCreateAccount(String name, String email, String password, String streetAddress, String city, String zipCode, String country) {
        progressDialog.setMessage("Checking email...");
        progressDialog.setTitle("Checking");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Query emailQuery = dbReference.child("customers").orderByChild("email").equalTo(email);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Custom logs", "onDataChange: Triggered");
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerSignUpActivity.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadAvatarAndCreateAccount(name, email, password, streetAddress, city, zipCode, country);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.e("Firebase", "Database error", databaseError.toException());
                Toast.makeText(CustomerSignUpActivity.this, "Error registering user, try again later..!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        // You can use a library like FileUtils to get the extension from the Uri, or use the file path
        String[] projection = {MediaStore.Images.Media.MIME_TYPE};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String mimeType = cursor.getString(0);
            cursor.close();
            if (mimeType != null) {
                return mimeType.substring(mimeType.lastIndexOf('/') + 1);
            }
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
