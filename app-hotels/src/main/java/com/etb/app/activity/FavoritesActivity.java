package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.search.Type;
import com.easytobook.api.utils.RequestUtils;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.adapter.HotelViewHolder;
import com.etb.app.fragment.FavoritesCitiesFragment;
import com.etb.app.fragment.FavoritesListFragment;
import com.etb.app.fragment.HomeFragment;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.HotelListRequest;
import com.etb.app.widget.AppBar;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-06-14
 */
public class FavoritesActivity extends BaseActivity implements HotelViewHolder.Listener, HomeFragment.Listener {

    private static final String FRAGMENT_FAVORITES_CITIES = "favorites_cities";
    private static final String FRAGMENT_FAVORITES_LIST = "favorites_list";
    private static final String FRAGMENT_HOME = "home";
    @Bind(R.id.app_bar)
    AppBar mToolbar;


    public static Intent createIntent(Context context) {
        return new Intent(context, FavoritesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setHotelsRequest(App.provide(this).createHotelsRequest());
        getHotelsRequest().setNoDatesRequest();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, FavoritesCitiesFragment.newInstance(),
                        FRAGMENT_FAVORITES_CITIES)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dates:
                showHome();
                break;
            case R.id.delete_list:
                FavoritesListFragment favoritesListFragment = (FavoritesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_FAVORITES_LIST);
                if (favoritesListFragment != null) {
                    favoritesListFragment.deleteList();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showHome() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_overlay_container,
                        HomeFragment.newInstance(getHotelsRequest(), true, false),
                        FRAGMENT_HOME)
                .addToBackStack(null)
                .commit();
    }

    public void showFavoritesList(String city, String country, String count) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, FavoritesListFragment.newInstance(city, country, count, getHotelsRequest()),
                        FRAGMENT_FAVORITES_LIST)
                .commit();
        if (getHotelsRequest().isDatesRequest()) {
            setSubtitle(DateUtils.formatDateRange(this, getHotelsRequest().getDateRange().from.getTimeInMillis(), getHotelsRequest().getDateRange().to.getTimeInMillis(), 0));
        } else {
            setSubtitle(getString(R.string.no_dates_selected));
        }

    }

    public void setSubtitle(String titleId) {
        mToolbar.setSubtitle(titleId);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME) != null) {
            super.onBackPressed();
        } else if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_FAVORITES_LIST) != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, FavoritesCitiesFragment.newInstance(),
                            FRAGMENT_FAVORITES_CITIES)
                    .commit();
            setSubtitle("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_favorites);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hotel_details, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onHotelClick(Accommodation acc, int position) {
        if (getHotelsRequest().isDatesRequest()) {
            Accommodation.Rate firstRate = acc.getFirstRate();
            int rateId = firstRate == null ? 0 : firstRate.rateId;
            final HotelSnippet hotelSnippet = HotelSnippet.from(acc, rateId, position);
            showHotelDetails(hotelSnippet);
        } else {
            showHome();
        }
    }

    public void showHotelDetails(HotelSnippet hotelSnippet) {
        startActivity(HotelSummaryActivity.createIntent(hotelSnippet, getHotelsRequest(), this));
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return null;
    }

    @Override
    public void startSearch(Type locationType, DateRange dates, int persons, int rooms) {
        remove(getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME));

        FavoritesListFragment favoritesListFragment = (FavoritesListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_FAVORITES_LIST);
        RequestUtils.apply(getHotelsRequest(), dates, persons, rooms);
        if (favoritesListFragment != null) {
            favoritesListFragment.refresh(getHotelsRequest());
            setSubtitle(DateUtils.formatDateRange(this, getHotelsRequest().getDateRange().from.getTimeInMillis(), getHotelsRequest().getDateRange().to.getTimeInMillis(), 0));
        }
    }
}
