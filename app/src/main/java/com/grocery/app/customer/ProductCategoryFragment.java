package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grocery.app.GroceryApplication;
import com.grocery.app.R;
import com.grocery.app.adapter.ProductAdapter;
import com.grocery.app.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private String categoryId;

    ProgressDialog progressDialog;

    public static ProductCategoryFragment newInstance(String categoryId) {
        ProductCategoryFragment fragment = new ProductCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString(ARG_CATEGORY_ID);
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while we fetch items..");
        progressDialog.setCancelable(false);

        dbRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        productList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_category, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewProductsByCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProductsByCategory(categoryId);

        return view;
    }

    private void loadProductsByCategory(String categoryId) {

        progressDialog.show();

        dbRef.child("products").orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product product = snap.getValue(Product.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        adapter = new ProductAdapter(getContext(), productList, storageRef,
                                ((GroceryApplication) getActivity().getApplicationContext()).customer.isMember());
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching products", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }
}

