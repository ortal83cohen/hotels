package com.etb.app.analytics;

import android.app.Activity;
import android.content.Context;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.HotelRequest;
import com.easytobook.api.model.OrderResponse;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.OrderItem;


/**
 * @author ortal
 * @date 2016-01-04
 */
public class AnalyticsCalls {
    private static Omniture omniture = null;
    private static GoogleAnalyticsCalls googleAnalyticsCalls = null;

    private static AnalyticsCalls mInstance = null;

    public static AnalyticsCalls get() {
        if (mInstance == null) {
            mInstance = new AnalyticsCalls();
        }
        return mInstance;
    }


    public void register(Context applicationContext) {
//        omniture = new Omniture();
//        googleAnalyticsCalls = new GoogleAnalyticsCalls();
//
//        omniture.register(applicationContext);
//        googleAnalyticsCalls.register(applicationContext);
    }

    public void transaction(OrderResponse orderResponse) {
        //     googleAnalyticsCalls.transaction(orderResponse);
    }


    public void trackRetrofitFailure(String url, String response, String body) {
//        googleAnalyticsCalls.trackRetrofitFailure(url, response, body);
    }

    public void trackSearchResults(SearchRequest request) {
//        omniture.trackSearchResults(request);
//        googleAnalyticsCalls.trackSearchResults(request);
    }

    public void trackBookingFormPersonal() {
//        omniture.trackBookingFormPersonal();
//        googleAnalyticsCalls.trackBookingFormPersonal();
    }

    public void trackLanding() {
//        omniture.trackLanding();
//        googleAnalyticsCalls.trackLanding();
    }

    public void trackFilterPage() {
//        omniture.trackFilterPage();
//        googleAnalyticsCalls.trackFilterPage();
    }

    public void trackBookingFormPayment() {
//        omniture.trackBookingFormPayment();
//        googleAnalyticsCalls.trackBookingFormPayment();
    }

    public void trackHotelRooms(HotelRequest hotelRequest) {
//        omniture.trackHotelRooms(hotelRequest);
//        googleAnalyticsCalls.trackHotelRooms(hotelRequest);
    }

    public void trackHotelReviews(HotelRequest hotelRequest) {
//        omniture.trackHotelReviews(hotelRequest);
//        googleAnalyticsCalls.trackHotelReviews(hotelRequest);
    }

    public void trackHotelDetails(HotelRequest request, HotelSnippet hotelSnippet, Accommodation.Rate rate, String currencyCode) {
//        omniture.trackHotelDetails(request, hotelSnippet, rate, currencyCode);
//        googleAnalyticsCalls.trackHotelDetails(request, hotelSnippet, rate, currencyCode);
    }

    public void trackBookingFormAddress() {
//        omniture.trackBookingFormAddress();
//        googleAnalyticsCalls.trackBookingFormAddress();
    }

    public void pauseCollectingLifecycleData() {
//        omniture.pauseCollectingLifecycleData();
//        googleAnalyticsCalls.pauseCollectingLifecycleData();
    }

    public void collectLifecycleData(Activity activity) {
//        if (activity != null && omniture != null) {
//            omniture.collectLifecycleData(activity);
//        }
//        googleAnalyticsCalls.collectLifecycleData(activity);
    }

    public void trackBookingSummary(HotelRequest request, HotelSnippet hotelSnippet, OrderItem orderItem) {
//        omniture.trackBookingSummary(request, hotelSnippet, orderItem);
//        googleAnalyticsCalls.trackBookingSummary(request, hotelSnippet, orderItem);
    }

    public void trackBookingConfirmation() {
//        omniture.trackBookingConfirmation();
//        googleAnalyticsCalls.trackBookingConfirmation();
    }

//    public void event(String categoryId,String actionId,String labelId){
//        googleAnalyticsCalls.event( categoryId, actionId, labelId);
//    }
}
