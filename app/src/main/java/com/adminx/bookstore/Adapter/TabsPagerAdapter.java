package com.adminx.bookstore.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.adminx.bookstore.CategoriesFragment;
import com.adminx.bookstore.HomeFragment;
import com.adminx.bookstore.TopSellingFragment;
import com.adminx.bookstore.NewFragment;

/**
 * Created by admin-x on 6/14/15.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, 1, object);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Categories fragment
                return new CategoriesFragment();
            case 1:
                // Home fragment
                return new HomeFragment();
            case 2:
                // New fragment
                return new NewFragment();
            case 3:
                // Top Rated fragment
                return new TopSellingFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }

}