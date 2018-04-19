package com.adminx.bookstore;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.helper.SessionManager;
import com.adminx.bookstore.model.CartRequestModel;
import com.adminx.bookstore.model.CartResponseModel;
import com.adminx.bookstore.model.OrderRequestBody;
import com.adminx.bookstore.model.OrderResponseModel;
import com.adminx.bookstore.model.User;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin-x on 7/8/15.
 */
public class OrderActivity extends AppCompatActivity {

    private SessionManager session;
    private SearchView searchView;
    private String userID;
    private int mCartCounter = 0;
    private ProgressDialog pDialog;


    private EditText etFullName, etAddress, etPhone, etCountry, etCity, etZipCode,
            etCardNumber, etCardHolderName, etCVC, etExpireMonth, etExpireYear;
    private TextView tvCartCounter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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

        initialize();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Set up the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
        }
        searchView = (SearchView) findViewById(R.id.action_search);

        loadCartCount();
    }

    private void initialize() {
        etFullName = (EditText) findViewById(R.id.fullName);
        etAddress = (EditText) findViewById(R.id.address);
        etPhone = (EditText) findViewById(R.id.phone);
        etCountry = (EditText) findViewById(R.id.country);
        etCity = (EditText) findViewById(R.id.city);
        etZipCode = (EditText) findViewById(R.id.zipCode);
        etCardNumber = (EditText) findViewById(R.id.cardNumber);
        etCardHolderName = (EditText) findViewById(R.id.cardHolderName);
        etCVC = (EditText) findViewById(R.id.CVC);
        etExpireMonth = (EditText) findViewById(R.id.expireMonth);
        etExpireYear = (EditText) findViewById(R.id.expireYear);

        Button btnBuy = (Button) findViewById(R.id.buy);

        etFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etFullName.getText().toString().trim().length() <= 0) {
                        etFullName.setError("You can't leave this empty");
                    } else {
                        etFullName.setError(null);
                    }
                }

            }
        });

        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etAddress.getText().toString().trim().length() <= 0) {
                        etAddress.setError("You can't leave this empty");
                    } else {
                        etAddress.setError(null);
                    }
                }

            }
        });

        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etPhone.getText().toString().trim().length() <= 0) {
                        etPhone.setError("You can't leave this empty");
                    } else {
                        etPhone.setError(null);
                    }
                }

            }
        });

        etCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etCountry.getText().toString().trim().length() <= 0) {
                        etCountry.setError("You can't leave this empty");
                    } else {
                        etCountry.setError(null);
                    }
                }

            }
        });

        etCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etCity.getText().toString().trim().length() <= 0) {
                        etCity.setError("You can't leave this empty");
                    } else {
                        etCity.setError(null);
                    }
                }

            }
        });

        etZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etZipCode.getText().toString().trim().length() <= 0) {
                        etZipCode.setError("You can't leave this empty");
                    } else {
                        etZipCode.setError(null);
                    }
                }

            }
        });

        etCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etCardNumber.getText().toString().trim().length() <= 0) {
                        etCardNumber.setError("You can't leave this empty");
                    } else if (etCardNumber.getText().toString().trim().length() < 14) {
                        etCardNumber.setError("Card number must be 14 number");
                    } else {
                        etCardNumber.setError(null);
                    }
                }

            }
        });

        etCardHolderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etCardHolderName.getText().toString().trim().length() <= 0) {
                        etCardHolderName.setError("You can't leave this empty");
                    } else {
                        etCardHolderName.setError(null);
                    }
                }

            }
        });

        etCVC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etCVC.getText().toString().trim().length() <= 0) {
                        etCVC.setError("You can't leave this empty");
                    } else {
                        etCVC.setError(null);
                    }
                }
            }
        });

        etExpireMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etExpireMonth.getText().toString().trim().length() <= 0) {
                        etExpireMonth.setError("You can't leave this empty");
                    } else {
                        etExpireMonth.setError(null);
                    }
                }

            }
        });

        etExpireYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etExpireYear.getText().toString().trim().length() <= 0) {
                        etExpireYear.setError("You can't leave this empty");
                    } else {
                        etExpireYear.setError(null);
                    }
                }

            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString();
                String address = etAddress.getText().toString();
                String phone = etPhone.getText().toString();
                String country = etCountry.getText().toString();
                String city = etCity.getText().toString();
                String zipCode = etZipCode.getText().toString();
                String cardNumber = etCardNumber.getText().toString();
                String cardHolderName = etCardHolderName.getText().toString();

                boolean inputValid = true;

                if (fullName.trim().length() <= 0) {
                    etFullName.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etFullName.setError(null);
                }

                if (address.trim().length() <= 0) {
                    etAddress.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etAddress.setError(null);
                }

                if (phone.trim().length() <= 0) {
                    etPhone.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etPhone.setError(null);
                }
                if (country.trim().length() <= 0) {
                    etCountry.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etCountry.setError(null);
                }
                if (city.trim().length() <= 0) {
                    etCity.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etCity.setError(null);
                }
                if (zipCode.trim().length() <= 0) {
                    etZipCode.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etZipCode.setError(null);
                }
                if (cardNumber.trim().length() <= 0) {
                    etCardNumber.setError("You can't leave this empty");
                    inputValid = false;
                } else if (etCardNumber.getText().toString().trim().length() < 14) {
                    etCardNumber.setError("Card number must be 14 number");
                    inputValid = false;
                } else {
                    etCardNumber.setError(null);
                }
                if (cardHolderName.trim().length() <= 0) {
                    etCardHolderName.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etCardHolderName.setError(null);
                }
                if (etCVC.getText().toString().trim().length() <= 0) {
                    etCVC.setError("You can't leave this empty");
                } else {
                    etCVC.setError(null);
                }
                if (etExpireMonth.getText().toString().trim().length() <= 0) {
                    etExpireMonth.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etExpireMonth.setError(null);
                }
                if (etExpireYear.getText().toString().trim().length() <= 0) {
                    etExpireYear.setError("You can't leave this empty");
                    inputValid = false;
                } else {
                    etExpireYear.setError(null);
                }

                if (inputValid) {
                    pDialog.setMessage("Placing order ...");
                    showDialog();
                    placeOrder();
                }

            }
        });
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

    private void placeOrder() {
        Runnable placeOrder = new Runnable() {
            @Override
            public void run() {
                OrderRequestBody orderRequestBody = new OrderRequestBody();
                orderRequestBody.setUserID(userID);
                orderRequestBody.setFullName(etFullName.getText().toString());
                orderRequestBody.setPhone(etPhone.getText().toString());
                orderRequestBody.setAddress(etAddress.getText().toString());
                orderRequestBody.setCountry(etCountry.getText().toString());
                orderRequestBody.setCity(etCity.getText().toString());
                orderRequestBody.setZipCode(etZipCode.getText().toString());
                orderRequestBody.setCardNumber(etCardNumber.getText().toString());
                orderRequestBody.setCardHolderName(etCardHolderName.getText().toString());
                orderRequestBody.setCVC(etCVC.getText().toString());
                orderRequestBody.setExpireMonth(etExpireMonth.getText().toString());
                orderRequestBody.setExpireYear(etExpireYear.getText().toString());

                RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(AppConfig.URL_ORDER).build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<OrderResponseModel> callback = new Callback<OrderResponseModel>() {
                    @Override
                    public void success(OrderResponseModel orderResponseBody, Response response) {
                        //Done! now continue on the UI thread
                        updateOrder(orderResponseBody);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };
                WebServiceAPI.getOrderResponse(orderRequestBody, callback);
            }
        };
        Thread order = new Thread(null, placeOrder);
        order.start();
    }

    private void updateOrder(final OrderResponseModel orderResponseBody) {
        Runnable updateOrder = new Runnable() {
            @Override
            public void run() {
                if (!orderResponseBody.getError()) {
                    hideDialog();
                    Toast.makeText(getApplicationContext(), "Your order has been placed", Toast.LENGTH_LONG).show();
                    loadCartCount();
                } else {
                    hideDialog();
                    Toast.makeText(getApplicationContext(), orderResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }
        };
        runOnUiThread(updateOrder);
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
        Runnable updateCartCounter = new Runnable() {
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
        };
        runOnUiThread(updateCartCounter);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
