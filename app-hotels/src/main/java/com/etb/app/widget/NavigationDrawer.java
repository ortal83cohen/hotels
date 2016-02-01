package com.etb.app.widget;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.fragment.NavigationFragment;

/**
 * @author alex
 * @date 2015-04-19
 */
public class NavigationDrawer {

    public static final int NAV_RECENT_SEARCH = 0;
    public static final int NAV_FAVORITES = 1;
    public static final int NAV_BOOKINGS = 2;
    public static final int NAV_SCANNER = 8;
    public static final int NAV_PROFILE = 3;
    public static final int NAV_CREDIT_CARD = 4;
    public static final int NAV_SETTING = 5;
    public static final int NAV_LOGIN = 6;
    public static final int NAV_SIGN_UP = 7;


    public static final String CARDS_SCREEN_FRAGMENT = "cards_screen_fragment";
    private final BaseActivity mActivity;
    private int mCurrentTab = -1;

    public NavigationDrawer(BaseActivity baseActivity) {
        mActivity = baseActivity;
        //setup the NavigationDrawer
        NavigationFragment drawerFragment = (NavigationFragment)
                baseActivity.getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_navigation_drawer);
        if (drawerFragment != null) {
            drawerFragment.setUp(R.id.fragment_navigation_drawer,
                    (DrawerLayout) baseActivity.findViewById(R.id.drawer_layout),
                    baseActivity.getToolbar());
        }
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public void setCurrentTab(int tab) {
        mCurrentTab = tab;
    }

    public void change(int tab, Bundle extras) {

        mCurrentTab = tab;
        mActivity.onPageChange(tab, extras);

    }


}
