package com.etb.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.etb.app.R;
import com.etb.app.fragment.PoisFilterFragment;
import com.etb.app.model.HotelListRequest;

import java.util.HashMap;

/**
 * @author alex
 * @date 2015-06-22
 */
public class PoisFiltersActivity extends BaseActivity implements PoisFilterFragment.Listener {

    private static final String EXTRA_TYPES = "types";
    private static final String EXTRA_FILTERS = "filters";

    public static Intent createIntent(Context context, HotelListRequest request, HashMap<Integer, Integer> types, boolean[] filers) {
        Intent hotelListFiltersIntent = new Intent(context, PoisFiltersActivity.class);

        hotelListFiltersIntent.putExtra(EXTRA_TYPES, types);
        hotelListFiltersIntent.putExtra(EXTRA_FILTERS, filers);
        hotelListFiltersIntent.putExtra(EXTRA_REQUEST, request);

        return hotelListFiltersIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_landmarks_filters);
    }

    @Override
    public void onFiltersApply(boolean[] selectedAccTypes) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", selectedAccTypes);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public HashMap<Integer, Integer> getTypes() {
        return (HashMap<Integer, Integer>) getIntent().getSerializableExtra(EXTRA_TYPES);
    }

    public boolean[] getFilters() {
        return getIntent().getBooleanArrayExtra(EXTRA_FILTERS);
    }

}
