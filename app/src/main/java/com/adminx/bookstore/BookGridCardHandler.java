package com.adminx.bookstore;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.model.Book;
import com.squareup.picasso.Picasso;

/**
 * Created by admin-x on 6/20/15.
 */
public class BookGridCardHandler {
    public static final int LAYOUT = R.layout.grid_card_book;

    private final Context context;
    private final ImageView cover;
    private final TextView title;
    private final TextView price;

    public BookGridCardHandler(Context context, View convertView) {
        this.context = context;
        this.cover = (ImageView) convertView.findViewById(R.id.bookPic);
        this.title = (TextView) convertView.findViewById(R.id.bookTitle);
        this.price = (TextView) convertView.findViewById(R.id.bookPrice);
    }

    public void bind(Book bookViewModel) {
        this.title.setText(bookViewModel.getTitle());
        this.price.setText("$" + bookViewModel.getPrice());
        Picasso.with(context).load(AppConfig.URL_BOOK_PIC + bookViewModel.getPicURL()).error(R.drawable.book_error).resize(100, 135).into(this.cover);
    }
}
