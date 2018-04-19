package com.adminx.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.helper.SessionManager;
import com.adminx.bookstore.model.BookRequestModel;
import com.adminx.bookstore.model.BookResponseModel;
import com.adminx.bookstore.model.CartRequestModel;
import com.adminx.bookstore.model.CartResponseModel;
import com.adminx.bookstore.model.User;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by admin-x on 6/25/15.
 */
public class BookActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private final String LOG_TAG = BookActivity.class.getSimpleName();

    private SearchView searchView;
    private TextView tvCartCounter = null;
    private int mCartCounter = 0;
    private ImageView pic;
    private TextView title;
    private TextView price;
    private TextView author;
    private TextView publisher;
    private TextView publishingDate;
    private TextView category;
    private TextView details;
    private EditText etQuantity;
    private String bookID;
    private String userID;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            userID = session.getLogInInfo().getId();
        }

        // Set up the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        searchView = (SearchView) findViewById(R.id.action_search);

        pic = (ImageView) findViewById(R.id.ivBookPic);
        title = (TextView) findViewById(R.id.tvBookTitle);
        price = (TextView) findViewById(R.id.tvBookPrice);
        author = (TextView) findViewById(R.id.tvAuthorName);
        publisher = (TextView) findViewById(R.id.tvPublisherName);
        publishingDate = (TextView) findViewById(R.id.tvPublishingData);
        category = (TextView) findViewById(R.id.tvCategoryName);
        details = (TextView) findViewById(R.id.tvBookDetails);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        Button btnAddToCart = (Button) findViewById(R.id.btnAddToCart);

        etQuantity.setOnFocusChangeListener(this);
        btnAddToCart.setOnClickListener(this);
        bookID = getIntent().getExtras().getString("bookID");

        loadBook();

        loadCartCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
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
                // Launching the cart activity
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onClick(View v) {
        if (isInteger(etQuantity.getText().toString()) && Integer.parseInt(etQuantity.getText().toString()) > 0) {
            etQuantity.setError(null);
            etQuantity.setBackgroundColor(getResources().getColor(R.color.input_quantity));
            etQuantity.clearFocus();
            addToCart();
        } else {
            Toast.makeText(getApplicationContext(), "Quantity must be decimal number 1 or more", Toast.LENGTH_SHORT).show();
            etQuantity.setError("Quantity must be decimal number 1 or more");
            etQuantity.setBackgroundColor(getResources().getColor(R.color.input_quantity_error));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (isInteger(etQuantity.getText().toString()) && Integer.parseInt(etQuantity.getText().toString()) > 0) {
                etQuantity.setError(null);
                etQuantity.setBackgroundColor(getResources().getColor(R.color.input_quantity));
            } else {
                etQuantity.setError("Quantity must be decimal number 1 or more");
                etQuantity.setBackgroundColor(getResources().getColor(R.color.input_quantity_error));

            }
        }

    }

    private void loadBook() {
        Runnable loadBook = new Runnable() {
            @Override
            public void run() {
                BookRequestModel bookRequestBody = new BookRequestModel();
                bookRequestBody.setBookID(bookID);
                OkHttpClient client = new OkHttpClient();
                client.setFollowRedirects(true);
                RestAdapter restAdapter = new RestAdapter.Builder().setClient(new OkClient(client)).setEndpoint(AppConfig.URL_BOOK).build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<BookResponseModel> callback = new Callback<BookResponseModel>() {
                    @Override
                    public void success(BookResponseModel bookResponseBody, Response response) {
                        updateBook(bookResponseBody);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(LOG_TAG, error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                WebServiceAPI.getBookResponse(bookRequestBody, callback);
            }
        };

        Thread thread = new Thread(null, loadBook);
        thread.start();
    }

    private void updateBook(final BookResponseModel bookResponseBody) {
        //Since we cant update our UI from a thread this Runnable takes care of that!
        Runnable updateBookOnUI = new Runnable() {
            @Override
            public void run() {
                if (!bookResponseBody.getError()) {
                    if (bookResponseBody.getBooks() != null && bookResponseBody.getBooks().size() > 0) {
                        title.setText(bookResponseBody.getBooks().get(0).getTitle());
                        author.setText(bookResponseBody.getBooks().get(0).getAuthorName());
                        publisher.setText(bookResponseBody.getBooks().get(0).getPublisherName());
                        price.setText("$" + String.valueOf(bookResponseBody.getBooks().get(0).getPrice()));
                        publishingDate.setText(bookResponseBody.getBooks().get(0).getPublishingDate());
                        category.setText(bookResponseBody.getBooks().get(0).getCategoryName());
                        details.setText(bookResponseBody.getBooks().get(0).getDetalis());
                        Picasso.with(getApplicationContext()).load(AppConfig.URL_BOOK_PIC + bookResponseBody.getBooks().get(0).getPicURL())
                                .error(R.drawable.book_error).resize(230, 360).into(pic);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), bookResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }
        };
        runOnUiThread(updateBookOnUI);
    }

    private void addToCart() {
        Runnable addCart = new Runnable() {
            @Override
            public void run() {
                CartRequestModel cartRequestBody = new CartRequestModel();
                cartRequestBody.setAction(CartRequestModel.ADD);
                cartRequestBody.setUserID(userID);
                cartRequestBody.setBookID(bookID);
                cartRequestBody.setQuantity(Integer.parseInt(etQuantity.getText().toString()));
                RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(AppConfig.URL_CART).build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<CartResponseModel> callback = new Callback<CartResponseModel>() {
                    @Override
                    public void success(CartResponseModel cartResponseBody, Response response) {
                        //Done! now continue on the UI thread
                        updateAddToCart(cartResponseBody);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                WebServiceAPI.getCartResponse(cartRequestBody, callback);
            }
        };

        Thread threadCartCount = new Thread(null, addCart);
        threadCartCount.start();
    }

    private void updateAddToCart(final CartResponseModel cartResponseBody) {
        Runnable cartAdded = new Runnable() {
            @Override
            public void run() {
                if (!cartResponseBody.getError()) {
                    if (cartResponseBody.getCart() != null && cartResponseBody.getCart().size() > 0) {
                        Toast.makeText(getApplicationContext(), etQuantity.getText().toString() + " copies of "
                                + title.getText() + "Added to cart", Toast.LENGTH_SHORT).show();
                        updateCartCounter(cartResponseBody.getCount());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), cartResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }
        };
        runOnUiThread(cartAdded);
    }

    private void loadCartCount() {
        Runnable loadCartCount = new Runnable() {
            @Override
            public void run() {
                CartRequestModel cartRequestBody = new CartRequestModel();
                cartRequestBody.setAction(CartRequestModel.COUNT);
                cartRequestBody.setUserID(userID);
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(AppConfig.URL_CART).build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<CartResponseModel> callback = new Callback<CartResponseModel>() {
                    @Override
                    public void success(CartResponseModel o, Response response) {
                        updateCartCounter(o.getCount());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                WebServiceAPI.getCartResponse(cartRequestBody, callback);
            }
        };
        Thread thread = new Thread(null, loadCartCount);
        thread.start();
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

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
}