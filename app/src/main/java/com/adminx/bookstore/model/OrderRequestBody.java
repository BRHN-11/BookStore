package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 7/10/15.
 */
public class OrderRequestBody {

    @SerializedName("customer_id")
    @Expose
    private String userID;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @Expose
    private String phone;
    @Expose
    private String address;
    @Expose
    private String country;
    @Expose
    private String city;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("cc_number")
    @Expose
    private String cardNumber;
    @SerializedName("cc_name")
    @Expose
    private String cardHolderName;
    @Expose
    private String CVC;
    @SerializedName("expire_month")
    @Expose
    private String expireMonth;
    @SerializedName("expire_year")
    @Expose
    private String expireYear;

    public OrderRequestBody() {
        this.fullName = "";
        this.userID = "";
        this.phone = "";
        this.address = "";
        this.country = "";
        this.city = "";
        this.zipCode = "";
        this.cardNumber = "";
        this.cardHolderName = "";
        this.CVC = "";
        this.expireMonth = "";
        this.expireYear = "";
    }

    public OrderRequestBody(String fullName, String userID, String phone, String address, String country, String city, String zipCode, String cardNumber, String cardHolderName, String CVC, String expireMonth, String expireYear) {
        this.fullName = fullName;
        this.userID = userID;
        this.phone = phone;
        this.address = address;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.CVC = CVC;
        this.expireMonth = expireMonth;
        this.expireYear = expireYear;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCVC() {
        return CVC;
    }

    public void setCVC(String CVC) {
        this.CVC = CVC;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    @Override
    public String toString() {
        return "OrderRequestBody{" +
                "userID='" + userID + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", CVC='" + CVC + '\'' +
                ", expireMonth='" + expireMonth + '\'' +
                ", expireYear='" + expireYear + '\'' +
                '}';
    }
}
