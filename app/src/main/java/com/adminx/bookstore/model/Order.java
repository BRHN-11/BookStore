package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 7/10/15.
 */
class Order {
    @Expose
    private String id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @Expose
    private String address;
    @Expose
    private String phone;
    @Expose
    private String country;
    @Expose
    private String city;
    @Expose
    private String zip;
    @SerializedName("trans_id")
    @Expose
    private String transID;


}
