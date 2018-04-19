package com.adminx.bookstore;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.Adapter.BookListAdapter;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.helper.SessionManager;
import com.adminx.bookstore.model.BookRequestModel;
import com.adminx.bookstore.model.BookResponseModel;
import com.adminx.bookstore.model.CartRequestModel;
import com.adminx.bookstore.model.CartResponseModel;
import com.adminx.bookstore.model.User;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin-x on 6/30/15.
 */
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private Toolbar toolbar;
    private LinearLayout layout;
    private GridViewWithHeaderAndFooter listView;
    private View footerView;
    private BookListAdapter adapter;
    private Boolean loadingMore;
    private boolean booksFinish;
    private boolean ignoredFirstTime;
    private int start;
    private int itemsPerPage;
    private String searchKey;
    private String userID;

    private SearchView searchView;
    private TextView tvCartCounter;
    private int mCartCounter;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_books);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchKey = intent.getStringExtra(SearchManager.QUERY);
        }

        searchKey = intent.getExtras().getString("searchKey");

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User isn't logged in. Take him to login activity
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        userID = session.getLogInInfo().getId();

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        listView = (GridViewWithHeaderAndFooter) findViewById(R.id.BooksListView);
        footerView = getLayoutInflater().inflate(R.layout.footer_loading, listView, false);
        listView.addFooterView(footerView);
        adapter = new BookListAdapter(getApplicationContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        loadingMore = false;
        booksFinish = false;
        start = 0;
        itemsPerPage = 9;
        ignoredFirstTime = false;
        mCartCounter = 0;
        tvCartCounter = null;

        layout = (LinearLayout) findViewById(R.id.listViewLayout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int numColumns = listView.getNumColumns();
                listView.getRowHeight();
                int rowHeight = 215;
                int height = layout.getMeasuredHeight();
                int numRows = Math.round(height / rowHeight);
                itemsPerPage = numColumns * (numRows + 1);
                loadBooks();
            }
        });
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

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQuery(searchKey, false);
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                adapter.getViewModels().clear();
                adapter.notifyDataSetChanged();
                searchKey = query;
                start = 0;
                if (booksFinish) {
                    footerView = getLayoutInflater().inflate(R.layout.footer_loading, listView, false);
                    listView.addFooterView(footerView);
                }
                booksFinish = false;
                loadingMore = false;
                ignoredFirstTime = false;
                loadBooks();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final View menuCart = MenuItemCompat.getActionView(menu.findItem(R.id.action_cart));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
        String bookID = adapter.getItem(position).getId();
        intent.putExtra("bookID", bookID);
        startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //what is the bottom item that is visible
        int lastInScreen = firstVisibleItem + visibleItemCount;
        //is the bottom item visible & not loading more already ? Load more !
        if ((lastInScreen == totalItemCount) && !(loadingMore) && !(booksFinish) && ignoredFirstTime) {
            loadBooks();
        }
    }

    private void loadBooks() {
        Runnable loadBooks = new Runnable() {
            @Override
            public void run() {
                //Set flag so we cant load new items 2 at the same time
                loadingMore = true;
                BookRequestModel bookRequestBody = new BookRequestModel();
                bookRequestBody.setStart(start);
                bookRequestBody.setNumItems(itemsPerPage);
                bookRequestBody.setSearchKey(searchKey);
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(AppConfig.URL_BOOK_PAGE).build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<BookResponseModel> callback = new Callback<BookResponseModel>() {
                    @Override
                    public void success(BookResponseModel bookResponseBody, Response response) {
                        //Done! now continue on the UI thread
                        updateBooks(bookResponseBody);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        BookResponseModel bookResponseBody = new BookResponseModel();
                        bookResponseBody.setError(true);
                        bookResponseBody.setErrorMsg(error.getMessage());
                        updateBooks(bookResponseBody);
                    }
                };
                WebServiceAPI.getBookResponse(bookRequestBody, callback);
            }
        };
        Thread thread = new Thread(null, loadBooks);
        thread.start();
    }

    private void updateBooks(final BookResponseModel bookResponseBody) {
        Runnable updateBooks = new Runnable() {
            @Override
            public void run() {
                if (!bookResponseBody.getError()) {
                    if (bookResponseBody.getBooks() != null && bookResponseBody.getBooks().size() > 0) {
                        for (int i = 0; i < bookResponseBody.getBooks().size(); i++) {
                            adapter.getViewModels().add(bookResponseBody.getBooks().get(i));
                        }
                        adapter.notifyDataSetChanged();
                        if (bookResponseBody.getFinish()) {
                            listView.removeFooterView(footerView);
                        }
                    } else {
                        listView.removeFooterView(footerView);
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), lastBookResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                    listView.removeFooterView(footerView);
                }
                booksFinish = bookResponseBody.getFinish();
                start += itemsPerPage;
                //Set flag so we can load new items
                loadingMore = false;
                ignoredFirstTime = true;
            }
        };
        runOnUiThread(updateBooks);
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
        runOnUiThread(loadCartCount);
    }

    private void updateCartCounter(final int cartCounter) {
        mCartCounter = cartCounter;
        if (this.tvCartCounter == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cartCounter == 0) {
                    tvCartCounter.setVisibility(View.INVISIBLE);
                } else {
                    tvCartCounter.setVisibility(View.VISIBLE);
                    tvCartCounter.setText(Integer.toString(cartCounter));
                }
            }
        });
    }
}
