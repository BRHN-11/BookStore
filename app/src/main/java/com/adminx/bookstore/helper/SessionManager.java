package com.adminx.bookstore.helper;

/**
 * Created by admin-x on 6/5/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.adminx.bookstore.model.User;

public class SessionManager {


    // Shared preferences file name
    private static final String PREF_NAME = "BookStoreLogin";
    private static final String KEY_LOGGEDIN = "LoggedIn";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATE = "date";
    // Shared Preferences
    private SharedPreferences pref;
    private Editor editor;
    private Context mContext;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLoginInfo(User user) {
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.putString(KEY_DATE, user.getDate());
        editor.commit();

    }

    public User getLogInInfo() {
        User user = new User();
        user.setId(pref.getString(KEY_ID, ""));
        user.setName(pref.getString(KEY_NAME, ""));
        user.setEmail(pref.getString(KEY_EMAIL, ""));
        user.setPhone(pref.getString(KEY_PHONE, ""));
        user.setAddress(pref.getString(KEY_ADDRESS, ""));
        user.setDate(pref.getString(KEY_DATE, ""));
        editor.commit();

        return user;
    }

    public void setLogin(boolean loggedIn) {
        editor.putBoolean(KEY_LOGGEDIN, loggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_LOGGEDIN, false);
    }
}