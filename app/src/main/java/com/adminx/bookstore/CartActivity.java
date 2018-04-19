package com.adminx.bookstore;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.helper.SessionManager;
import com.adminx.bookstore.model.CartRequestModel;
import com.adminx.bookstore.model.CartResponseModel;
import com.adminx.bookstore.model.User;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin-x on 7/4/15.
 */
public class CartActivity extends AppCompatActivity {
    private SessionManager session;
    private SearchView searchView;
    private String userID;

    private TextView tvCartCounter = null;
    private int mCartCounter = 0;
    private CartResponseModel cartResponseBody;
    private double cartTotal = 0.0;

    private TableLayout table;
    private TableRow tableRow;
    private TextView tvCartTotal;


    //Since we cant update our UI from a thread this Runnable takes care of that!
    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            if (!cartResponseBody.getError()) {
                if (cartResponseBody.getCart() != null && cartResponseBody.getCart().size() > 0) {
                    for (int i = 0; i < cartResponseBody.getCart().size(); i++) {
                        /** Create a TableRow dynamically **/
                        tableRow = new TableRow(getApplicationContext());
                        tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                        /** Creating a TextView to add to the row **/
                        TextView tvBook = new TextView(getApplicationContext());
                        tvBook.setText(cartResponseBody.getCart().get(i).getBookTitle());
                        tvBook.setTextColor(Color.BLACK);
                        tvBook.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                        tvBook.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        tvBook.setPadding(5, 5, 5, 0);
                        tableRow.addView(tvBook);  // Adding textView to tablerow.

                        /** Creating another textview **/
                        TextView tvPrice = new TextView(getApplicationContext());
                        tvPrice.setText("$" + String.valueOf(cartResponseBody.getCart().get(i).getBookPrice()));
                        tvPrice.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        tvPrice.setPadding(5, 5, 5, 0);
                        tvPrice.setTextColor(Color.BLACK);
                        tvPrice.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                        tableRow.addView(tvPrice); // Adding textView to tablerow.

                        /** Creating another textview **/
                        TextView tvQuantity = new TextView(getApplicationContext());
                        tvQuantity.setText(String.valueOf(cartResponseBody.getCart().get(i).getQuantity()));
                        tvQuantity.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        tvQuantity.setPadding(5, 5, 5, 0);
                        tvQuantity.setTextColor(Color.BLACK);
                        tvQuantity.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                        tableRow.addView(tvQuantity); // Adding textView to tablerow.

                        /** Creating another textview **/
                        TextView tvTotal = new TextView(getApplicationContext());
                        tvTotal.setText("$" + cartResponseBody.getCart().get(i).getBookPrice() * cartResponseBody.getCart().get(i).getQuantity());
                        tvTotal.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        tvTotal.setPadding(5, 5, 5, 0);
                        tvTotal.setTextColor(Color.BLACK);
                        tvTotal.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                        tableRow.addView(tvTotal); // Adding textView to tablerow.

                        // Add the TableRow to the TableLayout
                        table.addView(tableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        cartTotal = cartTotal +cartResponseBody.getCart().get(i).getBookPrice() * cartResponseBody.getCart().get(i).getQuantity();
                    }
                    tvCartTotal.setText("$"+ cartTotal);
                }
            } else {
                Toast.makeText(getApplicationContext(), cartResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private Runnable loadCart = new Runnable() {
        @Override
        public void run() {
            CartRequestModel cartRequestBody = new CartRequestModel();
            cartRequestBody.setAction(CartRequestModel.GET);
            cartRequestBody.setUserID(userID);
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(AppConfig.URL_CART).build();
            WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

            Callback<CartResponseModel> callback = new Callback<CartResponseModel>() {
                @Override
                public void success(CartResponseModel o, Response response) {
                    cartResponseBody = o;
                    //Done! now continue on the UI thread
                    runOnUiThread(returnRes);
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            WebServiceAPI.getCartResponse(cartRequestBody, callback);
        }
    };

    private Runnable loadCartCount = new Runnable() {
        @Override
        public void run() {
            CartRequestModel cartRequestBody = new CartRequestModel();
            cartRequestBody.setAction(CartRequestModel.COUNT);
            cartRequestBody.setUserID(userID);
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(AppConfig.URL_CART).build();
            WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

            Callback<CartResponseModel> callback = new Callback<CartResponseModel>() {
                @Override
                public void success(CartResponseModel cartResponseBody, Response response) {
                    updateCartCounter(cartResponseBody.getCount());
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            WebServiceAPI.getCartResponse(cartRequestBody, callback);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        table = (TableLayout) findViewById(R.id.table);

        Button btnBuy = (Button) findViewById(R.id.btnBuy);
        tvCartTotal = (TextView) findViewById(R.id.tvTotal);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

        TextView tvBook = new TextView(getApplicationContext());
        TextView tvPrice = new TextView(getApplicationContext());
        TextView tvQuantity = new TextView(getApplicationContext());
        TextView tvTotal = new TextView(getApplicationContext());

        addHeaders();

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User isn't logged in. Take him to login activity
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        userID = session.getLogInInfo().getId();

        // Set up the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
        }
        searchView = (SearchView) findViewById(R.id.action_search);

        Thread thread = new Thread(null, loadCart);
        thread.start();

        Thread CartCount = new Thread(null, loadCartCount);
        CartCount.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread thread = new Thread(null, loadCartCount);
        thread.start();
    }

    private void addHeaders() {
        /** Create a TableRow dynamically **/
        tableRow = new TableRow(this);
        tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        /** Creating a TextView to add to the row **/
        TextView tvBookHeader = new TextView(this);
        tvBookHeader.setText("Book");
        tvBookHeader.setTextColor(Color.BLACK);
        tvBookHeader.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tvBookHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tvBookHeader.setPadding(5, 5, 5, 0);
        tableRow.addView(tvBookHeader);  // Adding textView to tablerow.

        /** Creating another textview **/
        TextView tvPriceHeader = new TextView(this);
        tvPriceHeader.setText("Price");
        tvPriceHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tvPriceHeader.setPadding(5, 5, 5, 0);
        tvPriceHeader.setTextColor(Color.BLACK);
        tvPriceHeader.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tableRow.addView(tvPriceHeader); // Adding textView to tablerow.

        /** Creating another textview **/
        TextView tvQuantityHeader = new TextView(this);
        tvQuantityHeader.setText("Quantity");
        tvQuantityHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tvQuantityHeader.setTextColor(Color.BLACK);
        tvQuantityHeader.setPadding(5, 5, 5, 0);
        tvQuantityHeader.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tableRow.addView(tvQuantityHeader); // Adding textView to tablerow.

        /** Creating another textview **/
        TextView tvTotalHeader = new TextView(this);
        tvTotalHeader.setText("Total");
        tvTotalHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tvTotalHeader.setPadding(5, 5, 5, 0);
        tvTotalHeader.setTextColor(Color.BLACK);
        tvTotalHeader.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tableRow.addView(tvTotalHeader); // Adding textView to tablerow.

        // Add the TableRow to the TableLayout
        table.addView(tableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("searchKey", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final View menuCart = menu.findItem(R.id.action_cart).getActionView();
        tvCartCounter = (TextView) menuCart.findViewById(R.id.cartCounter);
        updateCartCounter(mCartCounter);
        menuCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void updateCartCounter(final int cartCounter) {
        mCartCounter = cartCounter;
        if (tvCartCounter == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cartCounter == 0) {
                    tvCartCounter.setVisibility(View.INVISIBLE);
                    supportInvalidateOptionsMenu();
                } else {
                    tvCartCounter.setVisibility(View.VISIBLE);
                    tvCartCounter.setText(Integer.toString(cartCounter));
                    supportInvalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                session.setLogin(false);
                Intent intent;
                session.setLoginInfo(new User());
                // Launching the login activity
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_cart:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
