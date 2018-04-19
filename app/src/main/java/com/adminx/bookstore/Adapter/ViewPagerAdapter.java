package com.adminx.bookstore.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by admin-x on 8/22/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment fragments[];
    private CharSequence titles[];
    private int numbOfTabs;


    public ViewPagerAdapter(FragmentManager fm, Fragment mFragments[], CharSequence mTitles[], int mNumbOfTabs) {
        super(fm);

        this.fragments = mFragments;
        this.titles = mTitles;
        this.numbOfTabs = mNumbOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }
}