package com.adminx.bookstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adminx.bookstore.CategoryListRowHandler;
import com.adminx.bookstore.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin-x on 6/28/15.
 */
public class CategoryListAdapter extends BaseAdapter {
    private final List<Category> viewModels;

    private final Context context;
    private final LayoutInflater inflater;

    public CategoryListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = new ArrayList<>();
    }

    public CategoryListAdapter(Context context, List<Category> viewModels) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
    }

    public List<Category> getViewModels() {
        return this.viewModels;
    }

    @Override
    public int getCount() {
        return this.viewModels.size();
    }

    @Override
    public Category getItem(int position) {
        return this.viewModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        // We only need to implement this if we have multiple rows with a different layout.
        // All your rows use the same layout so we can just return 0.
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // We get the view model for this position
        final Category viewModel = getItem(position);

        CategoryListRowHandler row;
        // If the convertView is null we need to create it
        if (convertView == null) {
            convertView = this.inflater.inflate(CategoryListRowHandler.LAYOUT, parent, false);

            // In that case we also need to create a new row and attach it to the newly created View
            row = new CategoryListRowHandler(this.context, convertView);
            convertView.setTag(row);
        }

        // After that we get the row associated with this View and bind the view model to it
        row = (CategoryListRowHandler) convertView.getTag();
        row.bind(viewModel);

        return convertView;
    }

}
