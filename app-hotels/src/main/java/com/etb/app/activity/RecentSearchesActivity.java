package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.easytobook.api.model.DateRange;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.adapter.RecentSearchesAdapter;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.Location;
import com.etb.app.model.ViewPort;
import com.etb.app.provider.DbContract;
import com.etb.app.utils.AppLog;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author user
 * @date 2015-08-16
 */
public class RecentSearchesActivity extends BaseActivity {
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-d k", Locale.US);
    @Bind(android.R.id.list)
    ListView mRecyclerView;
    @Bind(R.id.hotel_list_no_result)
    LinearLayout mNoResult;

    @Override
    protected boolean requiresRequest() {
        return false;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, RecentSearchesActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Cursor cursor = getContentResolver().query(DbContract.SearchHistory.CONTENT_URI.buildUpon().build(), null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                mNoResult.setVisibility(View.VISIBLE);
            } else {
                mNoResult.setVisibility(View.GONE);
            }
        }

        mRecyclerView.setAdapter(new RecentSearchesAdapter(this, cursor));
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    try {
                        Date fromDate = input.parse(cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.FROM_DATE)) + " 23");
                        Date toDate = input.parse(cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.TO_DATE)) + " 23");
                        String northeastLat = cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NORTHEAST_LAT));
                        String northeastLon = cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NORTHEAST_LON));
                        String southwestLat = cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.SOUTHWEST_LAT));
                        String southwestLon = cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.SOUTHWEST_LON));
                        startSearch(new Location(cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LOCATION_NAME)), new LatLng(cursor.getDouble(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LAT)), cursor.getDouble(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LON)))),
                                (northeastLat == null || northeastLon == null || southwestLat == null || southwestLon == null) ? null : new ViewPort(cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LOCATION_NAME)), Double.valueOf(northeastLat), Double.valueOf(northeastLon), Double.valueOf(southwestLat), Double.valueOf(southwestLon)),
                                cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.TYPES)), cursor.getInt(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NUMBER_ROOMS)), cursor.getInt(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NUMBER_GUESTS))
                                , new DateRange(fromDate.getTime(), toDate.getTime()));
                    } catch (ParseException e) {
                        AppLog.e(e);
                    }
                }
            }
        });

    }

    public void startSearch(Location location, ViewPort viewPort,
                            String types, int rooms, int persons, DateRange dateRange) {
        HotelListRequest request = App.provide(this).createHotelsRequest();
        if (viewPort != null) {
            request.setType(viewPort);
        } else {
            request.setType(location);
        }
        if (dateRange.from.getTimeInMillis() > System.currentTimeMillis()) {
            request.setNumbersOfRooms(rooms);
            request.setNumberOfPersons(persons);
            request.setDateRange(dateRange);
        } else {
            request.setNumbersOfRooms(1);
            request.setNumberOfPersons(2);
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            request.setDateRange(new DateRange(today.getTime(), tomorrow.getTime()));
        }
        startActivity(HotelListActivity.createIntent(request, this));
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_recent_searches);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle(getString(R.string.recent_searched));
    }


}
