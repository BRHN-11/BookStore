package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 7/4/15.
 */
public class Cart {
    @Expose
    private String id;
    @SerializedName("customer_id")
    @Expose
    private String customerID;
    @SerializedName("book_id")
    @Expose
    private String bookID;
    @SerializedName("book_title")
    @Expose
    private String bookTitle;
    @SerializedName("book_price")
    @Expose
    private int bookPrice;
    @Expose
    private int quantity;

    public Cart(String id, String customerID, String bookID, String bookTitle, int bookPrice, int quantity) {
        this.id = id;
        this.customerID = customerID;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", customerID='" + customerID + '\'' +
                ", bookID='" + bookID + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookPrice=" + bookPrice +
                ", quantity=" + quantity +
                '}';
    }
}
