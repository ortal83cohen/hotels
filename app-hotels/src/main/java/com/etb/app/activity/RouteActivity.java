package com.etb.app.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.easytobook.api.EtbApi;
import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.Order;
import com.easytobook.api.model.OrderResponse;
import com.easytobook.api.model.ResultsResponse;
import com.easytobook.api.model.search.ListType;
import com.etb.app.App;
import com.etb.app.HotelsApplication;
import com.etb.app.core.CoreInterface;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.Location;
import com.etb.app.provider.DbContract;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.BrowserUtils;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.ResponseBody;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Response;


/**
 * @author ortal
 * @date 2015-06-11
 */
public class RouteActivity extends Activity {

    private Uri mUriData;
    private RetrofitCallback<ResultsResponse> mResultsCallback = new RetrofitCallback<ResultsResponse>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            AppLog.e("RouteActivity - results failure");
            startActivity(HomeActivity.createIntent(RouteActivity.this));
        }

        @Override
        protected void success(ResultsResponse apiResponse, Response<ResultsResponse> response) {
            if (apiResponse.accommodations == null) {
                AppLog.e("RouteActivity - accommodations == null");
                startActivity(HomeActivity.createIntent(RouteActivity.this));
            } else {
                startHotelSummaryActivity(apiResponse.accommodations.get(0));
            }
        }

    };
    private RetrofitCallback<OrderResponse> mRetrieveResultsCallback = new RetrofitCallback<OrderResponse>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            startActivity(MemberActivity.createIntent(0, getBaseContext()));
        }

        @Override
        public void success(OrderResponse orderResponse, Response response) {
            Order.Rate rate = orderResponse.order.rates.get(0);
            ContentValues values = new ContentValues();
            values.put(DbContract.BookingsColumns.ORDER_ID, orderResponse.order.orderId);
            values.put(DbContract.BookingsColumns.REFERENCE, orderResponse.order.orderId);
            values.put(DbContract.BookingsColumns.CITY, rate.accommodation.summary.city);
            values.put(DbContract.BookingsColumns.COUNTRY, rate.accommodation.summary.country);
            values.put(DbContract.BookingsColumns.TOTAL_VALUE, rate.charge.totalChargeable);
            values.put(DbContract.BookingsColumns.STARS, String.valueOf(Double.valueOf(rate.accommodation.starRating)));
            values.put(DbContract.BookingsColumns.ROOMS, rate.rateCount);
            values.put(DbContract.BookingsColumns.RATE_NAME, rate.name);
            values.put(DbContract.BookingsColumns.IS_CANCELLED, false);
            if ((rate.accommodation.images.size() > 0)) {
                values.put(DbContract.BookingsColumns.IMAGE, rate.accommodation.images.get(0));
            }
            values.put(DbContract.BookingsColumns.HOTEL_NAME, rate.accommodation.name);
            values.put(DbContract.BookingsColumns.CURRENCY, rate.charge.currency);
            values.put(DbContract.BookingsColumns.ARRIVAL, rate.checkIn);
            values.put(DbContract.BookingsColumns.DEPARTURE, rate.checkOut);
            values.put(DbContract.BookingsColumns.RATE_ID, rate.rateId);
            values.put(DbContract.BookingsColumns.CONFIRMATION_ID, rate.confirmationId);
            getBaseContext().getContentResolver().insert(DbContract.Bookings.CONTENT_URI, values);
            startActivity(MemberActivity.createIntent(0, getBaseContext()));
        }

    };

    @Override
    public void onStart() {
        try {
            super.onStart();
            mUriData = getIntent().getData();
            AppLog.d("Received data: " + mUriData);
            if (mUriData == null) {
                startActivity(HomeActivity.createIntent(RouteActivity.this));
                finish();
                return;
            }
            if (mUriData.toString().contains("mobile/download.php")) {
                String[] split = mUriData.getEncodedQuery().split("url=");
                mUriData = Uri.parse(URLDecoder.decode(split[1], "UTF-8"));
            }
            if (mUriData.toString().contains("/mybooking/")) {
                {//index.php?br=4404268-66&pc=5418&amu=280822147
                    String[] split = mUriData.getEncodedQuery().split("&");
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : split) {
                        String name = param.split("=")[0];
                        String value = param.split("=")[1];
                        map.put(name, value);
                    }
                    retrieveBooking(map.get("br"), map.get("pc"));
                }
            } else {
                if (mUriData.toString().startsWith("http://recp.mkt32.net/")) {
                    String[] split = mUriData.getEncodedQuery().split("&kd=");
                    mUriData = Uri.parse(URLDecoder.decode(split[1], "UTF-8"));
                }
                String uri = mUriData.toString();
                uri = uri.replace("m.easytobook.com", "www.easytobook.com");

                CoreInterface.create(getApplicationContext()).uriParse(uri).enqueue(new RetrofitCallback<ArrayMap<String, String>>() {
                    @Override
                    protected void failure(ResponseBody response, boolean isOffline) {
                        sendToWebBrowser(mUriData);
                    }

                    @Override
                    protected void success(ArrayMap<String, String> params, Response<ArrayMap<String, String>> response) {
                        try {
                            switch (params.get("target")) {
                                case "/search/result.php":
                                case "/index_landing.php":
                                    final HotelListRequest hotelListRequest = App.provide(RouteActivity.this).createHotelsRequest();
                                    hotelListRequest.setType(new Location(params.get("name"), new LatLng(Double.valueOf(params.get("lat")), Double.valueOf(params.get("lon")))));
                                    startActivity(HotelListActivity.createIntent(hotelListRequest, RouteActivity.this));
                                    break;
                                case "/hoteldetails.php":
                                    loadAccommodationDetails(params.get("group_hotel_id"));
                                    break;
                                case "index_home.php":
                                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                                    break;
                                default:
                                    sendToWebBrowser(mUriData);
                            }
                        } catch (Exception e) {
                            AppLog.e(e);
                            sendToWebBrowser(getIntent().getData());
                        }
                    }
                });
            }
        } catch (Exception e) {
            AppLog.e(e);
            sendToWebBrowser(getIntent().getData());
        }
        finish();
    }

    private void loadAccommodationDetails(String hotelId) {
        ArrayList<String> hotels = new ArrayList<>();
        hotels.add(hotelId);
        HotelListRequest request = App.provide(this).createHotelsRequest();
        request.setType(new ListType(hotels));
        request.setNoDatesRequest();
        EtbApi etbApi = HotelsApplication.provide(this).etbApi();
        etbApi.results(request, 0).enqueue(mResultsCallback);
    }

    private void startHotelSummaryActivity(Accommodation acc) {
        try {
            int rateId = acc.getFirstRate() == null ? 0 : acc.getFirstRate().rateId;
            final HotelSnippet hotelSnippet = HotelSnippet.from(acc, rateId, -1);
            HotelListRequest request = App.provide(this).createHotelsRequest();
            request.setNoDatesRequest();
            startActivity(HotelSummaryActivity.createIntent(hotelSnippet, request, true, this));
        } catch (Exception e) {
            AppLog.e(e);
            sendToWebBrowser(getIntent().getData());
        }
    }

    private void sendToWebBrowser(Uri data) {
        getIntent().setData(null);
        BrowserUtils.sendToWebBrowser(data, this);
    }

    private void retrieveBooking(String id, String pass) {
        EtbApi etbApi = HotelsApplication.provide(this).etbApi();
        etbApi.retrieve("B-" + id, pass).enqueue(mRetrieveResultsCallback);
    }
}
