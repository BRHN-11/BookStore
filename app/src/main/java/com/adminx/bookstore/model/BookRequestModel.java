package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 6/11/15.
 */

public class BookRequestModel {

    @Expose
    private int start;
    @Expose
    private int numItems;
    @Expose
    private String categoryId;
    @Expose
    private String searchKey;
    @SerializedName("book_id")
    @Expose
    private String bookID;

    public BookRequestModel() {
        this.categoryId = null;
        this.start = 0;
        this.numItems = 0;
        this.searchKey = null;
        this.bookID = null;
    }

    public BookRequestModel(String categoryId, int start, String bookID, int numItems, String searchKey) {
        this.categoryId = categoryId;
        this.start = start;
        this.numItems = numItems;
        this.searchKey = searchKey;
        this.bookID = bookID;

    }

    /**
     * @return The searchKey
     */
    public String getSearchKey() {
        return searchKey;
    }

    /**
     * @param searchKey The searchKey
     */
    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    /**
     * @return The categoryId
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId The categoryId
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return The start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return The numItems
     */
    public int getNumItems() {
        return numItems;
    }

    /**
     * @param numItems The numItems
     */
    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    /**
     * @return The bookID
     */
    public String getBookID() {
        return bookID;
    }

    /**
     * @param bookID The book_id
     */
    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    @Override
    public String toString() {
        return "BookRequestModel{" +
                "start=" + start +
                ", numItems=" + numItems +
                ", categoryId='" + categoryId + '\'' +
                ", searchKey='" + searchKey + '\'' +
                ", bookID='" + bookID + '\'' +
                '}';
    }
}