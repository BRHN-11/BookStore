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
import com.adminx.bookstore.Adapter.CategoryListAdapter;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.model.BookRequestModel;
import com.adminx.bookstore.model.CategoryResponseModel;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by admin-x on 6/28/15.
 */
public class CategoriesFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private GridViewWithHeaderAndFooter listView;
    private View footerView;
    private LinearLayout layout;
    private CategoryListAdapter adapter;
    private Boolean loadingMore;
    private boolean CategoriesFinish;
    private boolean ignoredFirstTime;
    private int start;
    private int itemsPerPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listview_categories, container, false);

        listView = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.categoriesListView);
        footerView = inflater.inflate(R.layout.footer_loading, container, false);
        listView.addFooterView(footerView);

        adapter = new CategoryListAdapter(getActivity());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        itemsPerPage = 7;
        start = 0;
        ignoredFirstTime = false;
        CategoriesFinish = false;
        loadingMore = false;

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
                int rowHeight = 140;
                int height = layout.getMeasuredHeight();
                int numRows = Math.round(height / rowHeight);
                itemsPerPage = numColumns * (numRows + 1);

                loadCategories();
            }
        });


        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("categoryID", adapter.getItem(position).getId());
        intent.putExtra("categoryName", adapter.getItem(position).getName());
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
        if ((lastInScreen == totalItemCount) && !(loadingMore) && !(CategoriesFinish) && ignoredFirstTime) {
            loadCategories();
        }
    }

    private void loadCategories() {
        Runnable loadCategories = new Runnable() {
            @Override
            public void run() {
                //Set flag so we cant load new items 2 at the same time
                loadingMore = true;
                BookRequestModel bookRequestBody = new BookRequestModel();
                bookRequestBody.setStart(start);
                bookRequestBody.setNumItems(itemsPerPage);
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(AppConfig.URL_CATEGORY_PAGE).build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<CategoryResponseModel> callback = new Callback<CategoryResponseModel>() {
                    @Override
                    public void success(CategoryResponseModel categoryResponseBody, Response response) {
                        //Done! now continue on the UI thread
                        updateCategories(categoryResponseBody);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CategoryResponseModel categoryResponseBody = new CategoryResponseModel();
                        categoryResponseBody.setError(true);
                        categoryResponseBody.setErrorMsg(error.getMessage());
                        updateCategories(categoryResponseBody);
                    }
                };
                WebServiceAPI.getCategoryResponse(bookRequestBody, callback);
            }
        };
        Thread thread = new Thread(null, loadCategories);
        thread.start();

    }

    private void updateCategories(final CategoryResponseModel categoryResponseBody) {
        //Since we cant update our UI from a thread this Runnable takes care of that!
        Runnable updateCategory = new Runnable() {
            @Override
            public void run() {
                if (!categoryResponseBody.getError()) {
                    if (categoryResponseBody.getCategories() != null && categoryResponseBody.getCategories().size() > 0) {
                        for (int i = 0; i < categoryResponseBody.getCategories().size(); i++) {
                            adapter.getViewModels().add(categoryResponseBody.getCategories().get(i));
                        }
                        adapter.notifyDataSetChanged();
                        if (categoryResponseBody.getFinish()) {
                            listView.removeFooterView(footerView);
                        }
                    } else {
                        listView.removeFooterView(footerView);
                    }
                } else {
                    Toast.makeText(getActivity(), categoryResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                    listView.removeFooterView(footerView);
                }

                CategoriesFinish = categoryResponseBody.getFinish();
                start += itemsPerPage;
                //Set flag so we can load new items
                loadingMore = false;
                ignoredFirstTime = true;
            }
        };
        getActivity().runOnUiThread(updateCategory);

    }
}