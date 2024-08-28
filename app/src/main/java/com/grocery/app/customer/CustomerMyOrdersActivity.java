package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grocery.app.GroceryApplication;
import com.grocery.app.R;
import com.grocery.app.adapter.OrderAdapter;
import com.grocery.app.model.Customer;
import com.grocery.app.model.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomerMyOrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrderList;
    private ImageView ivOrdersEmpty;

    private DatabaseReference dbRef;

    private ProgressDialog progressDialog;

    private List<Order> orderList;

    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_my_orders);

        // Initialize views
        rvOrderList = findViewById(R.id.rv_view_orders_items);
        ivOrdersEmpty = findViewById(R.id.iv_cart_empty);

        rvOrderList.setLayoutManager(new LinearLayoutManager(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        orderList = new ArrayList<>();

        setTitle("My orders");

        dbRef = FirebaseDatabase.getInstance().getReference();

        customer = ((GroceryApplication) getApplicationContext()).customer;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        loadOrders();
    }

    private void loadOrders() {
        dbRef.child("orders").orderByChild("customerId").equalTo(customer.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();

                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);

                            if (order != null) {
                                orderList.add(order);
                            }
                        }

                        // Update UI based on the order list
                        if (orderList.isEmpty()) {
                            rvOrderList.setVisibility(View.GONE);
                            ivOrdersEmpty.setVisibility(View.VISIBLE);
                        } else {
                            rvOrderList.setVisibility(View.VISIBLE);
                            ivOrdersEmpty.setVisibility(View.GONE);
                        }

                        OrderAdapter adapter = new OrderAdapter(CustomerMyOrdersActivity.this, orderList);
                        rvOrderList.setAdapter(adapter);

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CustomerMyOrdersActivity.this, "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); // Handle the up button press
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(CustomerMyOrdersActivity.this, CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_cart) {
            Intent intent = new Intent(CustomerMyOrdersActivity.this, CustomerCartActivity.class);
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
