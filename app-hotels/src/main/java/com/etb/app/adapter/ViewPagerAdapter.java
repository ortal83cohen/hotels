package com.etb.app.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.etb.app.utils.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTags = new ArrayList<>();
    private final TabLayout mTabLayout;

    public ViewPagerAdapter(FragmentManager manager, TabLayout tabLayout) {
        super(manager);
        mTabLayout = tabLayout;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public String getTag(int position) {
        return mTags.get(position);
    }

    public void addFragment(Fragment fragment, String title, String tag) {
        mFragmentList.add(fragment);
        mTabLayout.addTab(mTabLayout.newTab().setText(title));
        mTags.add(tag);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabLayout.getTabAt(position).getText();
    }
}