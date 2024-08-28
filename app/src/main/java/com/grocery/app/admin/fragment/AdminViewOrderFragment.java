package com.grocery.app.admin.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grocery.app.R;
import com.grocery.app.adapter.AdminOrderAdapter;
import com.grocery.app.model.Order;

import java.util.ArrayList;
import java.util.List;

public class AdminViewOrderFragment extends Fragment {

    private RecyclerView rvOrderList;
    private ImageView ivOrdersEmpty;

    private DatabaseReference dbRef;

    private ProgressDialog progressDialog;

    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_view_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvOrderList = view.findViewById(R.id.rv_view_orders_items);
        ivOrdersEmpty = view.findViewById(R.id.iv_cart_empty);

        rvOrderList.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();

        dbRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        loadOrders();
    }

    private void loadOrders() {
        dbRef.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
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

                AdminOrderAdapter adapter = new AdminOrderAdapter(getContext(), orderList, dbRef);
                rvOrderList.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error loading orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
