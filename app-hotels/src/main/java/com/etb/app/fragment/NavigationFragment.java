package com.etb.app.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.events.Events;
import com.etb.app.events.UserLogOutEvent;
import com.etb.app.events.UserLoginEvent;
import com.etb.app.events.UserProfileUpdateEvent;
import com.etb.app.member.MemberStorage;
import com.etb.app.member.model.User;
import com.etb.app.widget.NavigationDrawer;
import com.squareup.otto.Subscribe;


public class NavigationFragment extends Fragment implements View.OnClickListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Events.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Events.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        MemberStorage memberStorage = App.provide(getActivity()).memberStorage();
        User user = memberStorage.loadUser();

        updateViews(user, inflater);

        return mView;
    }

    @Subscribe
    public void onUserLogIn(UserLoginEvent event) {
        updateViews(event.getUser(), LayoutInflater.from(getActivity()));
    }

    @Subscribe
    public void onUserLogOut(UserLogOutEvent event) {
        updateViews(null, LayoutInflater.from(getActivity()));
    }

    @Subscribe
    public void onUserProfileUpdate(UserProfileUpdateEvent event) {
        updateViews(event.getUser(), LayoutInflater.from(getActivity()));
    }

    private void updateViews(User user, LayoutInflater inflater) {
//        boolean loggedIn = user != null;

        setupItem(R.id.nav_recent_searches, NavigationDrawer.NAV_RECENT_SEARCH, true);
        setupItem(R.id.nav_favorites, NavigationDrawer.NAV_FAVORITES, true);
        setupItem(R.id.nav_bookings, NavigationDrawer.NAV_BOOKINGS, true);
        setupItem(R.id.nav_code_scanner, NavigationDrawer.NAV_SCANNER, true);
//        setupItem(R.id.nav_profile, NavigationDrawer.NAV_PROFILE, loggedIn);
//        setupItem(R.id.nav_creditcards, NavigationDrawer.NAV_CREDIT_CARD, loggedIn);
        setupItem(R.id.nav_settings, NavigationDrawer.NAV_SETTING, true);
        FrameLayout headerView = (FrameLayout) mView.findViewById(R.id.nav_header_view);
        headerView.removeAllViews();
        View header = inflater.inflate(R.layout.nav_header, headerView, false);

//        if (user == null) {
//            header = inflater.inflate(R.layout.nav_header_logout, headerView, false);
//            mView.findViewById(R.id.nav_divider).setVisibility(View.GONE);
//            Button loginButton = (Button) header.findViewById(R.id.nav_login_button);
//            Button signUpButton = (Button) header.findViewById(R.id.nav_sign_up_button);
//            signUpButton.setOnClickListener(this);
//            loginButton.setOnClickListener(this);
//        } else {
//            header = inflater.inflate(R.layout.nav_header_login, headerView, false);
//            TextView nameFirst = (TextView) header.findViewById(R.id.profile_name);
//            TextView nameLast = (TextView) header.findViewById(R.id.profile_surname);
//
//            if(true){//if (TextUtils.isEmpty(user.profile.firstName) && TextUtils.isEmpty(user.profile.lastName)) {
//                nameFirst.setText("Hello,");
//                nameLast.setText("Traveller");
//            } else {
//                nameFirst.setText(user.profile.firstName);
//                nameLast.setText(user.profile.lastName);
//            }
//
////            if (!TextUtils.isEmpty(user.profile.imageUrl)) {
//            if(false){
//                ImageView image = (ImageView) header.findViewById(R.id.profile_image);
//                Uri uri = Uri.parse(user.profile.imageUrl);
//
//                Picasso.with(getActivity()).load(uri)
//                        .resize(image.getMeasuredWidth(), image.getMeasuredHeight())
//                        .centerCrop()
//                        .into(image);
//            }
        mView.findViewById(R.id.nav_divider).setVisibility(View.VISIBLE);
//        }
        headerView.addView(header);
    }


    private void setupItem(@IdRes int resId, final int tab, boolean visible) {
        View item = mView.findViewById(resId);
        item.setTag(tab);
        if (visible) {
            item.setVisibility(View.VISIBLE);
            item.setOnClickListener(this);
        } else {
            item.setVisibility(View.GONE);
            item.setOnClickListener(null);
        }
    }

    public void selectItem(int position) {
        ((BaseActivity) getActivity()).getNavigationDrawer().change(position, null);

    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.nav_sign_up_button:
                selectItem(NavigationDrawer.NAV_SIGN_UP);
                break;
            case R.id.nav_login_button:
                selectItem(NavigationDrawer.NAV_LOGIN);
                break;
            case R.id.nav_recent_searches:
                selectItem((Integer) v.getTag());
                break;
            case R.id.nav_code_scanner:
                selectItem((Integer) v.getTag());
                break;
            default:
                selectItem((Integer) v.getTag());
                closeDrawer();
        }

    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
