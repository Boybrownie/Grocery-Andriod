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
import com.grocery.app.customer.CustomerShopActivity;
import com.grocery.app.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> {

    private final Context mContext;
    private final List<Product> mList;
    private final StorageReference storageReference;
    private final Boolean isMember;

    public ProductAdapter(Context mContext, List<Product> mList, StorageReference storageRef, Boolean isMember) {
        this.mContext = mContext;
        this.mList = mList;
        this.storageReference = storageRef;
        this.isMember = isMember;
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
        Product model = mList.get(position);

        holder.productTitle.setText(model.getTitle());
        holder.productPrice.setText(String.format("CAD%,d", model.getPrice() ));

        // Apply discount if user is a membership user
        if (isMember) {
            int originalPrice = model.getPrice();
            holder.productPrice.setText((String.format("CAD%,d", model.getMembershipPrice() )) + " Membership price");
            holder.discountPrice.setText(String.format("CAD%,d", originalPrice ));
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountPrice.setPaintFlags(holder.discountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.productPrice.setText(String.format("CAD%,d", model.getPrice() ));
            holder.discountPrice.setVisibility(View.GONE);
        }

        ImageLoader.loadImage(model.getImageName(), storageReference, mContext, holder.productImage);

        holder.addToCart.setOnClickListener((v) -> ((CustomerShopActivity) mContext).addItemToCart(model));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final TextView productTitle;
        private final TextView productPrice;
        private final TextView discountPrice;
        private final Button addToCart;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.cv_productImage);
            productTitle = itemView.findViewById(R.id.tv_productTitle);
            productPrice = itemView.findViewById(R.id.tv_productPrice);
            addToCart = itemView.findViewById(R.id.iv_addToCart);
            discountPrice = itemView.findViewById(R.id.tv_offerPrice);
        }
    }

}
