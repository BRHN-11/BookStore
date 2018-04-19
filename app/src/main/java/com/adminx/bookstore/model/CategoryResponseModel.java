package com.adminx.bookstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin-x on 6/24/15.
 */

public class CategoryResponseModel {

    @Expose
    private boolean error;
    @Expose
    private boolean finish;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @Expose
    private ArrayList<Category> Categories;

    public CategoryResponseModel() {
        this.error = false;
        this.finish = false;
        this.errorMsg = "";
        Categories = new ArrayList<>();
    }

    public CategoryResponseModel(boolean error, boolean finish, String errorMsg, ArrayList<Category> categories) {
        this.error = error;
        this.finish = finish;
        this.errorMsg = errorMsg;
        Categories = categories;
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
     * @return The Categories
     */
    public List<Category> getCategories() {
        return Categories;
    }

    /**
     * @param Catogaries The Categories
     */
    public void setCategories(ArrayList<Category> Catogaries) {
        this.Categories = Catogaries;
    }

    @Override
    public String toString() {
        return "CategoryResponseModel{" +
                "error=" + error +
                ", finish=" + finish +
                ", errorMsg='" + errorMsg + '\'' +
                ", Categories=" + Categories +
                '}';
    }
}

