package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin-x on 7/11/15.
 */
public class OrderResponseModel {
    @Expose
    private boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @Expose
    private String id;

    public OrderResponseModel(boolean error, String errorMsg, String id) {
        this.error = error;
        this.errorMsg = errorMsg;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OrderResponseModel{" +
                "error=" + error +
                ", errorMsg='" + errorMsg + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
