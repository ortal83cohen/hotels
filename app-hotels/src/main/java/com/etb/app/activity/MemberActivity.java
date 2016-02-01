package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.etb.app.R;
import com.etb.app.adapter.ViewPagerAdapter;
import com.etb.app.fragment.AddMoreBookingFragment;
import com.etb.app.fragment.MemberBookingsListFragment;
import com.etb.app.fragment.MemberProfileEditFragment;
import com.etb.app.widget.NavigationDrawer;

/**
 * @author alex
 * @date 2015-08-16
 */
public class MemberActivity extends TabActivity {

    public static final String FRAGMENT_EDIT = "fragment_edit";
    private static final String FRAGMENT_ADD_MORE_BOOKING = "fragment_add";
    private ViewPagerAdapter mAdapter;

    public static Intent createIntent(int navTab, Context context) {
        int tabId = 0;
        if (navTab == NavigationDrawer.NAV_BOOKINGS) {
            tabId = 1;
        } else if (navTab == NavigationDrawer.NAV_CREDIT_CARD) {
            tabId = 2;
        }
        Intent memberIntent = new Intent(context, MemberActivity.class);
        memberIntent.putExtra(EXTRA_TABID, tabId);
        return memberIntent;
    }

    @Override
    protected boolean requiresRequest() {
        return false;
    }

    @Override
    protected void onCreateTabFragments(ViewPagerAdapter adapter, Bundle savedInstanceState) {
        mAdapter = adapter;
//        adapter.addFragment(MemberProfileFragment.newInstance(), "PROFILE", "fragment_profile");
        adapter.addFragment(MemberBookingsListFragment.newInstance(), "Bookings", "fragment_bookings");
//        adapter.addFragment(MemberProfileFragment.newInstance(), "CREDIT CARDS", "fragment_credit_cards");
    }

    public void notifyDataSetChanged() {
        ((MemberBookingsListFragment) mAdapter.getItem(0)).notifyDataSetChanged();
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_member);
    }

    public void showProfileEdit() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.raise, R.anim.shrink)
                .replace(R.id.fragment_overlay_container,
                        MemberProfileEditFragment.newInstance(),
                        FRAGMENT_EDIT)
                .commit();
    }

    public void showAddMoreBookingEdit() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.raise, R.anim.shrink)
                .replace(R.id.fragment_overlay_container,
                        AddMoreBookingFragment.newInstance(),
                        FRAGMENT_ADD_MORE_BOOKING).addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment editFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_EDIT);
        if (editFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(editFragment).commit();
            return;
        }
        super.onBackPressed();
    }
}
