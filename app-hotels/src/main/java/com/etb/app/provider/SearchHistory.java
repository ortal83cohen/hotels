package com.etb.app.provider;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.easytobook.api.model.DateRange;
import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author alex
 * @date 2015-12-01
 */
public class SearchHistory {
    public static void insert(Place place, DateRange range, int persons, int rooms, Context context) {
        ContentValues values = new ContentValues();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d", Locale.US);
        String placeTypes = TextUtils.join(",", place.getPlaceTypes());
        values.put(DbContract.SearchHistoryColumns.LOCATION_NAME, place.getAddress().toString());
        values.put(DbContract.SearchHistoryColumns.LAT, place.getLatLng().latitude);
        values.put(DbContract.SearchHistoryColumns.LON, place.getLatLng().longitude);
        values.put(DbContract.SearchHistoryColumns.NORTHEAST_LAT, place.getViewport() == null ? null : place.getViewport().northeast.latitude);
        values.put(DbContract.SearchHistoryColumns.NORTHEAST_LON, place.getViewport() == null ? null : place.getViewport().northeast.longitude);
        values.put(DbContract.SearchHistoryColumns.SOUTHWEST_LAT, place.getViewport() == null ? null : place.getViewport().southwest.latitude);
        values.put(DbContract.SearchHistoryColumns.SOUTHWEST_LON, place.getViewport() == null ? null : place.getViewport().southwest.longitude);
        values.put(DbContract.SearchHistoryColumns.TYPES, placeTypes);
        values.put(DbContract.SearchHistoryColumns.NUMBER_GUESTS, persons);
        values.put(DbContract.SearchHistoryColumns.NUMBER_ROOMS, rooms);
        values.put(DbContract.SearchHistoryColumns.FROM_DATE, format.format(range.from.getTime()));
        values.put(DbContract.SearchHistoryColumns.TO_DATE, format.format(range.to.getTime()));

        context.getContentResolver().insert(DbContract.SearchHistory.CONTENT_URI, values);
    }
}
