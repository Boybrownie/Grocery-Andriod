package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grocery.app.GroceryApplication;
import com.grocery.app.R;
import com.grocery.app.model.Cart;
import com.grocery.app.model.Category;
import com.grocery.app.model.Customer;
import com.grocery.app.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerShopActivity extends AppCompatActivity {

    private List<Category> categories;
    private CategoryPagerAdapter adapter;

    ViewPager2 viewPager;

    ProgressDialog progressDialog;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_shop);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        Button btnProceed = findViewById(R.id.btn_proceed);

        dbRef = FirebaseDatabase.getInstance().getReference();

        categories = new ArrayList<>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");

        // Fetch categories from Firebase
        fetchCategoriesFromFirebase();

        adapter = new CategoryPagerAdapter(CustomerShopActivity.this, categories);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(categories.get(position).getTitle())
        ).attach();


        btnProceed.setOnClickListener(v -> startActivity(new Intent(
                CustomerShopActivity.this, CustomerCartActivity.class)));
    }

    private void fetchCategoriesFromFirebase() {

        progressDialog.show();

        dbRef.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }

                adapter.notifyDataSetChanged();

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
                Log.e("CustomerShopActivity", "Failed to load categories", error.toException());
            }
        });
    }

    public void addItemToCart(Product product) {

        progressDialog.show();

        // Implement logic to add the product to the cart
        String id = UUID.randomUUID().toString();
        Customer customer = ((GroceryApplication) getApplicationContext()).customer;
        Cart cart = new Cart(id, product.getId(), product.getTitle(), product.getMembershipPrice(),
                product.getImageName(), customer.getId());

        dbRef.child("cart").child(id).setValue(cart).addOnSuccessListener(aVoid -> {
                    // Handle success, e.g., show a toast message
                    progressDialog.dismiss();
                    Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    // Handle failure, e.g., show a toast message
                    Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); // Handle the up button press
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(CustomerShopActivity.this, CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        menu.getItem(0).setVisible(false); // Hide any unwanted menu items
        return true;
    }
}
