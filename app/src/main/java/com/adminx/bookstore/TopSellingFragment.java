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
import com.adminx.bookstore.Adapter.BookGridAdapter;
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
public class TopSellingFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private GridViewWithHeaderAndFooter gridView;
    private View footerView;
    private BookGridAdapter adapter;
    private Boolean loadingMore;
    private Boolean ignoredFirstTime;
    private boolean booksFinish;
    private int start;
    private int itemsPerPage;
    private LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gridview_books, container, false);

        gridView = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.gridView);
        footerView = inflater.inflate(R.layout.footer_loading, container, false);
        gridView.addFooterView(footerView);

        adapter = new BookGridAdapter(getActivity());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(this);

        booksFinish = false;
        loadingMore = false;
        start = 0;
        ignoredFirstTime = false;
        itemsPerPage=15;

        layout = (LinearLayout) rootView.findViewById(R.id.gridViewLayout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int numColumns = gridView.getNumColumns();
                int rowHeight = 195;
                int height = layout.getMeasuredHeight();
                int numRows = Math.round(height / rowHeight);
                itemsPerPage = numColumns * (numRows + 1);

                loadBooks();
            }
        });
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), BookActivity.class);
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
                bookRequestBody.setCategoryId("TopSelling");
                bookRequestBody.setStart(start);
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
        //Since we cant update our UI from a thread this Runnable takes care of that!
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
                            gridView.removeFooterView(footerView);
                        }
                    } else {
                        gridView.removeFooterView(footerView);
                    }
                } else {
                    Toast.makeText(getActivity(), bookResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                    gridView.removeFooterView(footerView);
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