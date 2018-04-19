package com.adminx.bookstore;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.Adapter.ViewPagerAdapter;
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
 * Created by admin-x on 6/14/15.
 */

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SearchView searchView;
    private TextView tvCartCounter = null;
    private int mCartCounter = 0;
    private String userID;

    private TabLayout tabs;
    private ViewPagerAdapter adapter;
    private Toolbar toolbar;
    private int numbOfTabs = 4;
    private final CharSequence Titles[] = {"Categories", "Home", "New", "Top Selling"};
    private Fragment fragments[] = {new CategoriesFragment(), new HomeFragment(), new NewFragment(), new TopSellingFragment()};

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, fragments, Titles for the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, Titles, numbOfTabs);

        // Assigning ViewPager View and setting the adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (TabLayout) findViewById(R.id.tabs);

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(1, false);

        loadCartCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("searchKey", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final View menuCart =  MenuItemCompat.getActionView(menu.findItem(R.id.action_cart));
        tvCartCounter = (TextView) menuCart.findViewById(R.id.cartCounter);
        updateCartCounter(mCartCounter);
        menuCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    public void success(CartResponseModel cartResponseBody, Response response) {
                        if (!cartResponseBody.getError()) {
                            updateCartCounter(cartResponseBody.getCount());
                        }
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
                } else {
                    tvCartCounter.setVisibility(View.VISIBLE);
                    tvCartCounter.setText(Integer.toString(cartCounter));
                }
            }
        };
        runOnUiThread(updateCartCounter);
    }
}