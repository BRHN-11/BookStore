package com.adminx.bookstore.model;

/**
 * Created by admin-x on 6/7/15.
 */

import com.google.gson.annotations.Expose;

public class User {

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String email;
    @Expose
    private String phone;
    @Expose
    private String address;
    @Expose
    private String date;

    public User() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.date = "";
    }

    public User(String id, String name, String email, String phone, String address, String date) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.date = date;

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
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The phone
     */

    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone
     */

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The address
     */

    public String getAddress() {
        return address;
    }

    /**
     * @param address The email
     */

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return The date
     */

    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}