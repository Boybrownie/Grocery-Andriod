package com.grocery.app.admin.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grocery.app.R;
import com.grocery.app.model.Category;

import java.util.UUID;

public class AdminCategoryFragment extends Fragment {

    private DatabaseReference dbRef;
    private EditText etCategoryName;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Adding...");
        progressDialog.setMessage("Please wait while the category get added.");
        progressDialog.setCancelable(false);

        dbRef = FirebaseDatabase.getInstance().getReference("categories");

        etCategoryName = view.findViewById(R.id.etCategoryName);
        Button btnAddCategory = view.findViewById(R.id.btnAddCategory);

        btnAddCategory.setOnClickListener(v -> addCategory());
    }

    private void addCategory() {
        String categoryName = etCategoryName.getText().toString().trim();

        if (TextUtils.isEmpty(categoryName)) {
            etCategoryName.setError("Category name is required");
            return;
        }

        checkCategoryExists(categoryName);
    }

    private void checkCategoryExists(String categoryName) {
        progressDialog.show();
        dbRef.orderByChild("title").equalTo(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    etCategoryName.setError("Category already exists");
                } else {
                    addNewCategory(categoryName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewCategory(String categoryName) {
        String categoryId = UUID.randomUUID().toString();
        Category newCategory = new Category(categoryId, categoryName);

        dbRef.child(categoryId).setValue(newCategory).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                etCategoryName.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
