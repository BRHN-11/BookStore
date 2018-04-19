package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin-x on 7/4/15.
 */
public class CartResponseModel {
    @Expose
    private boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @Expose
    private int count;
    @Expose
    private ArrayList<Cart> cart;

    public CartResponseModel(boolean error, ArrayList<Cart> cart, int count, String errorMsg) {
        this.error = error;
        this.cart = cart;
        this.count = count;
        this.errorMsg = errorMsg;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getCount() {

        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Cart> getCart() {
        return cart;
    }

    public void setCart(ArrayList<Cart> cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return "CartResponseModel{" +
                "error=" + error +
                ", errorMsg='" + errorMsg + '\'' +
                ", cart=" + cart +
                '}';
    }
}
