package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 7/5/15.
 */
public class CartRequestModel {
    public static final String GET = "get";
    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String COUNT = "count";

    @Expose
    private String action;
    @SerializedName("cart_id")
    @Expose
    private String cartID;
    @SerializedName("customer_id")
    @Expose
    private String userID;
    @SerializedName("book_id")
    @Expose
    private String bookID;
    @Expose
    private int quantity;

    public CartRequestModel() {
        this.action = "";
        this.cartID = "";
        this.userID = "";
        this.bookID = "";
        this.quantity = 0;
    }

    public CartRequestModel(String action, String cartID, String userID, String bookID, int quantity) {
        this.action = action;
        this.cartID = cartID;
        this.userID = userID;
        this.bookID = bookID;
        this.quantity = quantity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartRequestModel{" +
                "action='" + action + '\'' +
                ", cartID='" + cartID + '\'' +
                ", userID='" + userID + '\'' +
                ", bookID='" + bookID + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
