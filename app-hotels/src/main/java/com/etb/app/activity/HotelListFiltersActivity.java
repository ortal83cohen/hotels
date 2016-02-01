package com.etb.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.etb.app.R;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.fragment.HotelListFilterFragment;
import com.etb.app.model.HotelListFilter;
import com.etb.app.model.HotelListRequest;

/**
 * @author alex
 * @date 2015-06-22
 */
public class HotelListFiltersActivity extends BaseActivity implements HotelListFilterFragment.Listener {

    private static final String EXTRA_RATE_MIN = "rate_min";
    private static final String EXTRA_RATE_MAX = "rate_max";
    public static final String EXTRA_FILTER = "filter";

    public static Intent createIntent(Context context, HotelListRequest request, int rateMinPrice, int rateMaxPrice) {
        Intent hotelListFiltersIntent = new Intent(context, HotelListFiltersActivity.class);

        hotelListFiltersIntent.putExtra(EXTRA_RATE_MIN, rateMinPrice);
        hotelListFiltersIntent.putExtra(EXTRA_RATE_MAX, rateMaxPrice);
        hotelListFiltersIntent.putExtra(EXTRA_REQUEST, request);
        hotelListFiltersIntent.putExtra(EXTRA_FILTER, (HotelListFilter)request.getFilter());

        return hotelListFiltersIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHotelsRequest().setFilter((HotelListFilter)getIntent().getParcelableExtra(EXTRA_FILTER));
        setResult(RESULT_CANCELED);
        AnalyticsCalls.get().trackFilterPage();

    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_hotellist_filters);
    }

    @Override
    public void onFiltersApply() {
        HotelListRequest request = getHotelsRequest();

        Intent data = new Intent();
        data.putExtra(EXTRA_FILTER, (HotelListFilter)request.getFilter());

        setResult(RESULT_OK, data);
        finish();
    }

    public int getRateMaxPrice() {
        return getIntent().getIntExtra(EXTRA_RATE_MAX, 1250);
    }

    public int getRateMinPrice() {
        return getIntent().getIntExtra(EXTRA_RATE_MIN, 10);
    }
}
