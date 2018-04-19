package com.adminx.bookstore.model;

/**
 * Created by admin-x on 6/7/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponseModel {

    @Expose
    private boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @Expose
    private User user;

    private UserResponseModel() {
        this.error = false;
        this.errorMsg = "";
        this.user = new User();
    }

    public UserResponseModel(boolean error, String errorMsg, User user) {
        this();
        this.error = error;
        this.errorMsg = errorMsg;
        this.user = user;
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
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserResponseModel{" +
                "error=" + error +
                ", errorMsg='" + errorMsg + '\'' +
                ", user=" + user +
                '}';
    }
}
