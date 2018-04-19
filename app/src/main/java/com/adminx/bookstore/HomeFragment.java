package com.adminx.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.Adapter.BookGridAdapter;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.model.Book;
import com.adminx.bookstore.model.BookRequestModel;
import com.adminx.bookstore.model.Category;
import com.adminx.bookstore.model.CategoryResponseModel;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by admin-x on 6/24/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private final int categoriesCount = 6;
    private static View rootView;
    private ArrayList<TextView> catName;
    private ArrayList<View> catView;
    private ArrayList<Button> btnMore;
    private ArrayList<GridView> gridView;
    private ArrayList<BookGridAdapter> adapter;
    private ArrayList<Category> categories;

    public HomeFragment() {
        adapter = new ArrayList<>();
        gridView = new ArrayList<>();
        btnMore = new ArrayList<>();
        catView = new ArrayList<>();
        catName = new ArrayList<>();
        categories = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        initialize();
        loadCategories();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < categoriesCount; i++) {
            String str = "catName" + (i + 1);
            int ResID = getResources().getIdentifier(str, "id", getActivity().getPackageName());
            String strBtn = "btnMore" + (i + 1);
            int ResIDBtn = getResources().getIdentifier(strBtn, "id", getActivity().getPackageName());
            if (v.getId() == ResID || v.getId() == ResIDBtn) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("categoryID", categories.get(i).getId());
                intent.putExtra("categoryName", categories.get(i).getName());
                startActivity(intent);
            }
        }
    }

    private void initialize() {
        for (int i = 0; i < categoriesCount; i++) {
            String str = "gridView" + (i + 1);
            int ResID = getResources().getIdentifier(str, "id", getActivity().getPackageName());
            gridView.add((GridView) rootView.findViewById(ResID));
            adapter.add(new BookGridAdapter(getActivity()));
            gridView.get(i).setAdapter(adapter.get(i));
            gridView.get(i).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String bookID = ((Book) parent.getAdapter().getItem(position)).getId();
                    Intent intent = new Intent(getActivity(), BookActivity.class);
                    intent.putExtra("bookID", bookID);
                    startActivity(intent);
                }
            });

            str = "catName" + (i + 1);
            ResID = getResources().getIdentifier(str, "id", getActivity().getPackageName());
            catName.add((TextView) rootView.findViewById(ResID));
            catName.get(i).setOnClickListener(this);

            str = "btnMore" + (i + 1);
            ResID = getResources().getIdentifier(str, "id", getActivity().getPackageName());
            btnMore.add((Button) rootView.findViewById(ResID));
            btnMore.get(i).setOnClickListener(this);

            str = "catLayout" + (i + 1);
            ResID = getResources().getIdentifier(str, "id", getActivity().getPackageName());
            catView.add(rootView.findViewById(ResID));
            catView.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private void updateCategories(final CategoryResponseModel categoryResponseBody){
        Runnable updateCategories=new Runnable() {
            @Override
            public void run() {
                if (!categoryResponseBody.getError()) {
                    for (int i = 0; i < categoriesCount; i++) {
                        Category Category = categoryResponseBody.getCategories().get(i);
                        if (Category.getBooks().size() >= gridView.get(i).getNumColumns()) {
                            for (int j = 0; j < gridView.get(i).getNumColumns(); j++) {
                                adapter.get(i).getViewModels().add(Category.getBooks().get(j));
                            }
                            adapter.get(i).notifyDataSetChanged();
                            catName.get(i).setText(Category.getName());
                            catView.get(i).setVisibility(View.VISIBLE);
                        } else {
                            catView.get(i).setVisibility(View.GONE);
                        }
                    }
                    categories.addAll(categoryResponseBody.getCategories());

                } else {
                    Toast.makeText(getActivity(), categoryResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }
        };
        getActivity().runOnUiThread(updateCategories);

    }

    private void loadCategories(){
        Runnable loadCategories=new Runnable() {
            @Override
            public void run() {
                BookRequestModel bookRequestBody = new BookRequestModel();
                RestAdapter restAdapter = new RestAdapter
                        .Builder()
                        .setEndpoint(AppConfig.URL_HOME)
                        .build();
                WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);

                Callback<CategoryResponseModel> callback = new Callback<CategoryResponseModel>() {
                    @Override
                    public void success(CategoryResponseModel categoryResponseBody, Response response) {
                        updateCategories(categoryResponseBody);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CategoryResponseModel categoryResponseBody=new CategoryResponseModel();
                        categoryResponseBody.setError(true);
                        categoryResponseBody.setErrorMsg(error.getMessage());
                    }
                };
                WebServiceAPI.getHomeResponse(bookRequestBody, callback);
            }
        };
        Thread networkThread=new Thread(null, loadCategories);
        networkThread.start();
    }
}
