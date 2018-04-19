package com.adminx.bookstore;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.Adapter.BookListAdapter;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.model.BookRequestModel;
import com.adminx.bookstore.model.BookResponseModel;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin-x on 6/11/15.
 */
public class NewFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private LinearLayout layout;
    private GridViewWithHeaderAndFooter listView;
    private View footerView;
    private BookListAdapter adapter;
    private Boolean loadingMore;
    private boolean booksFinish;
    private boolean ignoredFirstTime;
    private int start;
    private int itemsPerPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listview_books, container, false);
        listView = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.BooksListView);
        footerView = inflater.inflate(R.layout.footer_loading, container, false);
        listView.addFooterView(footerView);

        adapter = new BookListAdapter(getActivity());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        loadingMore = false;
        booksFinish = false;
        start = 0;
        itemsPerPage = 9;
        ignoredFirstTime = false;

        layout = (LinearLayout) rootView.findViewById(R.id.listViewLayout);
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

        return rootView;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), BookActivity.class);
        String bookID = adapter.getItem(position).getId();
        intent.putExtra("bookID", bookID);
        startActivity(intent);
    }

    private void loadBooks() {
        Runnable loadBooks = new Runnable() {
            @Override
            public void run() {
                //Set flag so we cant load new items 2 at the same time
                loadingMore = true;
                BookRequestModel bookRequestBody = new BookRequestModel();
                bookRequestBody.setCategoryId("new");
                bookRequestBody.setStart(start);
                itemsPerPage = 7;
                bookRequestBody.setNumItems(itemsPerPage);
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
                    Toast.makeText(getActivity(), bookResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                    listView.removeFooterView(footerView);
                }
                booksFinish = bookResponseBody.getFinish();
                start += itemsPerPage;
                //Set flag so we can load new items
                loadingMore = false;
                ignoredFirstTime = true;
            }
        };
        getActivity().runOnUiThread(updateBooks);
    }
}