package com.grocery.app.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grocery.app.GroceryApplication;
import com.grocery.app.PaymentActivity;
import com.grocery.app.R;
import com.grocery.app.adapter.CartAdapter;
import com.grocery.app.model.Cart;
import com.grocery.app.model.Customer;
import com.grocery.app.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerCartActivity extends AppCompatActivity {

    private RecyclerView rvCartList;
    private TextView tvTotalPrice;
    private ImageView ivCartEmpty;

    private DatabaseReference dbRef;
    private StorageReference storageRef;

    private ProgressDialog progressDialog;

    private List<Cart> cartList;
    private List<String> cartItemKey;

    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        // Initialize views
        rvCartList = findViewById(R.id.rv_view_cart_items);
        Button btnProceed = findViewById(R.id.btn_placer_order);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        ivCartEmpty = findViewById(R.id.iv_cart_empty);

        rvCartList.setLayoutManager(new LinearLayoutManager(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        storageRef = FirebaseStorage.getInstance().getReference();
        cartList = new ArrayList<>();
        cartItemKey = new ArrayList<>();

        setTitle("Cart");

        dbRef = FirebaseDatabase.getInstance().getReference();

        customer = ((GroceryApplication) getApplicationContext()).customer;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        loadCartItems();

        btnProceed.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(CustomerCartActivity.this, "Cart is empty. Add items to cart before placing an order.", Toast.LENGTH_SHORT).show();
            } else {
                // Launch PaymentActivity
                Intent intent = new Intent(CustomerCartActivity.this, PaymentActivity.class);
                intent.putExtra("extra_price", calculateTotalPrice());
                startActivityForResult(intent, 1001); //1 is the request code
            }
        });
    }

    private void loadCartItems() {
        Query query = dbRef.child("cart").orderByChild("customerId")
                .equalTo(customer.getId());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                cartItemKey.clear();
                int totalPrice = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Cart cart = snap.getValue(Cart.class);
                    if (cart != null) {
                        cartList.add(cart);
                        cartItemKey.add(snap.getKey());
                        totalPrice += cart.getProductPrice(); // Assuming price is in cents
                    }
                }

                updateCartUI(totalPrice);

                CartAdapter adapter = new CartAdapter(CustomerCartActivity.this, cartList, cartItemKey, storageRef,
                        customer.isMember());
                rvCartList.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(CustomerCartActivity.this, "Error fetching cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long calculateTotalPrice() {
        long totalPrice = 0;
        for (Cart cart : cartList) {
            if (customer.isMember()) {
                totalPrice = cart.getMembershipPrice(); // Apply 10% discount for members
            } else {
                totalPrice += cart.getProductPrice(); // Assuming price is in cents
            }
        }
        return totalPrice;
    }

    private void updateCartUI(int totalPrice) {
        int price = totalPrice;
        if (customer.isMember()) {
            price = price - (price / 10);
        }

        if (cartList.isEmpty()) {
            rvCartList.setVisibility(View.GONE);
            ivCartEmpty.setVisibility(View.VISIBLE);
            tvTotalPrice.setVisibility(View.GONE);
        } else {
            rvCartList.setVisibility(View.VISIBLE);
            ivCartEmpty.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.VISIBLE);
            tvTotalPrice.setText(String.format("Total Price: CAD%,d", price));
        }
    }

    public void addItemsToOrderList() {
        String orderId = UUID.randomUUID().toString();

        List<Cart> tempList = new ArrayList<>();
        // For membership customers, add the discounted price.
        if (customer.isMember()) {
            for (Cart cart : cartList) {
                tempList.add(new Cart(cart.getId(), cart.getProductId(), cart.getProductTitle(),
                        cart.getMembershipPrice(), cart.getProductImageName(), cart.getCustomerId()));
            }
        } else {
            tempList = cartList;
        }

        Order order = new Order(orderId, customer.getId(), tempList, true, customer);

        dbRef.child("orders").child(orderId).setValue(order).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), orderId + " placed successfully", Toast.LENGTH_SHORT).show();

                Query query = dbRef.child("cart").orderByChild("customerId")
                        .equalTo(customer.getId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                            // Remove each item that matches the query
                            cartSnapshot.getRef().removeValue();
                        }

                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                        Toast.makeText(getApplicationContext(), "Error removing item from cart order",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Error placing order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeItemsFromCart(Cart cart) {
        progressDialog.setTitle("Removing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbRef.child("cart").child(cart.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), cart.getProductTitle() + " removed from cart", Toast.LENGTH_SHORT).show();
                        // Reload the cart items to reflect changes
                        loadCartItems();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error removing item from cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Payment successful, proceed with order
            addItemsToOrderList();
        } else {
            // Payment failed or cancelled
            Toast.makeText(this, "Payment failed or cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); // Handle the up button press
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(CustomerCartActivity.this, CustomerLoginActivity.class);
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
