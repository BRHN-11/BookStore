package com.adminx.bookstore.API;

import com.adminx.bookstore.model.BookRequestModel;
import com.adminx.bookstore.model.BookResponseModel;
import com.adminx.bookstore.model.CartRequestModel;
import com.adminx.bookstore.model.CartResponseModel;
import com.adminx.bookstore.model.CategoryResponseModel;
import com.adminx.bookstore.model.OrderRequestBody;
import com.adminx.bookstore.model.OrderResponseModel;
import com.adminx.bookstore.model.UserRequestModel;
import com.adminx.bookstore.model.UserResponseModel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by admin-x on 6/7/15.
 */
public interface WebServiceAPI {
    @POST("/")
    void getUserResponse(@Body UserRequestModel userRequestBody, Callback<UserResponseModel> cb);

    @POST("/")
    void getBookResponse(@Body BookRequestModel bookRequestBody, Callback<BookResponseModel> cb);

    @POST("/")
    void getHomeResponse(@Body BookRequestModel categoryRequestBody, Callback<CategoryResponseModel> cb);

    @POST("/")
    void getCategoryResponse(@Body BookRequestModel categoryRequestBody, Callback<CategoryResponseModel> cb);

    @POST("/")
    void getCartResponse(@Body CartRequestModel cartRequestBody, Callback<CartResponseModel> cb);

    @POST("/")
    void getOrderResponse(@Body OrderRequestBody orderRequestBody, Callback<OrderResponseModel> cb);
}
