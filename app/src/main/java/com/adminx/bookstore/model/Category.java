package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by admin-x on 6/24/15.
 */


public class Category {

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private ArrayList<Book> books;

    public Category() {
        this.id = "";
        this.name = "";
        this.books = new ArrayList<>();
    }

    public Category(String id, String name, ArrayList<Book> books) {
        this.id = id;
        this.name = name;
        this.books = books;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
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
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", books=" + books +
                '}';
    }
}