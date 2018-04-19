package com.adminx.bookstore;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.adminx.bookstore.model.Category;

/**
 * Created by admin-x on 6/28/15.
 */
public class CategoryListRowHandler {

    public static final int LAYOUT = R.layout.row_card_category;

    private final TextView catName;


    public CategoryListRowHandler(Context context, View convertView) {
        Context context1 = context;
        this.catName = (TextView) convertView.findViewById(R.id.tvCatName);

    }

    public void bind(Category categoryViewModel) {
        this.catName.setText(categoryViewModel.getName());
    }

}
