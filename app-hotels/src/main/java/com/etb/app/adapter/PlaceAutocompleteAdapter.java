/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.etb.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easytobook.api.model.DateRange;
import com.etb.app.R;
import com.etb.app.provider.DbContract;
import com.etb.app.utils.AppLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class PlaceAutocompleteAdapter
        extends ArrayAdapter<PlaceAutocompleteAdapter.PlaceItem> implements Filterable {

    private LayoutInflater mInflater;
    private int mDropDownResource;
    /**
     * Current results returned by this adapter.
     */
    private ArrayList<PlaceItem> mResultList;
    private ArrayList<PlaceItem> mHistoryList;

    /**
     * Handles autocomplete requests.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The bounds used for Places Geo Data autocomplete API requests.
     */
    private LatLngBounds mBounds;

    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private AutocompleteFilter mPlaceFilter;
    private CharSequence mConstraint;

    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     *
     * @see ArrayAdapter#ArrayAdapter(Context, int)
     */
    public PlaceAutocompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds) {
        super(context, resource);
        mDropDownResource = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = AutocompleteFilter.create(Arrays.asList(Place.TYPE_GEOCODE, Place.TYPE_ESTABLISHMENT));
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setupHistory(CharSequence constraint) {
        if (constraint.toString().equals("")) {
            mHistoryList = getHistory();
        }
        mConstraint = constraint;
    }

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {
        LinearLayout Layout;
        TextView text;
        Layout = (LinearLayout) mInflater.inflate(resource, parent, false);
        text = (TextView) Layout.findViewById(android.R.id.title);
        PlaceItem item = getItem(position);

        text.setCompoundDrawablesRelativeWithIntrinsicBounds(item.getDrawable(), 0, 0, 0);
        text.setTextColor(item.getTextColor());

        text.setText(Html.fromHtml(item.getHtmlText()));
        return Layout;
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        int count;

        if (mConstraint == null || mConstraint.toString().equals("")) {
            count = mHistoryList == null ? 0 : mHistoryList.size();
        } else {
            count = mResultList == null ? 0 : mResultList.size();
        }
        return count;
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public PlaceItem getItem(int position) {
        if (mConstraint == null || mConstraint.toString().equals("")) {
            return mHistoryList.get(position);
        }
        return (mResultList == null || position >= mResultList.size()) ? new PlaceCurrentLocation() : mResultList.get(position);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Query the autocomplete API for the (constraint) search string.
                ArrayList<PlaceItem> values = getAutocomplete(constraint);
                if (values != null) {
                    // The API successfully returned results.
                    results.values = values;
                    results.count = values.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResultList = (ArrayList<PlaceItem>) results.values;
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    mResultList = null;
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
    }


    private ArrayList<PlaceItem> getAutocomplete(CharSequence constraint) {
        ArrayList<PlaceItem> resultList = new ArrayList<>();
        resultList.add(new PlaceCurrentLocation());

        // Submit the query to the autocomplete API and retrieve a PendingResult that will
        // contain the results when the query completes.
        AutocompletePredictionBuffer autocompletePredictions;

        if (constraint != null && !constraint.equals("")) {
            mConstraint = constraint;
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            autocompletePredictions = results.await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(getContext(), R.string.could_not_connect_to_google + status.toString(),
                        Toast.LENGTH_SHORT).show();

                autocompletePredictions.release();
                return null;
            }
            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and mDescription).

            for (AutocompletePrediction prediction : autocompletePredictions) {
                if (prediction.getPlaceTypes().contains(Place.TYPE_COUNTRY)) {
                    continue;
                }
                String htmlText = prediction.getDescription();
                if (mConstraint != null && htmlText.toUpperCase().startsWith(mConstraint.toString().toUpperCase())) {
                    htmlText = "<font color=\"black\">" + htmlText.substring(0, mConstraint.length()) + "</font>" + htmlText.substring(mConstraint.length());
                }
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getDescription(), htmlText));
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();
        }

        return resultList;
    }

    private ArrayList<PlaceItem> getHistory() {
        ArrayList<PlaceItem> resultList = new ArrayList<>();
        resultList.add(new PlaceCurrentLocation());

        Cursor cursor = getContext().getContentResolver().query(DbContract.SearchHistory.CONTENT_URI.buildUpon().appendQueryParameter("limit", "5").build(), null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LOCATION_NAME)) == null) {
                    continue;
                }
                resultList.add(new PlaceHistory(
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LOCATION_NAME)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NUMBER_GUESTS)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NUMBER_ROOMS)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LAT)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.LON)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NORTHEAST_LAT)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.NORTHEAST_LON)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.SOUTHWEST_LAT)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.SOUTHWEST_LON)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.TYPES)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.FROM_DATE)),
                        cursor.getString(cursor.getColumnIndex(DbContract.SearchHistoryColumns.TO_DATE))))
                ;

            } while (cursor.moveToNext());
            cursor.close();
        }
        return resultList;
    }

    public PlaceItem getFirstSearchItem() {
        if (mResultList == null) {
            return null;
        }
        return mResultList.size() > 2 ? mResultList.get(1) : null;
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete extends PlaceItem {
        private CharSequence placeId;
        private CharSequence htmlText;

        PlaceAutocomplete(CharSequence placeId, CharSequence description, CharSequence htmlText) {
            super(description);
            this.placeId = placeId;
            this.htmlText = htmlText;
        }

        @Override
        public String getHtmlText() {
            return this.htmlText.toString();
        }

        @Override
        public int getDrawable() {
            return R.drawable.desintation_autocomplete;
        }

        @Override
        public int getTextColor() {
            return getContext().getResources().getColor(R.color.text_gray);
        }

        public String getPlaceId() {
            return String.valueOf(this.placeId);
        }
    }

    public class PlaceHistory extends PlaceItem {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-d k", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("MMM d", Locale.getDefault());
        private CharSequence mHtmlText;
        private String mNumberGuests;
        private String mNumberRooms;
        private float mLat;
        private float mLng;
        private double mNortheastLat;
        private double mNortheastLon;
        private double mSouthwestLat;
        private double mSouthwestLon;
        private String mGoogleTypes;
        private Date mFromDate;
        private Date mToDate;

        PlaceHistory(String locationName, String numberGuests, String numberRooms, String lat, String lng, String northeastLat, String northeastLon, String southwestLat, String southwestLon, String googleTypes, String fromDate, String toDate) {
            super(locationName);
            mNumberGuests = numberGuests;
            mNumberRooms = numberRooms;
            mLat = Float.valueOf(lat);
            mLng = Float.valueOf(lng);
            mNortheastLat = northeastLat == null ? 0 : Double.valueOf(northeastLat);
            mNortheastLon = northeastLon == null ? 0 : Double.valueOf(northeastLon);
            mSouthwestLat = southwestLat == null ? 0 : Double.valueOf(southwestLat);
            mSouthwestLon = southwestLon == null ? 0 : Double.valueOf(southwestLon);
            mGoogleTypes = googleTypes;
            try {
                mFromDate = input.parse(fromDate + " 10");
                mToDate = input.parse(toDate + " 10");

                this.mHtmlText = "<font color=\"black\">" + locationName + " </font> <br>"
                        + numberGuests + " guests, "
                        + output.format(mFromDate) + " - "
                        + output.format(mToDate);
            } catch (ParseException e) {
                AppLog.e(e);
            }
        }

        public LatLng getLatLng() {
            return new LatLng(mLat, mLng);
        }

        public LatLngBounds getLatLngBounds() {
            if (mSouthwestLat == 0 || mSouthwestLon == 0 || mNortheastLat == 0 || mNortheastLon == 0) {
                return null;
            }
            return new LatLngBounds(new LatLng(mSouthwestLat, mSouthwestLon), new LatLng(mNortheastLat, mNortheastLon));
        }


        public int getNumberGuests() {
            return Integer.valueOf(mNumberGuests);
        }

        public int getNumberRooms() {
            return Integer.valueOf(mNumberRooms);
        }

        @Override
        public String getHtmlText() {
            return this.mHtmlText.toString();
        }

        @Override
        public int getDrawable() {
            return R.drawable.mainnav_recent_search;
        }

        @Override
        public int getTextColor() {
            return getContext().getResources().getColor(R.color.text_gray);
        }

        public DateRange getDateRange() {
            return new DateRange(mFromDate.getTime(), mToDate.getTime());
        }
    }

    public class PlaceCurrentLocation extends PlaceItem {
        PlaceCurrentLocation() {
            super("Current Location");
        }

        @Override
        public String getHtmlText() {
            return this.mDescription.toString();
        }

        @Override
        public int getDrawable() {
            return R.drawable.current_location_autocomplete;
        }

        @Override
        public int getTextColor() {
            return Color.BLACK;
        }
    }


    public abstract class PlaceItem {
        protected CharSequence mDescription;

        public PlaceItem(CharSequence description) {
            this.mDescription = description;
        }

        @Override
        public String toString() {
            return mDescription.toString();
        }

        public abstract String getHtmlText();

        public abstract int getDrawable();

        public abstract int getTextColor();


    }

}
