package com.adminx.bookstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.adminx.bookstore.BookGridCardHandler;
import com.adminx.bookstore.model.Book;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin-x on 6/19/15.
 */
public class BookGridAdapter extends BaseAdapter {
    private final List<Book> viewModels;

    private final Context mContext;
    private final LayoutInflater mInflater;

    public BookGridAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.viewModels = new ArrayList<>();
    }

    public BookGridAdapter(Context mContext, List<Book> viewModels) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.viewModels = viewModels;
    }

    public List<Book> getViewModels() {
        return this.viewModels;
    }

    @Override
    public int getCount() {
        return this.viewModels.size();
    }

    @Override
    public Book getItem(int position) {
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
        final Book viewModel = getItem(position);

        BookGridCardHandler card;
        // If the convertView is null we need to create it
        if (convertView == null) {
            convertView = this.mInflater.inflate(BookGridCardHandler.LAYOUT, parent, false);

            // In that case we also need to create a new row and attach it to the newly created View
            card = new BookGridCardHandler(this.mContext, convertView);
            convertView.setTag(card);
        }

        // After that we get the row associated with this View and bind the view model to it
        card = (BookGridCardHandler) convertView.getTag();
        card.bind(viewModel);
        return convertView;
    }
}