package com.adminx.bookstore;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.model.Book;
import com.squareup.picasso.Picasso;

/**
 * Created by admin-x on 6/19/15.
 */
public class BookListRowHandler {

    public static final int LAYOUT = R.layout.row_card_book;

    private final Context context;
    private final TextView title;
    private final TextView author;
    private final TextView price;
    private final ImageView cover;

    public BookListRowHandler(Context context, View convertView) {
        this.context = context;
        this.cover = (ImageView) convertView.findViewById(R.id.bookPic);
        this.title = (TextView) convertView.findViewById(R.id.bookTitle);
        this.author = (TextView) convertView.findViewById(R.id.bookAuthor);
        this.price = (TextView) convertView.findViewById(R.id.bookPrice);
    }

    public void bind(Book bookViewModel) {
        this.title.setText(bookViewModel.getTitle());
        this.price.setText("$" + bookViewModel.getPrice());
        this.author.setText("Author: " + bookViewModel.getAuthorName());
        Picasso.with(context).load(AppConfig.URL_BOOK_PIC + bookViewModel.getPicURL()).error(R.drawable.book_error).resize(60, 90).into(cover);
    }
}