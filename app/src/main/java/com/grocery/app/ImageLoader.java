package com.grocery.app;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;

public class ImageLoader {

    public static void loadImage(String firebaseStorageName, StorageReference ref, Context context, ImageView iv) {

        if (firebaseStorageName.trim().isEmpty()) {
            iv.setImageDrawable(AppCompatResources.getDrawable(context,
                    R.drawable.placeholder_image));
        } else {
            // Get reference to the image in Firebase Storage
            StorageReference imageRef = ref.child("/" + firebaseStorageName);

            // Get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Got the download URL for the file
                String downloadUrl = uri.toString();
                Log.d("FirebaseStorage", "Download URL: " + downloadUrl);
                // You can now use this URL to display the image or store it in your database

                // Load image using Glide
                Glide.with(context)
                        .load(downloadUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                                .error(R.drawable.error_image) // Error image if load fails
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(iv);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e("FirebaseStorage", "Error getting download URL", exception);
                iv.setImageDrawable(AppCompatResources.getDrawable(context,
                        R.drawable.error_image));
            });
        }
    }

    public static void loadImageFromUrl(String url, Context context, ImageView iv) {
        if (url.trim().isEmpty()) {
            iv.setImageDrawable(AppCompatResources.getDrawable(context,
                    R.drawable.placeholder_image));
        } else {
            // Load image using Glide
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                            .error(R.drawable.error_image) // Error image if load fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(iv);
        }
    }

}
