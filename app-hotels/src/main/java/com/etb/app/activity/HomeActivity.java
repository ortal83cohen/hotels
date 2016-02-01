package com.etb.app.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.Type;
import com.easytobook.api.utils.RequestUtils;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.anim.BlurAnimation;
import com.etb.app.core.CoreInterface;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.fragment.HomeFragment;
import com.etb.app.model.HotelListRequest;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.utils.AppLog;
import com.etb.app.widget.IntentIntegrator;
import com.etb.app.widget.IntentResult;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.squareup.okhttp.ResponseBody;

import butterknife.ButterKnife;
import retrofit.Response;

//import com.newrelic.agent.android.NewRelic;

public class HomeActivity extends BaseActivity implements HomeFragment.Listener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Tracker mTracker;

    public static Intent createIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected boolean requiresRequest() {
        return false;
    }

    public static final int REQUEST_CHECK_SETTINGS = 1;
    private static final String FRAGMENT_HOME = "loc_chooser";
    protected GoogleApiClient mGoogleApiClient;
    private CoreInterface.Service mCoreInterface;
    private RetrofitCallback<String> mResultsCallback = new RetrofitCallback<String>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            String number = "tel:+31 20 531 33 00";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
            startActivity(callIntent);
        }

        @Override
        protected void success(String phoneNumber, Response<String> response) {
            String number = "tel:" + phoneNumber;
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
            startActivity(callIntent);
        }

    };

    @Override
    public Uri getReferrer() {
        String referrer = getIntent().getStringExtra("android.intent.extra.REFERRER_NAME");
        if (referrer != null) {
            try {
                return Uri.parse(referrer);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri referrer = getReferrer();
        if (referrer != null) {
            AppLog.d(referrer.toString());
//            Intent intent = new Intent(this, RouteActivity.class);
//            intent.setData(referrer);
//            startActivity(intent);
        }
        ButterKnife.bind(this);
        AnalyticsCalls.get().register(getApplicationContext());
        App.provide(this).facebook().initialize();
        mCoreInterface = CoreInterface.create(getApplicationContext());

        if (getHotelsRequest() == null) {
            setHotelsRequest(App.provide(this).createHotelsRequest());
        }

        mToolbar.showLogo();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (savedInstanceState == null) {
            HotelListRequest request = getHotelsRequest();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.newInstance(request, false, true),
                            FRAGMENT_HOME)
                    .commit();
        }
        animateBackground();

        AnalyticsCalls.get().trackLanding();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSearchBox();
    }

    private void updateSearchBox() {
        SearchRequest lastSearchRequest = App.provide(this).getLastSearchRequest();
        if (lastSearchRequest != null) {
            HotelListRequest request = getHotelsRequest();
            RequestUtils.apply(request, lastSearchRequest.getDateRange(), lastSearchRequest.getNumberOfPersons(), lastSearchRequest.getNumberOfRooms());
            request.setType(lastSearchRequest.getType());
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME);
            if (homeFragment != null) {
                homeFragment.init(request);
            }
        }
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_customer:
                UserPreferences userPrefs = getUserPrefs();
                mCoreInterface.customerServicePhone(userPrefs.getCountryCode().toLowerCase()).enqueue(mResultsCallback);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void animateBackground() {
        final View content = getWindow().getDecorView();
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Drawable background = getResources().getDrawable(R.drawable.img_background);
                ValueAnimator blurAnimation = new BlurAnimation().blur(content, background, 4f);
                blurAnimation.setStartDelay(100);
                blurAnimation.start();
                content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME);
        if (homeFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                getString(R.string.could_not_connect_to_google) + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void startSearch(Type locationType, DateRange dates, int persons, int rooms) {
        HotelListRequest request = getHotelsRequest();
        request.removeFilter();
        request.setType(locationType);
        RequestUtils.apply(request, dates, persons, rooms);

        Intent myIntent = HotelListActivity.createIntent(request, this);
        startActivity(myIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle("");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK) {
                    HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME);
                    if (fragment != null) {
                        fragment.onLocationAvailable();
                    }
                }
                break;
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                //check we have a valid result
                if (scanningResult != null) {
                    //get content from Intent Result
                    String scanContent = scanningResult.getContents();
                    //get format name of data scanned
                    String scanFormat = scanningResult.getFormatName();
                    //output to UI
                    if (scanFormat != null) {
                        if (scanFormat.equals("QR_CODE")) {
                            Intent intent = new Intent(this, RouteActivity.class);
                            intent.setData(Uri.parse(scanContent));
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, R.string.qr_not_match_warning, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        }
    }


}
