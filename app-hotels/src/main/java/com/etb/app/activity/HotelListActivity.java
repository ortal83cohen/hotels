package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.SprType;
import com.easytobook.api.model.search.Type;
import com.easytobook.api.model.search.ViewPortType;
import com.easytobook.api.utils.RequestUtils;
import com.etb.app.App;
import com.etb.app.HotelsApplication;
import com.etb.app.R;
import com.etb.app.adapter.HotelViewHolder;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.GoogleAnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.events.Events;
import com.etb.app.events.SearchRequestEvent;
import com.etb.app.events.SearchResultsEvent;
import com.etb.app.fragment.HomeFragment;
import com.etb.app.fragment.HotelMapSummaryFragment;
import com.etb.app.fragment.HotelsListFragment;
import com.etb.app.fragment.HotelsMapFragment;
import com.etb.app.fragment.ResultsSortFragment;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.map.PoiMarker;
import com.etb.app.map.ResultsMap;
import com.etb.app.model.HotelListFilter;
import com.etb.app.model.HotelListRequest;
import com.etb.app.utils.AppLog;
import com.etb.app.widget.AppBar;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-19
 */
public class HotelListActivity extends BaseActivity implements OnMapReadyCallback, ResultsMap.Listener, HotelViewHolder.Listener, HotelsListFragment.Listener, FragmentManager.OnBackStackChangedListener, HomeFragment.Listener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String FRAGMENT_HOME = "fragment_datepicker";
    private static final String FRAGMENT_RESULTSLIST = "fragment_listview";
    private static final String FRAGMENT_MAP = "menu_map";
    private static final String FRAGMENT_SORT = "fragment_sort";
    private static final String FRAGMENT_HOTEL_SUMMARY = "fragment_hotel_summary";
    private static final int ACTIVITY_REQUEST_FILTERS = 1;
    private static final int ACTIVITY_REQUEST_POIS_FILTERS = 2;
    protected GoogleApiClient mGoogleApiClient;
    @Bind(R.id.app_bar)
    AppBar mToolbar;
    @Bind(R.id.action_button)
    FloatingActionButton mButtonFilter;
    @Bind(R.id.poi_filter)
    FloatingActionButton mButtonPoisFilter;
    @Bind(R.id.refresh_hotels)
    Button mRefreshHotels;
    @Bind(R.id.loader_image)
    ImageView mLoaderImage;
    private ResultsMap mMap;

    private int mRateMinPrice = 10;
    private int mRateMaxPrice = 1250;
    private HashMap<Integer, Integer> mPoisTypes;
    private boolean[] mPoisFilter;

    public static Intent createIntent(HotelListRequest request, Context context) {
        Intent intent = new Intent(context, HotelListActivity.class);
        intent.putExtra(EXTRA_REQUEST, request);
        return intent;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Events.register(this);
    }

    @Override
    protected void onPause() {
        Events.unregister(this);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle(getHotelsRequest());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        configLoaderImage();

        mPoisFilter = createFilters(PoiMarker.TYPES);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilters();
            }
        });
        mButtonPoisFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoisFilters();
            }
        });
        mRefreshHotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.updateRequest();
                }
                hideRefreshHotelsButton();
                showLoaderImage();
                refreshMap();
            }
        });

        if (savedInstanceState == null) {
            showList();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        HotelsMapFragment fragmentMap = (HotelsMapFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_MAP);
        if (fragmentMap != null) {
            fragmentMap.getMapAsync(this);
        }
        // For the initial search track here, all others will be tracked with SearchRequestEvent
        AnalyticsCalls.get().trackSearchResults(getHotelsRequest());
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_hotel_list);
    }

    public void setTitle(SearchRequest hotelsRequest) {
        mToolbar.setLocation(hotelsRequest);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                R.string.could_not_connect_to_google + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mToolbar.setSubtitle("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = new ResultsMap(googleMap, this, this);
        setTitle(getHotelsRequest());
    }

    @Override
    public void onLandmarksTypesChange(HashMap<Integer, Integer> types) {
        if (types == null || types.size() == 0) {
            mButtonPoisFilter.setVisibility(View.GONE);
        } else {
            if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_MAP) != null) {
                mButtonPoisFilter.setVisibility(View.VISIBLE);
            }
        }
        mPoisTypes = types;
    }

    @Override
    public void onHotelMarkerClick(Accommodation acc) {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_overlay_container);

        if (fragment != null && fragment instanceof HotelMapSummaryFragment && fragment.isVisible()) {
            getSupportFragmentManager().popBackStack();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_overlay_container, HotelMapSummaryFragment.newInstance(acc),
                        FRAGMENT_HOTEL_SUMMARY)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onHotelClick(Accommodation acc, int position) {
        if (acc.getFirstRate() == null) {
            Toast.makeText(getBaseContext(), R.string.hotel_not_available, Toast.LENGTH_LONG).show();
            AppLog.e("HotelListActivity: hotel_not_available-" + acc.id);
        } else {
            final HotelSnippet hotelSnippet = HotelSnippet.from(acc, acc.getFirstRate().rateId, position);
            showHotelDetails(hotelSnippet);
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent startMain = new Intent(this, HomeActivity.class);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        HotelMapSummaryFragment hotelMapSummaryFragment = (HotelMapSummaryFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOTEL_SUMMARY);
        if (hotelMapSummaryFragment == null) {
            hidePoiFilterButton();
            hideRefreshHotelsButton();
        }
        super.onBackPressed();
    }

    @Override
    public void removeHotelSummaryFragment() {
        HotelMapSummaryFragment hotelMapSummaryFragment = (HotelMapSummaryFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOTEL_SUMMARY);
        if (hotelMapSummaryFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(hotelMapSummaryFragment)
                    .commit();
        }
    }


    public void showHotelDetails(HotelSnippet hotelSnippet) {

        startActivity(HotelSummaryActivity.createIntent(hotelSnippet, getHotelsRequest(), this));

    }

    public void refreshList() {
        HotelsListFragment listFragment = (HotelsListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_RESULTSLIST);
        if (listFragment != null) {
            listFragment.refresh();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dates:
                showHome();
                break;
            case R.id.menu_list:
                showList();
                break;
            case R.id.menu_map:
                showMap();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showFilters() {
        startActivityForResult(HotelListFiltersActivity.createIntent(this, getHotelsRequest(), mRateMinPrice, mRateMaxPrice), ACTIVITY_REQUEST_FILTERS);
    }

    public void showPoisFilters() {
        startActivityForResult(PoisFiltersActivity.createIntent(this, getHotelsRequest(), mPoisTypes, mPoisFilter), ACTIVITY_REQUEST_POIS_FILTERS);
    }

    private boolean[] createFilters(int[] values) {
        boolean[] selected = new boolean[values.length];

        for (int i = 0; i < values.length; i++) {
            selected[i] = false;
        }

        return selected;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_REQUEST_POIS_FILTERS) {
            if (resultCode == RESULT_OK) {
                mPoisFilter = data.getBooleanArrayExtra("result");
            }
        }
        if (requestCode == ACTIVITY_REQUEST_FILTERS) {
            if (resultCode == RESULT_OK) {
                getHotelsRequest().setFilter((HotelListFilter) data.getParcelableExtra(HotelListFiltersActivity.EXTRA_FILTER));
            }
        }
        if (resultCode == RESULT_OK) {
            refreshResults(mPoisFilter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshResults(boolean[] result) {
        if (mMap != null) {
            mMap.setPoiFilters(result);
            mMap.refreshHotels();
        }
        refreshList();
    }

    public void showHome() {
        removeHotelSummaryFragment();
        FragmentManager fm = getSupportFragmentManager();
        HotelListRequest request = getHotelsRequest();
        fm.beginTransaction()
                .replace(R.id.fragment_overlay_container,
                        HomeFragment.newInstance(request, true, true),
                        FRAGMENT_HOME)
                .addToBackStack(null)
                .commit();
        hidePoiFilterButton();
    }

    public void showSort() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_overlay_container, ResultsSortFragment.newInstance(),
                        FRAGMENT_SORT)
                .addToBackStack("sort")
                .commit();
        hidePoiFilterButton();
    }

    private void showMap() {
        HotelsMapFragment mapFragment = (HotelsMapFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_MAP);
        if (mapFragment == null) {
            mapFragment = new HotelsMapFragment();
            mapFragment.getMapAsync(this);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment, FRAGMENT_MAP)
                .addToBackStack(null)
                .commit();
        showLandmarksButtonFilter();
    }

    public void showList() {
        removeHotelSummaryFragment();
        hidePoiFilterButton();
        hideRefreshHotelsButton();

        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_RESULTSLIST) != null) {
            // Got to initial state of the stack and resume list from stack instead of adding a new one
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HotelsListFragment.newInstance(), FRAGMENT_RESULTSLIST)
                    .commit();
        }
    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void startSearch(Type locationType, DateRange dates, int persons, int rooms) {
        remove(getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME));
        getHotelsRequest().setType(locationType);
        getHotelsRequest().removeFilter();
        RequestUtils.apply(getHotelsRequest(), dates, persons, rooms);
        App.provide(this).updateLastSeatchRequest(getHotelsRequest());
        if (mMap != null) {
            mMap.refreshHotels();
            mMap.setPoiFilters(mPoisFilter);
            if (locationType instanceof ViewPortType) {
                mMap.moveCamera(((ViewPortType) locationType).getNortheastLat(), ((ViewPortType) locationType).getNortheastLon(), ((ViewPortType) locationType).getSouthwestLat(), ((ViewPortType) locationType).getSouthwestLon());
            } else if (locationType instanceof SprType) {
                mMap.moveCamera(((SprType) locationType).getLatitude(), ((SprType) locationType).getLongitude());
            }
        }
        refreshList();
        setTitle(getHotelsRequest());
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Subscribe
    public void onSearchRequestEvent(SearchRequestEvent event) {
        if (event.getOffset() == 0) { // Only for the fresh results
            AnalyticsCalls.get().trackSearchResults(event.getSearchRequest());
        }
    }

    @Subscribe
    public void onSearchResultsEvent(SearchResultsEvent event) {
        if (event.getCount() == 0) {
            hideButtonFilter();
        } else {
            showButtonFilter();
        }
    }

    public void hideButtonFilter() {
        mButtonFilter.setVisibility(View.GONE);
    }

    public void showButtonFilter() {
        mButtonFilter.setVisibility(View.VISIBLE);
    }

    public void hidePoiFilterButton() {
        mButtonPoisFilter.setVisibility(View.GONE);
    }

    public void hideRefreshHotelsButton() {
        mRefreshHotels.setVisibility(View.GONE);
    }

    public void showRefreshHotelsButton() {
        mRefreshHotels.setVisibility(View.VISIBLE);
    }

    private void configLoaderImage() {
        mLoaderImage.setBackgroundResource(R.drawable.logo_animation);
        AnimationDrawable logoAnimation = (AnimationDrawable) mLoaderImage.getBackground();
        logoAnimation.start();
        showLoaderImage();
    }

    public void hideLoaderImage() {
        mLoaderImage.setVisibility(View.GONE);
    }

    public void showLoaderImage() {
        mLoaderImage.setVisibility(View.VISIBLE);
    }

    public void refreshMap() {
        mMap.refreshHotels();
        setTitle(getHotelsRequest());
    }

    public void showLandmarksButtonFilter() {
        mButtonPoisFilter.setVisibility(View.VISIBLE);
    }


    public void setRateMinPrice(int mRateMinPrice) {
        this.mRateMinPrice = mRateMinPrice;
    }

    public void setRateMaxPrice(int mRateMaxPrice) {
        this.mRateMaxPrice = mRateMaxPrice;
    }

    @Override
    public void onEditLocationClick() {
        showHome();
    }


}
