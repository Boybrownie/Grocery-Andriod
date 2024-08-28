package com.grocery.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.grocery.app.ImageLoader;
import com.grocery.app.R;
import com.grocery.app.customer.CustomerCartActivity;
import com.grocery.app.model.Cart;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewHolder> {

    private final Context mContext;
    private final List<Cart> mList;
    private final List<String> mKey;
    private final StorageReference storageReference;
    private final boolean isMembershipUser;

    public CartAdapter(Context mContext, List<Cart> mList, List<String> cartItemKey, StorageReference storageRef, boolean isMembershipUser) {
        this.mContext = mContext;
        this.mList = mList;
        this.mKey = cartItemKey;
        this.storageReference = storageRef;
        this.isMembershipUser = isMembershipUser;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_child_layout,
                parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Cart model = mList.get(position);
        String key = mKey.get(position);

        holder.productTitle.setText(model.getProductTitle());
        holder.productPrice.setText(String.format("CAD%,d", model.getProductPrice()));

        // Apply discount if user is a membership user
        if (isMembershipUser) {
            int originalPrice = model.getProductPrice();
            int discountPrice = originalPrice - (originalPrice / 10);

            holder.productPrice.setText((String.format("CAD%,d", discountPrice)) + " Membership price");
            holder.offerPrice.setText(String.format("CAD%,d", originalPrice));
            holder.offerPrice.setVisibility(View.VISIBLE);
            holder.offerPrice.setPaintFlags(holder.offerPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.productPrice.setText(String.format("CAD%,d", model.getProductPrice()));
            holder.offerPrice.setVisibility(View.GONE);
        }

        ImageLoader.loadImage(model.getProductImageName(), storageReference, mContext, holder.productImage);

        holder.addToCart.setText("Remove from cart");

        holder.addToCart.setOnClickListener((v) -> {
            ((CustomerCartActivity) mContext).removeItemsFromCart(model);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final TextView productTitle;
        private final TextView productPrice;
        private final Button addToCart;
        private final TextView offerPrice;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.cv_productImage);
            productTitle = itemView.findViewById(R.id.tv_productTitle);
            productPrice = itemView.findViewById(R.id.tv_productPrice);
            addToCart = itemView.findViewById(R.id.iv_addToCart);
            offerPrice = itemView.findViewById(R.id.tv_offerPrice);
        }
    }

}
