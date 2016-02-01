package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.fragment.PriceBreakdownFragment;
import com.etb.app.fragment.RoomListFragment;
import com.etb.app.model.HotelListRequest;
import com.etb.app.utils.PriceRender;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-19
 */
public class RoomListActivity extends BaseActivity implements PriceBreakdownFragment.Listener {

    private static final String FRAGMENT_ROOM_LIST = "menu_room_list";
    private static final String HOTEL_ID = "hotel_id";
    private static final String FRAGMENT_PRICE_BREAKDOWN = "price_breakdown";
    private static final String EXTRA_REQUEST = "request";

    private int mHotelId;

    public static Intent createIntent(int hotelId, HotelListRequest request, Context context) {
        Intent intent = new Intent(context, RoomListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_REQUEST, request);
        bundle.putInt(HOTEL_ID, hotelId);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mHotelId = getIntent().getIntExtra(HOTEL_ID, 0);
        //set the Toolbar as ActionBar

        if (savedInstanceState == null) {
            showList(false);
        }

        AnalyticsCalls.get().trackHotelRooms(getHotelsRequest());
    }


    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_room_list);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle("");
    }


    public void showList(boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, RoomListFragment.newInstance(mHotelId, getHotelsRequest()), FRAGMENT_ROOM_LIST);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onPriceBreakdownClick(Accommodation.Rate rate) {
        SearchRequest request = getHotelsRequest();
        String currencyCode = App.provide(this).getUserPrefs().getCurrencyCode();
        PriceRender priceRender = getPriceRender();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.raise, R.anim.shrink)
                .replace(R.id.fragment_overlay_container,
                        PriceBreakdownFragment.newInstance(request.getNumberOfRooms(), rate, currencyCode, priceRender),
                        FRAGMENT_PRICE_BREAKDOWN)
                .commit();
    }

}
