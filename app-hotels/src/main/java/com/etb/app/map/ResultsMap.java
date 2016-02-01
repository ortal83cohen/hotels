package com.etb.app.map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easytobook.api.EtbApi;
import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.ResultsResponse;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.Poi;
import com.easytobook.api.model.search.Type;
import com.easytobook.api.model.search.ViewPortType;
import com.etb.app.HotelsApplication;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.activity.HotelListActivity;
import com.etb.app.core.CoreInterface;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.events.Events;
import com.etb.app.events.SearchRequestEvent;
import com.etb.app.model.Location;
import com.etb.app.model.LocationWithTitle;
import com.etb.app.model.MapSelectedViewPort;
import com.etb.app.utils.AppLog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.squareup.okhttp.ResponseBody;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit.Call;
import retrofit.Response;


/**
 * @author alex
 * @date 2015-04-19
 */
public class ResultsMap {

    private static final double CHANGES_IN_MAP_ZOOM_SENSITIVITY = 0.5;
    private static final double CHANGES_IN_MAP_DISTANCE_SENSITIVITY = 0.006;
    private final BaseActivity mActivity;

    // GoogleMap class
    private final GoogleMap mGoogleMap;
    private final Listener mListener;
    private final HotelMarker mHotelMarker;
    private final PoiMarker mPoiMarker;
    private final HashSet<Integer> selectedHotelList = new HashSet<>();
    private final HashSet<Integer> selectedPoiList = new HashSet<>();
    private MarkerManager mMarkerManager;
    private MarkerManager.Collection mHotelsCollection;
    private MarkerManager.Collection mPoiCollection;
    private boolean mHotelsVisible;
    private double mLastLat = 0;
    private double mLastLong = 0;
    private double mLastZoom = 0;
    private Marker lastHotelMarkerClicked = null;
    private Marker lastPoiMarkerClicked = null;
    private boolean[] mPoisFilter = null;
    private double lastRadiusInKM;

    private RetrofitCallback<ResultsResponse> mResultsCallback = new RetrofitCallback<ResultsResponse>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            ((HotelListActivity) mActivity).hideLoaderImage();
        }

        @Override
        protected void success(ResultsResponse apiResponse, Response<ResultsResponse> response) {
            AppLog.d("EtbApi", "Response: " + apiResponse.meta);
            ((HotelListActivity) mActivity).hideLoaderImage();
            mHotelsCollection.clear();
            mPoiCollection.clear();
            if (mHotelsVisible) {
                addHotelsMarkers(apiResponse.accommodations);
            }
            addPois();
        }

    };

    public ResultsMap(GoogleMap googleMap, Listener listener, BaseActivity activity) {
        this.mGoogleMap = googleMap;
        mActivity = activity;
        mListener = listener;
        mMarkerManager = new MarkerManager(googleMap);
        mResultsCallback.attach(mActivity);
        mHotelMarker = new HotelMarker(mActivity);
        mPoiMarker = new PoiMarker(mActivity);
        init();
    }

    public void init() {


        mGoogleMap.setOnMarkerClickListener(mMarkerManager);

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Place dot on current location
            mGoogleMap.setMyLocationEnabled(true);
        }

        // Turns traffic layer on
        mGoogleMap.setTrafficEnabled(false);

        // Enables indoor maps
        mGoogleMap.setIndoorEnabled(true);

        // Turns on 3D buildings
        mGoogleMap.setBuildingsEnabled(true);

        // Show Zoom buttons
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = ((Activity) mActivity).getLayoutInflater().inflate(R.layout.poi_info_window, null, false);
                TextView title = (TextView) view.findViewById(android.R.id.title);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                ImageView background = (ImageView) view.findViewById(R.id.background);
                FrameLayout frame = (FrameLayout) view.findViewById(R.id.frame);
                title.setText(marker.getTitle());
                text1.setText(marker.getSnippet());
                ViewGroup.LayoutParams params = background.getLayoutParams();
                params.width = Math.max(marker.getSnippet().length(), marker.getTitle().length()) * 30 + 36;
                background.setLayoutParams(params);
                frame.setLayoutParams(params);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        final Type type = mActivity.getHotelsRequest().getType();

        if (type instanceof Location) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((Location) type).getLatLng(), 12), 300, null);
        } else if (type instanceof ViewPortType) {
            try {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                        new LatLng(((ViewPortType) type).getSouthwestLat(), ((ViewPortType) type).getSouthwestLon()),
                        new LatLng(((ViewPortType) type).getNortheastLat(), ((ViewPortType) type).getNortheastLon())), 0), 300, null);
            } catch (IllegalStateException e) {
                AppLog.e(e.getMessage() + "-" + ((ViewPortType) type).getSouthwestLat() + "-" + ((ViewPortType) type).getSouthwestLon() + "-" + ((ViewPortType) type).getNortheastLat() + "-" + ((ViewPortType) type).getNortheastLon());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(((ViewPortType) type).getSouthwestLat(), ((ViewPortType) type).getSouthwestLon()), 12), 300, null);
            }
        }


        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mLastLat == 0 || mLastLong == 0 || mLastZoom == 0) {
                    mLastLat = cameraPosition.target.latitude;
                    mLastLong = cameraPosition.target.longitude;
                    mLastZoom = cameraPosition.zoom;
                }
                Projection projection = mGoogleMap.getProjection();
                lastRadiusInKM = (projection.getVisibleRegion().latLngBounds.northeast.latitude - projection.getVisibleRegion().latLngBounds.southwest.latitude) * 111 / 2;

                if (Math.abs(cameraPosition.target.latitude - mLastLat) / lastRadiusInKM > CHANGES_IN_MAP_DISTANCE_SENSITIVITY ||
                        Math.abs(cameraPosition.target.longitude - mLastLong) / lastRadiusInKM > CHANGES_IN_MAP_DISTANCE_SENSITIVITY ||
                        Math.abs(cameraPosition.zoom - mLastZoom) > CHANGES_IN_MAP_ZOOM_SENSITIVITY) {
                    mLastLat = cameraPosition.target.latitude;
                    mLastLong = cameraPosition.target.longitude;
                    mLastZoom = cameraPosition.zoom;
                    ((HotelListActivity) mActivity).showRefreshHotelsButton();
                }

            }
        });
        mHotelsCollection = mMarkerManager.newCollection("hotels");
        mPoiCollection = mMarkerManager.newCollection("pois");

        toggleHotels();
    }

    public void setPoiFilters(boolean[] filters) {

        mPoisFilter = filters;
    }

    public void refreshHotels() {

        SearchRequest hotelsRequest = mActivity.getHotelsRequest();
        EtbApi etb = HotelsApplication.provide(mActivity).etbApi();
        Events.post(new SearchRequestEvent(hotelsRequest, 0));
        try {
            etb.results(hotelsRequest, 0).enqueue(mResultsCallback);
        } catch (InvalidParameterException e) {
            mActivity.finish();
        }
    }

    private void addHotelsMarkers(final List<Accommodation> accommodations) {
        if (accommodations != null) {
            for (int i = 0; i < accommodations.size(); i++) {
                Accommodation acc = accommodations.get(i);

                mHotelsCollection.addMarker(mHotelMarker.create(i, acc, selectedHotelList.contains(acc.id) ? HotelMarker.STATUS_SEEN : HotelMarker.STATUS_UNSEEN));
            }
            mHotelsCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Accommodation accMarker = accommodations.get(Integer.valueOf(marker.getTitle()));
                    mListener.onHotelMarkerClick(accMarker);
                    if (lastHotelMarkerClicked != null && !(lastHotelMarkerClicked.getTitle().equals(marker.getTitle()))) {
                        Integer lastMarkerPos = Integer.valueOf(lastHotelMarkerClicked.getTitle());

                        // In case we have an item with same position, recreate marker
                        if (lastMarkerPos < accommodations.size()) {
                            Accommodation accLastMarker = accommodations.get(lastMarkerPos);
                            mHotelsCollection.remove(lastHotelMarkerClicked);
                            mHotelsCollection.addMarker(mHotelMarker.create(lastMarkerPos, accLastMarker, selectedHotelList.contains(accLastMarker.id) ? HotelMarker.STATUS_SEEN : HotelMarker.STATUS_UNSEEN));
                        }
                    }
                    mHotelsCollection.remove(marker);
                    lastHotelMarkerClicked = mHotelsCollection.addMarker(mHotelMarker.create(Integer.valueOf(marker.getTitle()), accMarker, HotelMarker.STATUS_SELECTED));
                    selectedHotelList.add(accMarker.id);
                    return true;
                }
            });
        } else {
            Toast.makeText(mActivity, R.string.no_hotels_in_this_area, Toast.LENGTH_LONG).show();
        }
    }

    private void addPois() {

        CoreInterface.Service coreInterface = CoreInterface.create(mActivity);
        Type type = mActivity.getHotelsRequest().getType();
        Call<List<Poi>> call = null;
        if (type instanceof Location) {
            call = coreInterface.poiList(String.valueOf(((Location) type).getLatLng().longitude), String.valueOf(((Location) type).getLatLng().latitude), String.valueOf(((int) (lastRadiusInKM * 1000))));
        } else if (type instanceof ViewPortType) {
            call = coreInterface.poiList(String.valueOf(((ViewPortType) type).getNortheastLon()), String.valueOf(((ViewPortType) type).getNortheastLat()),
                    String.valueOf(((ViewPortType) type).getSouthwestLon()), String.valueOf(((ViewPortType) type).getSouthwestLat()));
        }
        if (call != null) {
            call.enqueue(new RetrofitCallback<List<Poi>>() {
                @Override
                public void success(List<Poi> list, Response<List<Poi>> response) {
                    mapAsync(list);
                }

                @Override
                public void failure(ResponseBody error, boolean isOffline) {
                    mapAsync(null);
                }
            });
        }
    }


    private void mapAsync(final List<Poi> poiList) {
        HashMap<Integer, Integer> types = new HashMap<>();
        if (poiList != null) {
            for (int i = 0; i < poiList.size(); i++) {
                Poi poi = poiList.get(i);
                if (poi.type_id == PoiMarker.TYPE_DISTRICT || poi.type_id == PoiMarker.TYPE_AREA) {
                    break;
                }
                if (!types.containsKey(poi.getType_id())) {
                    types.put(poi.getType_id(), 1);
                } else {
                    types.put(poi.getType_id(), types.get(poi.getType_id()) + 1);
                }
                if (mPoisFilter != null && mPoisFilter[poi.type_id]) {
                    mPoiCollection.addMarker(mPoiMarker.create(i, poi, PoiMarker.STATUS_UNSEEN));
                }
            }


            mPoiCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }
            });
        }

        mListener.onLandmarksTypesChange(types);
    }

    public void moveCamera(double lat, double lon) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
    }

    public void moveCamera(double northeastLat, double northeastLon, double southwestLat, double southwestLon) {
        LatLngBounds bounds = new LatLngBounds(new LatLng(southwestLat, southwestLon), new LatLng(northeastLat, northeastLon));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }

    public boolean toggleHotels() {
        if (mHotelsVisible) {
            mHotelsCollection.clear();
            mPoiCollection.clear();
            mHotelsVisible = false;
        } else {
            refreshHotels();
            mHotelsVisible = true;
        }
        return mHotelsVisible;
    }

    public void updateRequest() {

        Type type = mActivity.getHotelsRequest().getType();
        if (type instanceof MapSelectedViewPort) {
            ((MapSelectedViewPort) type).setLatLngBounds(mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
        } else {
            String title = type instanceof LocationWithTitle ? ((LocationWithTitle) type).getTitle() : null;
            mActivity.getHotelsRequest().setType(new MapSelectedViewPort(title, mGoogleMap.getProjection().getVisibleRegion().latLngBounds));
        }
    }

    public interface Listener {

        void onLandmarksTypesChange(HashMap<Integer, Integer> types);

        void onHotelMarkerClick(Accommodation acc);

        void removeHotelSummaryFragment();
    }
}
