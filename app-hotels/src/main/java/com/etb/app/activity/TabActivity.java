package com.etb.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.etb.app.R;
import com.etb.app.adapter.ViewPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-08-11
 */
public abstract class TabActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    public static final String EXTRA_TABID = "tabid";

    protected int mTabId;

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tablayout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mTabId = savedInstanceState.getInt(EXTRA_TABID);
        } else {
            mTabId = getIntent().getIntExtra(EXTRA_TABID, 0);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mTabLayout);
        onCreateTabFragments(adapter, savedInstanceState);
        if (adapter.getCount() < 2) {
            mTabLayout.setVisibility(View.GONE);
            setTitle(adapter.getPageTitle(0));
        }
        mTabLayout.setOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new PageChangeListener(mTabLayout, this));
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mTabId);

    }

    protected abstract void onCreateTabFragments(ViewPagerAdapter adapter, Bundle savedInstanceState);

    public void setTabId(int tabId) {
        mTabId = tabId;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        mTabId = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_TABID, mTabId);
        super.onSaveInstanceState(outState);
    }

    static class PageChangeListener extends TabLayout.TabLayoutOnPageChangeListener {

        private final TabActivity mActivity;

        public PageChangeListener(TabLayout tabLayout, TabActivity activity) {
            super(tabLayout);
            mActivity = activity;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            mActivity.setTabId(position);
        }
    }
}
