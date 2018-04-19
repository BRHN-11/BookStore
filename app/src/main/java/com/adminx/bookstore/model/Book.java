package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 6/11/15.
 */

public class Book {

    @Expose
    private String id;
    @Expose
    private String title;
    @Expose
    private Integer price;
    @Expose
    private String detalis;
    @SerializedName("pic_url")
    @Expose
    private String picURL;
    @SerializedName("pub_data")
    @Expose
    private String publishingDate;
    @SerializedName("author_name")
    @Expose
    private String authorName;
    @SerializedName("author_id")
    @Expose
    private String authorID;
    @SerializedName("pub_name")
    @Expose
    private String publisherName;
    @SerializedName("pub_id")
    @Expose
    private String publisherID;
    @SerializedName("cat_name")
    @Expose
    private String categoryName;
    @SerializedName("cat_id")
    @Expose
    private String categoryID;


    public Book() {
        this.id = "";
        this.title = "";
        this.price = 0;
        this.detalis = "";
        this.picURL = "";
        this.publishingDate = "";
        this.authorName = "";
        this.authorID = "";
        this.publisherName = "";
        this.publisherID = "";
        this.categoryName = "";
        this.categoryID = "";
    }

    public Book(String id, String title, Integer price, String detalis, String picURL, String publishingDate, String authorName, String authorID, String publisherName, String publisherID, String categoryName, String categoryID) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.detalis = detalis;
        this.picURL = picURL;
        this.publishingDate = publishingDate;
        this.authorName = authorName;
        this.authorID = authorID;
        this.publisherName = publisherName;
        this.publisherID = publisherID;
        this.categoryName = categoryName;
        this.categoryID = categoryID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDetalis() {
        return detalis;
    }

    public void setDetalis(String detalis) {
        this.detalis = detalis;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(String publishingDate) {
        this.publishingDate = publishingDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", detalis='" + detalis + '\'' +
                ", picURL='" + picURL + '\'' +
                ", publishingDate='" + publishingDate + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorID='" + authorID + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", publisherID='" + publisherID + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryID='" + categoryID + '\'' +
                '}';
    }
}