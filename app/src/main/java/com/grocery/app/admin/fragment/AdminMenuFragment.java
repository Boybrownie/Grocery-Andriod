package com.grocery.app.admin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grocery.app.R;

public class AdminMenuFragment extends Fragment {

    private OnFragmentChangeListener fragmentChangeListener;

    public interface OnFragmentChangeListener {
        void replaceFragment(Fragment fragment);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout addProduct = view.findViewById(R.id.add_products);
        LinearLayout viewOrder = view.findViewById(R.id.view_order);
        LinearLayout newCategory = view.findViewById(R.id.add_new_category);

        addProduct.setOnClickListener(v -> {
            if (fragmentChangeListener != null) {
                fragmentChangeListener.replaceFragment(new AdminAddProductFragment());
            }
        });

        viewOrder.setOnClickListener(v -> {
            if (fragmentChangeListener != null) {
                fragmentChangeListener.replaceFragment(new AdminViewOrderFragment());
            }
        });

        newCategory.setOnClickListener(v -> {
            if (fragmentChangeListener != null) {
                fragmentChangeListener.replaceFragment(new AdminCategoryFragment());
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangeListener) {
            fragmentChangeListener = (OnFragmentChangeListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnFragmentChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentChangeListener = null;
    }
}
