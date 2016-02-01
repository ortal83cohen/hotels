package com.etb.app.analytics;

import android.app.Activity;
import android.content.Context;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.HotelRequest;
import com.easytobook.api.model.Order;
import com.easytobook.api.model.OrderResponse;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.R;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.CurrentLocation;
import com.etb.app.model.Location;
import com.etb.app.model.LocationWithTitle;
import com.etb.app.model.OrderItem;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * @author ortal
 * @date 2016-01-04
 */
public class GoogleAnalyticsCalls extends AnalyticsCalls {
    private static final int KEY_LOCATION = 1;
    private static final int KEY_CURRENCY = 2;
    private static final int KEY_COUNTRY_CODE = 3;
    private static final int KEY_FROM = 4;
    private static final int KEY_TO = 5;
    private static final int KEY_HOTEL_NAME = 6;
    private static final int KEY_HOTEL_INDEX = 7;
    private static final int KEY_MIN_RATE_PRICE = 8;
    private static final int KEY_RATE_NAME = 9;
    private static final int KEY_URL = 10;
    private static final int KEY_RESPONSE = 11;
    private static final int KEY_BODY = 12;
    private Tracker mTracker;
    private SimpleDateFormat mDayFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    synchronized public void register(Context applicationContext) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(applicationContext);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(applicationContext.getString(R.string.google_analytics_id));

        }
    }

    public void transaction(OrderResponse orderResponse) {

        Order.Rate rate = orderResponse.order.rates.get(0);
        Product product = new Product()
                .setId(String.valueOf(rate.accommodation.id))
                .setName(rate.accommodation.name)
                .setCategory(rate.accommodation.summary.country)
                .setBrand(rate.accommodation.summary.city)
                .setVariant(rate.name)
                .setPrice(Double.valueOf(rate.charge.totalChargeable))
//                .setCouponCode("APPARELSALE")
                .setQuantity(rate.rateCount);
        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                .setTransactionId(String.valueOf(orderResponse.order.orderId))
                .setTransactionAffiliation("Android")
//                .setTransactionRevenue(37.39)
//                .setTransactionTax(2.85)
//                .setTransactionShipping(5.34)
//                .setTransactionCouponCode("SUMMER2013")
                ;
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                .addProduct(product)
                .setProductAction(productAction);

        mTracker.setScreenName("transaction");
        mTracker.send(builder.build());
    }

    public void trackRetrofitFailure(String url, String response, String body) {
        body=body.equals("")?"none":body;
        mTracker.setScreenName("Retrofit Failure");
        mTracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(KEY_RESPONSE, response).setCustomDimension(KEY_URL, url).setCustomDimension(KEY_BODY, body).build());
    }

    public void trackSearchResults(SearchRequest request) {
        String location;
        if (request.getType() instanceof LocationWithTitle) {
            LocationWithTitle loc = (LocationWithTitle) (request).getType();
            location = loc.getTitle();
        } else if (request.getType() instanceof CurrentLocation) {
            CurrentLocation loc = (CurrentLocation) (request).getType();
            location = loc.getLatLng().toString();
        } else if (request.getType() instanceof Location) {
            Location loc = (Location) (request).getType();
            location = loc.getTitle();
        } else {
            location = request.getType().toString();
        }

        mTracker.setScreenName("Hotel List");
        mTracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(KEY_CURRENCY, request.getCurrency()).
                setCustomDimension(KEY_COUNTRY_CODE, request.getCustomerCountryCode()).setCustomDimension(KEY_LOCATION, location).
                setCustomDimension(KEY_FROM, mDayFormatter.format(request.getDateRange().from.getTime())).setCustomDimension(KEY_TO, mDayFormatter.format(request.getDateRange().to.getTime())).build());
    }


    public void trackBookingFormPersonal() {
        mTracker.setScreenName("Personal Info");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackLanding() {
        mTracker.setScreenName("Home");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    public void trackFilterPage() {
        mTracker.setScreenName("Hotel List Filters");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackBookingFormPayment() {
        mTracker.setScreenName("Booking Form Payment");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackHotelRooms(HotelRequest hotelRequest) {
        mTracker.setScreenName("Hotel Rooms");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackHotelReviews(HotelRequest hotelRequest) {
        mTracker.setScreenName("Hotel Reviews");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackHotelDetails(HotelRequest request, HotelSnippet hotelSnippet, Accommodation.Rate rate, String currencyCode) {
        mTracker.setScreenName("Hotel Details");
        Product product = new Product()
                .setId(String.valueOf(hotelSnippet.getAccommodation().id))
                .setName(hotelSnippet.getAccommodation().name)
                .setCategory(hotelSnippet.getAccommodation().summary.country)
                .setBrand(hotelSnippet.getAccommodation().summary.city)
                .setVariant(rate.name)
//                .setPosition(1)
                ;
        double price = 0;
        if (rate != null) {
            price = rate.displayPrice.get(currencyCode);
        }

        mTracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(KEY_HOTEL_NAME, hotelSnippet.getName()).
                setCustomDimension(KEY_HOTEL_INDEX, String.valueOf(hotelSnippet.getPosition())).setCustomDimension(KEY_MIN_RATE_PRICE, String.format("%.1f", price)).
                setCustomDimension(KEY_CURRENCY, currencyCode).addImpression(product, "impression").build());
    }

    public void trackBookingFormAddress() {
        mTracker.setScreenName("Booking Form Address");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void pauseCollectingLifecycleData() {
        mTracker.setScreenName("pauseCollectingLifecycleData");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void collectLifecycleData(Activity activity) {
        mTracker.setScreenName("collectLifecycleData");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackBookingSummary(HotelRequest request, HotelSnippet hotelSnippet, OrderItem orderItem) {
        mTracker.setScreenName("Booking Summary");

        mTracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(KEY_HOTEL_NAME, hotelSnippet.getName()).
                setCustomDimension(KEY_RATE_NAME, orderItem.rateName).build());
    }

    public void trackBookingConfirmation() {
        mTracker.setScreenName("Booking Confirmation");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

//    public void event(String categoryId,String actionId,String labelId){
//        mTracker.send(new HitBuilders.EventBuilder()
//                .setCategory(categoryId)
//                .setAction(actionId)
//                .setLabel(labelId)
//                .build());
//    }
}

