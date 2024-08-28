package com.grocery.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grocery.app.R;
import com.grocery.app.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.viewHolder> {

    private final Context mContext;
    private final List<Order> mList;

    public OrderAdapter(Context mContext, List<Order> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_child_layout,
                parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Order model = mList.get(position);

        if (model.isActive()) {
            holder.status.setText("Status: Processing");
            holder.status.setTextColor(mContext.getColor(R.color.light_yellow));
        } else {
            holder.status.setText("Status: Completed");
            holder.status.setTextColor(mContext.getColor(R.color.textGreen));
        }

        holder.orderId.setText("Order Id: " + model.getOrderId());
        holder.orderValue.setText("Order value: " + String.format("CAD%,d", model.getTotalPrice()));
        holder.itemCount.setText("Total Items: " + model.getProducts().size());
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

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.tv_orderId);
            itemCount = itemView.findViewById(R.id.tv_itemCount);
            orderValue = itemView.findViewById(R.id.tv_status);
            status = itemView.findViewById(R.id.tv_orderValue);
        }
    }

}
