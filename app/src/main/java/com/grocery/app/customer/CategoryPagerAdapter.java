package com.grocery.app.customer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.grocery.app.model.Category;

import java.util.List;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private final List<Category> categories;

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ProductCategoryFragment.newInstance(categories.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}

