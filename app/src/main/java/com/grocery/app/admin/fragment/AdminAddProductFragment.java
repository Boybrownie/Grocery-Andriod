package com.grocery.app.admin.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grocery.app.ImageLoader;
import com.grocery.app.R;
import com.grocery.app.model.Category;
import com.grocery.app.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminAddProductFragment extends Fragment {

    private EditText editTextProductName;
    private EditText editTextProductPrice;
    private EditText editTextProductUrl;
    private ImageView imageViewProductImage;
    private Spinner spinnerProductCategory;
    private ProgressDialog progressDialog;

    private StorageReference storageReference;
    private DatabaseReference dbReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Storage and Database
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        dbReference = FirebaseDatabase.getInstance().getReference();

        editTextProductName = view.findViewById(R.id.editTextProductName);
        editTextProductPrice = view.findViewById(R.id.editTextProductPrice);
        editTextProductUrl = view.findViewById(R.id.editTextProductUrl);
        imageViewProductImage = view.findViewById(R.id.imageViewProductImage);
        spinnerProductCategory = view.findViewById(R.id.spinnerProductCategory);
        Button buttonAddProduct = view.findViewById(R.id.buttonAddProduct);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Adding Product");
        progressDialog.setMessage("Please wait...");

        // Set up the EditText to accept only integer input
        editTextProductPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editTextProductPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        // Load image when URL is entered
        editTextProductUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String imageName = s.toString().trim();
                if (!imageName.isEmpty()) {
                    ImageLoader.loadImage(imageName, storageReference, getContext(), imageViewProductImage);
                } else {
                    imageViewProductImage.setImageDrawable(null); // Clear image if name is empty
                }
            }
        });

        // Load categories into spinner
        loadCategories();

        buttonAddProduct.setOnClickListener(v -> {
            String productName = editTextProductName.getText().toString().trim();
            String productPrice = editTextProductPrice.getText().toString().trim();
            String productUrl = editTextProductUrl.getText().toString().trim();
            String categoryId = ((Category) spinnerProductCategory.getSelectedItem()).getId();

            if (productName.isEmpty() || productPrice.isEmpty() || productUrl.isEmpty()) {
                Toast.makeText(getContext(), "Please complete all fields and enter a valid image name", Toast.LENGTH_SHORT).show();
            } else if (!isValidPrice(productPrice)) {
                Toast.makeText(getContext(), "Please enter a valid price of at least 1 rupee", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                Integer price = Integer.parseInt(productPrice);
                addNewProduct(productName, price, productUrl, categoryId);
            }
        });
    }

    private void loadCategories() {
        // Show progress dialog before starting to load categories
        progressDialog.setTitle("Loading Categories");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        dbReference.child("categories").get().addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(),
                        R.layout.spinner_item, categoryList);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerProductCategory.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPrice(String price) {
        try {
            int value = Integer.parseInt(price);
            return value >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addNewProduct(String name, Integer price, String imageName, String categoryId) {
        String id = UUID.randomUUID().toString();
        Product product = new Product(id, name, price * 100, imageName, categoryId);
        dbReference.child("products").child(id).setValue(product)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), name + " Added..", Toast.LENGTH_SHORT).show();
                        resetForm();
                    } else {
                        Toast.makeText(getContext(), "Error adding product, try again later..!",
                                Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Registration failed", task.getException());
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error adding product, try again later..!",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void resetForm() {
        editTextProductUrl.setText("");
        editTextProductName.setText("");
        editTextProductPrice.setText("");
        imageViewProductImage.setImageDrawable(null);
        spinnerProductCategory.setSelection(0);
    }
}
