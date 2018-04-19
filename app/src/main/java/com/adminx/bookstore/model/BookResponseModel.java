package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by admin-x on 6/11/15.
 */

public class BookResponseModel {

    @Expose
    private boolean error;
    @Expose
    private boolean finish;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @Expose
    private ArrayList<Book> books;

    public BookResponseModel() {
        this.error = false;
        this.finish = false;
        this.errorMsg = "";
        this.books = new ArrayList<>();
    }

    public BookResponseModel(boolean error, boolean finish, ArrayList<Book> books, String errorMsg) {
        this.error = error;
        this.finish = finish;
        this.books = books;
        this.errorMsg = errorMsg;
    }

    /**
     * @return The error
     */
    public boolean getError() {
        return error;
    }

    /**
     * @param error The error
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * @return The finish
     */
    public boolean getFinish() {
        return finish;
    }

    /**
     * @param finish The finish
     */
    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    /**
     * @return The errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg The error_msg
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @return The books
     */
    public ArrayList<Book> getBooks() {
        return books;
    }

    /**
     * @param books The books
     */
    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "BookResponseModel{" +
                "error=" + error +
                ", finish=" + finish +
                ", errorMsg='" + errorMsg + '\'' +
                ", books=" + books +
                '}';
    }
}