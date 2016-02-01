package com.etb.app.analytics;

import android.app.Activity;
import android.content.Context;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.HotelRequest;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.utils.DateRangeUtils;
import com.etb.app.BuildConfig;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.LocationWithTitle;
import com.etb.app.model.OrderItem;
import com.etb.app.utils.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author alex
 * @date 2015-11-18
 */
public class Omniture extends AnalyticsCalls{
    static final String KEY_HOTEL_NAME = "v40";
    static final String KEY_HOTEL_INDEX = "v13";
    static final String KEY_MIN_RATE_PRICE = "v46";
    static final String KEY_CURRENCY = "v45";
    static final String KEY_RATE_NAME = "v12";
    static final String KEY_PLATFORM = "v1";
    static final String KEY_DATE_FROM = "v4";
    static final String KEY_DATE_UNTIL = "v5";
    static final String KEY_NIGHTS = "v6";
    static final String KEY_LEADTIME = "v7";
    static final String KEY_PERSONS = "v11";
    static final String KEY_ROOMS = "v20";
    static final String KEY_LOCATION_TITLE = "v3";

    public void register(Context applicationContext) {
        Config.setContext(applicationContext);
        Config.setDebugLogging(BuildConfig.DEBUG);
    }

    public void pauseCollectingLifecycleData() {
        Config.pauseCollectingLifecycleData();
    }

    public void collectLifecycleData(Activity activity) {
        Config.collectLifecycleData(activity);
    }

    public void trackLanding() {
        Analytics.trackState("mob:hotels:landing:page", createContextData());
    }

    public void trackSearchResults(SearchRequest request) {
        HashMap<String, Object> contextData = createContextRequestData(request);

        Analytics.trackState("mob:hotels:searchresults", contextData);
    }

    public void trackFilterPage() {
        Analytics.trackState("mob:hotels:filter:page", createContextData());
    }

    public void trackHotelDetails(HotelRequest request, HotelSnippet hotelSnippet, Accommodation.Rate rate, String currencyCode) {
        HashMap<String, Object> contextData = createContextRequestData(request);

        double price = 0;
        if (rate != null) {
            price = rate.displayPrice.get(currencyCode);
        }

        contextData.put(KEY_HOTEL_NAME, hotelSnippet.getName());
        contextData.put(KEY_HOTEL_INDEX, hotelSnippet.getPosition());
        contextData.put(KEY_MIN_RATE_PRICE, String.format("%.1f", price));
        contextData.put(KEY_CURRENCY, currencyCode);

        Analytics.trackState("mob:hotels:hoteldetails", contextData);
    }

    public void trackHotelReviews(HotelRequest request) {
        HashMap<String, Object> contextData = createContextRequestData(request);

        Analytics.trackState("mob:hotels:custreview", contextData);
    }

    public void trackHotelRooms(HotelRequest request) {
        HashMap<String, Object> contextData = createContextRequestData(request);

        Analytics.trackState("mob:hotels:roomtypes", contextData);
    }

    public void trackBookingSummary(HotelRequest request, HotelSnippet hotelSnippet, OrderItem orderItem) {
        HashMap<String, Object> contextData = createContextRequestData(request);

        contextData.put(KEY_HOTEL_NAME, hotelSnippet.getName());
        contextData.put(KEY_RATE_NAME, orderItem.rateName);

        Analytics.trackState("mob:hotels:review", contextData);
    }

    public void trackBookingFormPersonal() {
        Analytics.trackState("mob:hotels:travelerdetail", createContextData());
    }

    public void trackBookingFormPayment() {
        Analytics.trackState("mob:hotels:payment", createContextData());
    }

    public void trackBookingFormAddress() {
        Analytics.trackState("mob:hotels:billing", createContextData());
    }

    public void trackBookingConfirmation() {
        Analytics.trackState("mob:hotels:confirmation", createContextData());
    }

    private HashMap<String, Object> createContextData() {
        HashMap<String, Object> contextData = new HashMap<>();
        contextData.put(KEY_PLATFORM, "android");
        return contextData;
    }

    HashMap<String, Object> createContextRequestData(HotelRequest request) {
        HashMap<String, Object> contextData = createContextData();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());



        if (request instanceof HotelListRequest || request instanceof SearchRequest) {
            if (((SearchRequest) request).getType() instanceof LocationWithTitle) {
                LocationWithTitle loc = (LocationWithTitle) ((SearchRequest) request).getType();
                contextData.put(KEY_LOCATION_TITLE, loc.getTitle());
            }
        }

        if (request.getDateRange() != null) {
            Calendar arrival = request.getDateRange().from;
            Calendar today = getToday();
            // check-in date
            contextData.put(KEY_DATE_FROM, dateFormat.format(arrival.getTime()));
            // check-out date
            contextData.put(KEY_DATE_UNTIL, dateFormat.format(request.getDateRange().to.getTime()));
            // no_of_nights
            contextData.put(KEY_NIGHTS, request.getDateRange().days());

            // Advance purchase window
            // Set same time as arrival
            CalendarUtils.copyTime(today, arrival);
            contextData.put(KEY_LEADTIME, DateRangeUtils.days(today.getTimeInMillis(), request.getDateRange().from.getTimeInMillis()));
        }

        // adultCount | childCount | (adultCount+childCount)
        contextData.put(KEY_PERSONS, String.format("%d|0|%d", request.getNumberOfPersons(), request.getNumberOfPersons()));
        contextData.put(KEY_ROOMS, request.getNumberOfRooms());

        return contextData;
    }

    Calendar getToday() {
        return Calendar.getInstance(Locale.getDefault());
    }
}
