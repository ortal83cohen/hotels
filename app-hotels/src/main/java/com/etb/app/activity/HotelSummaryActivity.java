package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.HotelRequest;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.Type;
import com.easytobook.api.utils.RequestUtils;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.fragment.HomeFragment;
import com.etb.app.fragment.HotelSummaryFragment;
import com.etb.app.fragment.PriceBreakdownFragment;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.Location;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.PriceRender;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-19
 */
public class HotelSummaryActivity extends BaseActivity implements PriceBreakdownFragment.Listener, HomeFragment.Listener {
    private static final String FRAGMENT_HOME = "fragment_datepicker";
    public static final String FRAGMENT_DATEPICKER = "fragment_datepicker";
    public static final String FRAGMENT_HOTEL_DETAILS = "fragment_hotel_details";
    private static final String EXTRA_SNIPPET = "snippet";
    private static final String EXTRA_NO_DATES = "no_dates";
    private static final String FRAGMENT_PRICE_BREAKDOWN = "price_breakdown";
    private int mRateId;
    private HotelSnippet mHotelSnippet;
    private StreetViewPanorama mStreetView;
    private android.app.Fragment mStreetViewFragment;
    private HotelSnippet mHotelSnippetDetails;
    private boolean mIsNoDates;


    public static Intent createIntent(HotelSnippet hotelSnippet, HotelListRequest request, Context context) {
        Intent intent = new Intent(context, HotelSummaryActivity.class);

        intent.putExtra(EXTRA_SNIPPET, hotelSnippet);
        intent.putExtra(EXTRA_REQUEST, request);

        return intent;
    }

    public static Intent createIntent(HotelSnippet hotelSnippet, HotelListRequest request, boolean noDates, Context context) {
        Intent intent = createIntent(hotelSnippet,request,context);
        intent.putExtra(EXTRA_NO_DATES, noDates);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        //set the Toolbar as ActionBar
        setTitle("");
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        if (savedInstanceState != null) {
            mHotelSnippet = savedInstanceState.getParcelable(EXTRA_SNIPPET);
            mIsNoDates = savedInstanceState.getBoolean(EXTRA_NO_DATES);
        } else {
            mHotelSnippet = getIntent().getParcelableExtra(EXTRA_SNIPPET);
            mIsNoDates = getIntent().getBooleanExtra(EXTRA_NO_DATES, false);
        }
        mRateId = mHotelSnippet == null ? 0 : mHotelSnippet.getSelectedRateId();

        if (mIsNoDates) {
            getHotelsRequest().setNoDatesRequest();
        }

        createStreetView();

        FragmentManager fm = getSupportFragmentManager();
        HotelSummaryFragment hotelSummaryFragment = (HotelSummaryFragment) fm.findFragmentByTag(FRAGMENT_HOTEL_DETAILS);
        if (hotelSummaryFragment == null) {
            hotelSummaryFragment = HotelSummaryFragment.newInstance(mRateId, mIsNoDates,  getHotelsRequest(), mHotelSnippet);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.raise, R.anim.shrink)
                    .replace(R.id.fragment_container,
                            hotelSummaryFragment,
                            FRAGMENT_HOTEL_DETAILS)
                    .commit();
        }
        mStreetViewFragment = getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        getFragmentManager().beginTransaction().hide(mStreetViewFragment).commit();

    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_hotelsummary);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_SNIPPET, mHotelSnippet);
        outState.putBoolean(EXTRA_NO_DATES, mIsNoDates);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dates:
                showDatePicker();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showRoomsList(int hotelId) {
        startActivity(RoomListActivity.createIntent(hotelId,  getHotelsRequest(), this));

    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent startMain = new Intent(this, HomeActivity.class);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finish();
            return;
        }
        if (!mStreetViewFragment.isHidden()) {
            getFragmentManager().beginTransaction().hide(mStreetViewFragment).commit();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            HotelSummaryFragment fragmentDetails = (HotelSummaryFragment) fm.findFragmentByTag(FRAGMENT_HOTEL_DETAILS);
            if (fragmentDetails != null) {
                if (fragmentDetails.isImageExpanded()) {
                    fragmentDetails.collapseImage();
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    private void createStreetView() {
        mStreetView = ((StreetViewPanoramaFragment)
                getFragmentManager().findFragmentById(R.id.streetviewpanorama))
                .getStreetViewPanorama();
    }

    public void showStreetView() {
        if (mStreetViewFragment.isHidden()) {
            getFragmentManager().beginTransaction().show(mStreetViewFragment).commit();
            mStreetView.setPosition(new LatLng(mHotelSnippet.getLocation().lat, mHotelSnippet.getLocation().lon));
        } else {
            getFragmentManager().beginTransaction().hide(mStreetViewFragment).commit();
        }
    }

    @Override
    public void onPriceBreakdownClick(Accommodation.Rate rate) {
        SearchRequest request = getHotelsRequest();
        PriceRender priceRender = getPriceRender();
        String currencyCode = App.provide(this).getUserPrefs().getCurrencyCode();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.raise, R.anim.shrink)
                .replace(R.id.fragment_overlay_container,
                        PriceBreakdownFragment.newInstance(request.getNumberOfRooms(), rate, currencyCode, priceRender),
                        FRAGMENT_PRICE_BREAKDOWN)
                .commit();
    }

    public void onDetailsResponse(HotelSnippet hotelDetailsSnippet) {
        mHotelSnippetDetails = hotelDetailsSnippet;
    }

    public void showDatePicker() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_overlay_container,
                        HomeFragment.newInstance(getHotelsRequest(), true, false),
                        FRAGMENT_HOME)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return null;
    }

    @Override
    public void startSearch(Type locationType, DateRange dates, int persons, int rooms) {
        remove(getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME));

        RequestUtils.apply(getHotelsRequest(), dates, persons, rooms);
        HotelSummaryFragment hotelSummaryFragment = (HotelSummaryFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOTEL_DETAILS);
        if (hotelSummaryFragment != null) {
            hotelSummaryFragment.checkAvailability(getHotelsRequest());
        }
    }

    public void showSearchResults(Location location, HotelRequest request) {
        HotelListRequest searchRequest = getHotelsRequest();
        searchRequest.setType(location);
        RequestUtils.apply(searchRequest, request.getDateRange(), request.getNumberOfPersons(), request.getNumberOfRooms());

        Intent myIntent = HotelListActivity.createIntent(searchRequest, this);
        startActivity(myIntent);
    }
}
