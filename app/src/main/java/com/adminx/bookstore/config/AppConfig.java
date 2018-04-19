package com.adminx.bookstore.config;

/**
 * Created by admin-x on 6/5/15.
 */
public class AppConfig {
    // Server user login url
    private static final String URL_SERVER = "http://192.168.1.2:84/";

    // Server user login url
    public static final String URL_LOGIN = AppConfig.URL_SERVER + "android/login.php";

    // Server user register url
    public static final String URL_REGISTER = AppConfig.URL_SERVER + "android/register.php";

    // Server book url
    public static final String URL_BOOK = AppConfig.URL_SERVER + "android/getbook.php";

    // Server get books page url
    public static final String URL_BOOK_PAGE = AppConfig.URL_SERVER + "android/getbookspage.php";

    // Server get books page url
    public static final String URL_CATEGORY_PAGE = AppConfig.URL_SERVER + "android/getcatpage.php";

    // Server home url
    public static final String URL_HOME = AppConfig.URL_SERVER + "android/home.php";

    // server book pic url
    public static final String URL_BOOK_PIC = AppConfig.URL_SERVER + "style/imgs/";

    // Server cart url
    public static final String URL_CART = AppConfig.URL_SERVER + "android/cart.php";

    // Server order url
    public static final String URL_ORDER = AppConfig.URL_SERVER + "android/order.php";
}