package com.etb.app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easytobook.api.model.search.Poi;
import com.etb.app.R;
import com.etb.app.adapter.ViewPagerAdapter;
import com.etb.app.core.CoreInterface;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.fragment.HotelFacilitiesFragment;
import com.etb.app.fragment.HotelReviewsFragment;
import com.etb.app.fragment.HotelsMapFragment;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.map.PoiMarker;
import com.etb.app.model.HotelListRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Response;


/**
 * @author alex
 * @date 2015-06-14
 */
public class HotelDetailsActivity extends TabActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static final int TAB_FACILITIES = 0;
    public static final int TAB_REVIEWS = 1;
    public static final int TAB_MAP = 2;
    private static final String MAP = "menu_map";
    private static final int MAP_POSITION = 2;
    private static final String HOTEL_MARKER = "hotel_marker";
    private static final int LANDMARKS_SIZE = 1000;
    private static final String EXTRA_SNIPPET = "snipet";
    private static final String EXTRA_SNIPPET_DETAILS = "snipet_details";
    @Bind(R.id.available_rooms_button)
    Button mAllRoomsButton;
    @Bind(R.id.show_landmarks)
    Button mShowLandmarks;
    private HotelSnippet mHotelSnippet;
    private HotelSnippet mHotelSnippetDetails;
    private SupportMapFragment mSupportMapFragment;
    private int mDrawLandmarksInMeters = 0;
    private List<Poi> mPoiList = null;
    private PoiMarker mPoiMarker;
    private boolean isFirstTime = true;

    public static Intent createIntent(HotelSnippet hotelSnippet, HotelSnippet hotelSnippetDetails, HotelListRequest request, int tabId, Context context) {
        Intent intent = new Intent(context, HotelDetailsActivity.class);
        intent.putExtra(EXTRA_SNIPPET, hotelSnippet);
        intent.putExtra(EXTRA_SNIPPET_DETAILS, hotelSnippetDetails);
        intent.putExtra(EXTRA_TABID, tabId);
        intent.putExtra(EXTRA_REQUEST, request);
        return intent;
    }

    @Override
    protected void onCreateTabFragments(ViewPagerAdapter adapter, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mHotelSnippet = savedInstanceState.getParcelable(EXTRA_SNIPPET);
            mHotelSnippetDetails = savedInstanceState.getParcelable(EXTRA_SNIPPET_DETAILS);
        } else {
            mHotelSnippet = getIntent().getParcelableExtra(EXTRA_SNIPPET);
            mHotelSnippetDetails = getIntent().getParcelableExtra(EXTRA_SNIPPET_DETAILS);
        }

        setTitle(mHotelSnippet.getName());
        ButterKnife.bind(this);

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("menu_map");
        if (mSupportMapFragment == null) {
            mSupportMapFragment = HotelsMapFragment.newInstance();
        }
        mSupportMapFragment.getMapAsync(this);

        if (mHotelSnippetDetails != null && !mHotelSnippetDetails.hasRates()) {
            mAllRoomsButton.setVisibility(View.GONE);
        }
        mPoiMarker = new PoiMarker(this);
        adapter.addFragment(HotelFacilitiesFragment.newInstance(mHotelSnippet), getString(R.string.cps_details), "fragment_facilities");
        adapter.addFragment(HotelReviewsFragment.newInstance(mHotelSnippet), getString(R.string.cps_reviews), "fragment_reviews");
        adapter.addFragment(mSupportMapFragment, getString(R.string.cps_map), MAP);
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_hoteldetails);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        super.onTabSelected(tab);
        if (tab.getPosition() == MAP_POSITION) {
            mShowLandmarks.setVisibility(View.VISIBLE);
        } else {
            mShowLandmarks.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        super.onTabReselected(tab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hotel_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mHotelSnippet.getLocation() != null) {
            googleMap.clear();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_selected));
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(mHotelSnippet.getLocation().lat, mHotelSnippet.getLocation().lon)).icon(icon).snippet(HOTEL_MARKER);

            googleMap.addMarker(options);
            if (isFirstTime) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mHotelSnippet.getLocation().lat, mHotelSnippet.getLocation().lon), 13));
                isFirstTime = false;
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            googleMap.getUiSettings().setAllGesturesEnabled(true);

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }

            // Turns traffic layer on
            googleMap.setTrafficEnabled(false);

            // Enables indoor maps
            googleMap.setIndoorEnabled(true);

            // Turns on 3D buildings
            googleMap.setBuildingsEnabled(true);

            // Show Zoom buttons
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.poi_info_window, null, false);
                    if (marker.getTitle() != null) {
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
                    }
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
            if (mDrawLandmarksInMeters != 0) {
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(mHotelSnippet.getLocation().lat, mHotelSnippet.getLocation().lon))   //set center
                        .radius(mDrawLandmarksInMeters)   //set radius in meters
                        .fillColor(getResources().getColor(R.color.transparent_bright_black))
                        .strokeColor(Color.TRANSPARENT)
                        .strokeWidth(0);
                googleMap.addCircle(circleOptions);
                if (mPoiList == null) {
                    Toast.makeText(getBaseContext(), R.string.nothing_to_show_in_this_area, Toast.LENGTH_LONG).show();
                } else {
                    Integer id = 0;
                    for (Poi poi : mPoiList) {
                        googleMap.addMarker(mPoiMarker.create(id, poi, PoiMarker.STATUS_UNSEEN));
                        id++;
                    }
                }
                googleMap.setOnMarkerClickListener(this);
            }
        }
    }

    @OnClick(R.id.available_rooms_button)
    public void onClickAvailableRooms(Button button) {
        showRoomsList(mHotelSnippet.geId());
    }


    @OnClick(R.id.show_landmarks)
    public void onClickShowLandmarks(Button button) {
        if (mDrawLandmarksInMeters != 0) {
            mDrawLandmarksInMeters = 0;
            button.setText(R.string.show_landmarks);
            mapAsync(null);
        } else {
            mDrawLandmarksInMeters = mDrawLandmarksInMeters + LANDMARKS_SIZE;
            button.setText(R.string.remove_landmarks);
            CoreInterface.Service mCoreInterface = CoreInterface.create(getApplicationContext());

            Call<List<Poi>> call = mCoreInterface.poiList(String.valueOf(mHotelSnippet.getLocation().lon), String.valueOf(mHotelSnippet.getLocation().lat), String.valueOf(mDrawLandmarksInMeters));
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


    private void mapAsync(List<Poi> poiList) {
        mPoiList = poiList;
        if (poiList != null) {
            for (int i = 0; i < poiList.size(); i++) {
                Poi poi = poiList.get(i);
                if (poi.type_id == PoiMarker.TYPE_DISTRICT || poi.type_id == PoiMarker.TYPE_AREA) {
                    poiList.remove(i);
                }
            }
        }
        mSupportMapFragment.getMapAsync(this);
    }

    public void showRoomsList(int hotelId) {
        startActivity(RoomListActivity.createIntent(hotelId, getHotelsRequest(), this));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_SNIPPET, mHotelSnippet);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
