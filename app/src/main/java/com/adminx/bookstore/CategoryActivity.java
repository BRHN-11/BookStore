package com.adminx.bookstore;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
 * Created by admin-x on 6/28/15.
 */
public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private LinearLayout layout;
    private GridViewWithHeaderAndFooter listView;
    private View footerView;
    private BookListAdapter adapter;

    private String categoryID;
    private String userID;

    private Boolean loadingMore;
    private boolean booksFinish;
    private int start;
    private int itemsPerPage;
    private boolean ignoredFirstTime;


    private SearchView searchView;
    private TextView tvCartCounter = null;
    private int mCartCounter = 0;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_books);

        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            userID = session.getLogInInfo().getId();
        }

        categoryID = getIntent().getExtras().getString("categoryID");
        String categoryName = getIntent().getExtras().getString("categoryName");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(categoryName);
        }
        searchView = (SearchView) findViewById(R.id.action_search);

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
        itemsPerPage = 7;
        ignoredFirstTime = false;

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
        Runnable loadMoreListItems = new Runnable() {
            @Override
            public void run() {
                //Set flag so we cant load new items 2 at the same time
                loadingMore = true;

                BookRequestModel bookRequestBody = new BookRequestModel();
                bookRequestBody.setCategoryId(categoryID);
                bookRequestBody.setStart(start);
                bookRequestBody.setNumItems(itemsPerPage);
                RestAdapter restAdapter = new RestAdapter
                        .Builder()
                        .setEndpoint(AppConfig.URL_BOOK_PAGE)
                        .build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<BookResponseModel> callback = new Callback<BookResponseModel>() {
                    @Override
                    public void success(BookResponseModel bookResponseBody, Response response) {
                        if (!bookResponseBody.getError()) {
                            if (bookResponseBody.getBooks() != null && bookResponseBody.getBooks().size() > 0) {
                                //Done! now continue on the UI thread
                                updateBooks(bookResponseBody);
                            } else {
                                listView.removeFooterView(footerView);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), bookResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                            listView.removeFooterView(footerView);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        listView.removeFooterView(footerView);
                    }
                };
                WebServiceAPI.getBookResponse(bookRequestBody, callback);
            }
        };
        Thread thread = new Thread(null, loadMoreListItems);
        thread.start();

    }

    private void updateBooks(final BookResponseModel bookResponseBody) {
        //Since we cant update our UI from a thread this Runnable takes care of that!
        Runnable updateBooks = new Runnable() {
            @Override
            public void run() {
                //Loop throw the new items and add them to the adapter
                if (bookResponseBody.getBooks() != null && bookResponseBody.getBooks().size() > 0) {
                    for (int i = 0; i < bookResponseBody.getBooks().size(); i++)
                        adapter.getViewModels().add(bookResponseBody.getBooks().get(i));
                }
                adapter.notifyDataSetChanged();
                if (bookResponseBody.getFinish()) {
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
}
