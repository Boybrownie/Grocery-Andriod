package com.grocery.app.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.grocery.app.R;
import com.grocery.app.model.Cart;
import com.grocery.app.model.Order;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.viewHolder> {

    private final Context mContext;
    private final List<Order> mList;
    private final DatabaseReference dbRef;

    public AdminOrderAdapter(Context mContext, List<Order> mList, DatabaseReference dbRef) {
        this.mContext = mContext;
        this.mList = mList;
        this.dbRef = dbRef;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.admin_order_child_layout,
                parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Order order = mList.get(position);

        if (order.isActive()) {
            holder.status.setText("Status: Processing");
            holder.status.setTextColor(mContext.getColor(R.color.light_yellow));
        } else {
            holder.status.setText("Status: Completed");
            holder.status.setTextColor(mContext.getColor(R.color.textGreen));
            holder.closeOrder.setVisibility(View.GONE);
        }

        holder.orderId.setText("Order Id: " + order.getOrderId());
        holder.orderValue.setText("Order value: " + String.format("CAD%,d", order.getTotalPrice()));
        holder.itemCount.setText("Total Items: " + order.getProducts().size());
        holder.tvName.setText("Name: " + order.getCustomer().getName());
        holder.tvAddress.setText("Address: " + order.getCustomer().getStreetAddress() + ", " +
                order.getCustomer().getCity() + ", " +
                order.getCustomer().getCountry() + " - " +
                order.getCustomer().getZipCode()
        );

        holder.viewOrder.setOnClickListener((v) -> showOrderDetailsDialog(order));

        holder.closeOrder.setOnClickListener((v) -> closeOrder(order, position));
    }

    private void closeOrder(Order order, int position) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Closing");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Closing order");
        progressDialog.show();

        dbRef.child("orders").child(order.getOrderId()).child("active").setValue(false)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Update the local order list and refresh the adapter
                        mList.get(position).setActive(false);
                        notifyItemChanged(position);
                        Toast.makeText(mContext, "Order closed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Failed to close order", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showOrderDetailsDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Items");

        StringBuilder message = new StringBuilder();
        int serialNumber = 1;

        for (Cart item : order.getProducts()) {
            message.append(serialNumber).append(". ")
                    .append(item.getProductTitle())
                    .append("\n");
            serialNumber++;
        }

        builder.setMessage(message.toString());

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView orderId;
        private final TextView itemCount;
        private final TextView orderValue;
        private final TextView status;
        private final Button closeOrder;
        private final Button viewOrder;
        private final TextView tvName;
        private final TextView tvAddress;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.tv_orderId);
            itemCount = itemView.findViewById(R.id.tv_itemCount);
            orderValue = itemView.findViewById(R.id.tv_status);
            status = itemView.findViewById(R.id.tv_orderValue);
            closeOrder = itemView.findViewById(R.id.btn_closeOrder);
            viewOrder = itemView.findViewById(R.id.btn_viewItems);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }

}
