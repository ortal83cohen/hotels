package com.etb.app.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.Type;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.activity.HomeActivity;
import com.etb.app.adapter.PlaceAutocompleteAdapter;
import com.etb.app.anim.AnimatorCollection;
import com.etb.app.anim.ResizeAnimator;
import com.etb.app.anim.RevealAnimatorCompat;
import com.etb.app.model.CurrentLocation;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.Location;
import com.etb.app.model.LocationWithTitle;
import com.etb.app.model.MapSelectedViewPort;
import com.etb.app.model.ViewPort;
import com.etb.app.provider.SearchHistory;
import com.etb.app.randerscript.BlurBuilder;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.TextWatcherAdapter;
import com.etb.app.widget.BaseDatePicker;
import com.etb.app.widget.NumberBoxView;
import com.etb.app.widget.NumberPicker;
import com.etb.app.widget.SmallDatePicker;
import com.facebook.device.yearclass.YearClass;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author alex
 * @date 2015-05-17
 */
public class HomeFragment extends BaseFragment implements BaseDatePicker.OnRangeChangedListener, View.OnTouchListener, NumberPicker.OnSelectListener, View.OnClickListener {
    private static final LatLngBounds BOUNDS_MAP = new LatLngBounds.Builder()
            .include(new LatLng(85, -180)) // top left corner of map
            .include(new LatLng(-85, 180))  // bottom right corner
            .build();


    private static final int REQUEST_PERMISSION_LOCATION = 2;

    private static final String ARG_SHOW_LOCATION = "show_location";
    private static final String LAST_LOCATION = "last_location";
    private static final String LAST_VIEW_PORT = "last_view_port";

    private static final String ARG_IS_LIGHT_BOX = "is_light_box";
    private static final String ARG_REQUEST = "request";

    private static final int POSITION_UNTOUCHED = -1;
    private static final int POSITION_UNSELECTED = -2;
    private final int[] mTouchLoc = new int[2];
    private final int[] mViewLoc = new int[2];
    @Bind(R.id.autoCompleteTextView_location)
    AutoCompleteTextView mAutocompleteView;
    @Bind(R.id.autoCompleteTextView_clear)
    ImageButton mAutocompleteViewClear;
    @Bind(R.id.instructions)
    TextView mInstructions;
    @Bind(R.id.number_persons_selector)
    NumberPicker mNumberPersonsSelector;
    @Bind(R.id.number_rooms_selector)
    NumberPicker mNumberRoomsSelector;
    @Bind(R.id.rooms_button)
    NumberBoxView mRoomsButton;
    @Bind(R.id.check_in_button)
    NumberBoxView mCheckInButton;
    @Bind(R.id.check_out_button)
    NumberBoxView mCheckOutButton;
    @Bind(R.id.persons_button)
    NumberBoxView mPersonsButton;
    @Bind(R.id.stay_datepicker)
    SmallDatePicker mDatePicker;
    @Bind(R.id.top_box)
    LinearLayout mTopBox;
    @Bind(R.id.background)
    View mBackground;
    @Bind(R.id.group_buttons_holder)
    LinearLayout mGroupButtonsHolder;
    @Bind(R.id.search_hotels)
    Button mSearchHotelsButton;
    private PlaceAutocompleteAdapter mAdapter;
    private int mSelectedPosition = POSITION_UNTOUCHED;
    private GoogleApiClient mGoogleApiClient;
    private Listener mListener;

    private Type mLastLocation;

    private LocationRequest mLocationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) // (~100m "block" accuracy)
            .setNumUpdates(1);
    private SimpleDateFormat mDayFormatter = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat mMonthFormatter = new SimpleDateFormat("MMM", Locale.getDefault());
    private boolean mShowLocation = true;
    private boolean mIsLightBox = false;
    private int mTopBoxHeight = -1;
    private int mInstructionsHeight = -1;
    private View mSelectPanelView;
    private DateRange mSelectedRange;
    private View mBlurContentView;
    private BlurBuilder mBlurBuilder;
    private boolean mAutocompleteOnFocus = false;
    private boolean mSearchButtonClicked = false;
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            if (getActivity() == null) {
                return;
            }
            // Get the Place object from the buffer.
            if (places.getCount() == 0) {
                Toast.makeText(getActivity(), "No result for " + mAutocompleteView.getText(), Toast.LENGTH_SHORT).show();
                AppLog.e("Google Autocomplete Api error for: " + mAutocompleteView.getText());
                return;
            }
            Place place = places.get(0);
            SearchHistory.insert(place, mSelectedRange, mPersonsButton.getValue(), mRoomsButton.getValue(), getActivity());
            Type lastLocation = updateLastLocation(place.getName().toString(), place.getLatLng(), place.getViewport());
            startSearch(lastLocation);
            places.release();
        }
    };
    private TextWatcher mAutocompleteTextWatcher = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mAutocompleteView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.destination_active, 0, R.color.white, 0);
            mSelectedPosition = POSITION_UNSELECTED;
        }
    };

    private Type updateLastLocation(String title, LatLng loc, LatLngBounds viewport) {
        if (viewport == null) {
            mLastLocation = new Location(title, loc);
        } else {
            mLastLocation = new ViewPort(title, viewport);
        }

        return mLastLocation;
    }

    public static HomeFragment newInstance(HotelListRequest request, boolean isLightBox, boolean showLocation) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_REQUEST, request);
        args.putBoolean(ARG_IS_LIGHT_BOX, isLightBox);
        args.putBoolean(ARG_SHOW_LOCATION, showLocation);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final Activity activity = getActivity();
        ButterKnife.bind(this, view);
        setupAutocomplete();
        if (savedInstanceState == null) {
            mGroupButtonsHolder.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
            mGroupButtonsHolder.animate();
        }

        mIsLightBox = getArguments().getBoolean(ARG_IS_LIGHT_BOX);
        mShowLocation = getArguments().getBoolean(ARG_SHOW_LOCATION);

        if (!mShowLocation) {
            mSearchHotelsButton.setText(R.string.select_dates);
            mAutocompleteView.setVisibility(View.GONE);
            mAutocompleteViewClear.setVisibility(View.GONE);
        }
        if (mIsLightBox) {
            setupLightBoxBackground();
        } else {
            mInstructions.setVisibility(View.VISIBLE);
        }
        mBackground.setOnTouchListener(this);

        //hide selector
        mDatePicker.setVisibility(View.INVISIBLE);

        mDatePicker.setOnRangeChangedListener(this);

        mNumberPersonsSelector.setVisibility(View.INVISIBLE);
        mNumberPersonsSelector.setListener(this);
        mNumberPersonsSelector.setOnTouchListener(this);
        mNumberRoomsSelector.setVisibility(View.INVISIBLE);
        mNumberRoomsSelector.setListener(this);
        mNumberRoomsSelector.setOnTouchListener(this);

        Resources r = getResources();

        mCheckInButton.setOnTouchListener(this);
        mCheckInButton.setOnClickListener(this);
        mCheckOutButton.setOnTouchListener(this);
        mCheckOutButton.setOnClickListener(this);
        mPersonsButton.setOnTouchListener(this);
        mPersonsButton.setOnClickListener(this);
        mRoomsButton.setOnTouchListener(this);
        mRoomsButton.setOnClickListener(this);

        ViewCompat.setElevation(mAutocompleteView, r.getDimension(R.dimen.home_view_elevation));
        ViewCompat.setElevation(mAutocompleteViewClear, 50);
        ViewCompat.setElevation(mCheckInButton, r.getDimension(R.dimen.home_view_elevation));
        ViewCompat.setElevation(mCheckOutButton, r.getDimension(R.dimen.home_view_elevation));
        ViewCompat.setElevation(mRoomsButton, r.getDimension(R.dimen.home_view_elevation));
        ViewCompat.setElevation(mPersonsButton, r.getDimension(R.dimen.home_view_elevation));

        SearchRequest request = getArguments().getParcelable(ARG_REQUEST);
        init(request);

        return view;
    }

    public void init(SearchRequest request) {
        mSelectedRange = request.getDateRange();
        if (mSelectedRange == null) {
            mSelectedRange = DateRange.getInstance();
        }
        mDatePicker.setSelectedRange(mSelectedRange);
        mDatePicker.goTo(mSelectedRange.from);

        updateViews(mSelectedRange, request.getNumberOfPersons(), request.getNumberOfRooms());
        mLastLocation = request.getType();
        updateLocationText(mLastLocation);
    }

    private void setupAutocomplete() {
        mAutocompleteView.setThreshold(0);
        mAutocompleteViewClear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutocompleteView.setText("");
                return true;
            }
        });

        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mAutocompleteOnFocus) {
                    animateFocusAutocomplete();
                }
                return false;
            }
        });
        mAutocompleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setupHistory(mAutocompleteView.getText());
                mAutocompleteView.showDropDown();
            }
        });
        mAutocompleteView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.length() == 0) {
                    mAutocompleteViewClear.setVisibility(View.GONE);
                } else {
                    mAutocompleteViewClear.setVisibility(View.VISIBLE);
                }
            }
        });
        mAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                PlaceAutocompleteAdapter.PlaceItem item = mAdapter.getItem(position);
                if (item instanceof PlaceAutocompleteAdapter.PlaceCurrentLocation) {
                    mAutocompleteView.setText("");
                } else if (item instanceof PlaceAutocompleteAdapter.PlaceAutocomplete) {
                } else if (item instanceof PlaceAutocompleteAdapter.PlaceHistory) {
                    PlaceAutocompleteAdapter.PlaceHistory historyItem = (PlaceAutocompleteAdapter.PlaceHistory) item;
                    mSelectedRange = historyItem.getDateRange();
                    mDatePicker.setSelectedRange(mSelectedRange);
                    updateViews(mSelectedRange, historyItem.getNumberGuests(), historyItem.getNumberRooms());
                }
                mAutocompleteView.setCompoundDrawablesRelativeWithIntrinsicBounds(item.getDrawable(), 0, R.color.white, 0);
                hideKeyboard();
            }
        });
        mAutocompleteView.addTextChangedListener(mAutocompleteTextWatcher);
    }

    private void setupLightBoxBackground() {

        int deviceClass = App.provide(getActivity()).getDeviceClass();
        if (deviceClass >= YearClass.CLASS_2014) {
            mBlurBuilder = new BlurBuilder(getActivity());
            mBlurContentView = getActivity().findViewById(R.id.blur_view);
        }

        Resources r = getResources();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) r.getDimension(R.dimen.default_padding), (int) r.getDimension(R.dimen.top_margin_home_fragment), (int) r.getDimension(R.dimen.default_padding), 0);
        mGroupButtonsHolder.setLayoutParams(lp);
        mInstructions.setVisibility(View.GONE);
        redrawBlurBackground();
    }

    @Override
    public void onDestroyView() {
        // delete references to activity
        if (mBlurContentView != null) {
            mBlurBuilder = null;
            mBlurContentView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onRangeChanged(int state, DateRange range) {
        updateDatesViews(range);
        if (state == DateRange.RANGE_SET_FROM) {
            highlightButton(R.id.check_out_button);
            mDatePicker.getSelectedRange().setState(DateRange.RANGE_SET_TO);
        } else if (state == DateRange.RANGE_SET_TO) {
            highlightButton(0);
            animatePanelHide(mDatePicker);
        }
        mSelectedRange = range;
    }

    private void updateViews(DateRange range, int persons, int rooms) {
        updateDatesViews(range);
        updatePersonsAndRoomsViews(persons, rooms);
    }

    private void updateDatesViews(DateRange range) {
        mCheckInButton.setValue(mDayFormatter.format(range.from.getTime()));
        mCheckInButton.setSubtitle(mMonthFormatter.format(range.from.getTime()));

        mCheckOutButton.setValue(mDayFormatter.format(range.to.getTime()));
        mCheckOutButton.setSubtitle(mMonthFormatter.format(range.to.getTime()));
    }

    private void updatePersonsAndRoomsViews(int persons, int rooms) {
        mNumberPersonsSelector.setSelected(persons);
        mNumberRoomsSelector.setSelected(rooms);

        Resources r = getResources();
        mRoomsButton.setValue(rooms);
        mRoomsButton.setTitle(r.getQuantityString(R.plurals.rooms_caps, rooms));

        mPersonsButton.setValue(persons);
        mPersonsButton.setTitle(r.getQuantityString(R.plurals.persons_caps, persons));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mBackground == v) {
            if (mIsLightBox) {
                ((BaseActivity) getActivity()).remove(this);
            } else {
                onBackPressed();
            }
            return true;
        }
        if (v instanceof com.etb.app.widget.NumberPicker) {
            return true;
        }
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                v.getLocationOnScreen(mTouchLoc);
                mTouchLoc[0] = mTouchLoc[0] + (int) event.getX();
                mTouchLoc[1] = mTouchLoc[1] + (int) event.getY();
                return false;
            default:
        }
        return false;
    }

    @Override
    public void onNumberSelect(NumberPicker view, Button button, int number) {
        if (view.getId() == R.id.number_rooms_selector) {
            mRoomsButton.setValue(number);
            mRoomsButton.setTitle(getResources().getQuantityString(R.plurals.rooms_caps, number));
            Toast.makeText(getActivity(), getResources().getString(R.string.note_guest_per_room, number, mPersonsButton.getValue()), Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.number_persons_selector) {
            mPersonsButton.setValue(number);
            mPersonsButton.setTitle(getResources().getQuantityString(R.plurals.persons_caps, number));
        }
        animatePanelHide(view);
    }

    public boolean onBackPressed() {
        NavigationFragment navigationDrawer = (NavigationFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        if (navigationDrawer != null && navigationDrawer.isVisible()) {
            navigationDrawer.closeDrawer();
            return true;
        }
        if (mSelectPanelView != null) {
            mSelectPanelView.getLocationOnScreen(mTouchLoc);
            mTouchLoc[0] = mTouchLoc[0] + mSelectPanelView.getWidth() / 2;
            highlightButton(0);
            animatePanelHide(mSelectPanelView);
            return true;
        }
        if (mInstructions.getVisibility() == View.GONE) {
            animateUnFocusAutocomplete();
            return true;
        }
        return false;
    }

    private void redrawBlurBackground() {
        if (mBlurBuilder == null) {
            mBackground.setBackgroundResource(R.color.transparent_black);
        } else {
            Bitmap bgImage = mBlurBuilder.blur(mBlurContentView, R.color.transparent_black);
            mBackground.setBackground(new BitmapDrawable(getResources(), bgImage));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rooms_button) {
            onRoomsButtonClick();
        } else if (id == R.id.persons_button) {
            onGuestButtonClick();
        } else if (id == R.id.check_in_button) {
            onCheckInButtonClick();
        } else if (id == R.id.check_out_button) {
            onCheckoutButtonClick();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
        if (mShowLocation) {
            if (savedInstanceState != null) {
                mLastLocation = savedInstanceState.getParcelable(LAST_LOCATION);
            }

            mGoogleApiClient = mListener.getGoogleApiClient();

            // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
            // the entire world.

            mAdapter = new PlaceAutocompleteAdapter(getActivity(), R.layout.autocomplite_list_item, mGoogleApiClient, BOUNDS_MAP);
            mAutocompleteView.setAdapter(mAdapter);

            if (mLastLocation != null) {
                updateLocationText(mLastLocation);
            } else {
                SearchRequest request = getArguments().getParcelable(ARG_REQUEST);
                updateLocationText(request.getType());
            }

        }
    }

    private void updateLocationText(Type locationType) {
        if (locationType instanceof CurrentLocation) {
            mAutocompleteView.setText("");
        } else if (locationType instanceof MapSelectedViewPort) {
            mAutocompleteView.setText(((MapSelectedViewPort) locationType).getLastSearch());
            if (mSelectedPosition == POSITION_UNTOUCHED) {
                mSelectedPosition = POSITION_UNSELECTED;
            }
        } else if (locationType instanceof LocationWithTitle) {
            String title = ((LocationWithTitle) locationType).getTitle();
            mAutocompleteView.setText(title == null ? "" : title);
            if (mSelectedPosition == POSITION_UNTOUCHED && !TextUtils.isEmpty(title)) {
                mSelectedPosition = POSITION_UNSELECTED;
            }
        }
    }

    private void checkPermissionAndstartSearchInCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            startSearchInCurrentLocation();
        }
    }

    public void onLocationAvailable() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {
                        CurrentLocation lastLocation = new CurrentLocation();
                        lastLocation.setLatLng(location.getLatitude(), location.getLongitude());
                        mLastLocation = lastLocation;
                        startSearch(lastLocation);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSearchInCurrentLocation();
            }
        }
    }

    private void onCheckInButtonClick() {

        // Hide datepicker
        if (mDatePicker.getSelectedRange().getState() == DateRange.RANGE_SET_FROM &&
                mDatePicker.getVisibility() == View.VISIBLE) {
            highlightButton(0);
            animatePanelHide(mDatePicker);
        } else {
            highlightButton(R.id.check_in_button);
            mDatePicker.getSelectedRange().setState(DateRange.RANGE_SET_FROM);
            if (mDatePicker.getVisibility() != View.VISIBLE) {
                mDatePicker.goTo(mSelectedRange.from);
                mNumberRoomsSelector.setVisibility(View.INVISIBLE);
                mNumberPersonsSelector.setVisibility(View.INVISIBLE);

                animatePanelShow(mDatePicker);
            }
        }

    }

    private void onCheckoutButtonClick() {

        // Hide datepicker
        if (mDatePicker.getSelectedRange().getState() == DateRange.RANGE_SET_TO &&
                mDatePicker.getVisibility() == View.VISIBLE) {
            highlightButton(0);
            animatePanelHide(mDatePicker);
        } else {
            highlightButton(R.id.check_out_button);
            mDatePicker.getSelectedRange().setState(DateRange.RANGE_SET_TO);
            if (mDatePicker.getVisibility() != View.VISIBLE) {
                mDatePicker.goTo(mSelectedRange.to);
                mNumberRoomsSelector.setVisibility(View.INVISIBLE);
                mNumberPersonsSelector.setVisibility(View.INVISIBLE);
                animatePanelShow(mDatePicker);
            }
        }
    }

    private void highlightButton(@IdRes int buttonId) {
        if (buttonId == R.id.rooms_button) {
            mRoomsButton.setSelected(true);
            mPersonsButton.setSelected(false);
            mCheckInButton.setSelected(false);
            mCheckOutButton.setSelected(false);
        } else if (buttonId == R.id.persons_button) {
            mRoomsButton.setSelected(false);
            mPersonsButton.setSelected(true);
            mCheckInButton.setSelected(false);
            mCheckOutButton.setSelected(false);
        } else if (buttonId == R.id.check_in_button) {
            mRoomsButton.setSelected(false);
            mPersonsButton.setSelected(false);
            mCheckInButton.setSelected(true);
            mCheckOutButton.setSelected(false);
        } else if (buttonId == R.id.check_out_button) {
            mRoomsButton.setSelected(false);
            mPersonsButton.setSelected(false);
            mCheckInButton.setSelected(false);
            mCheckOutButton.setSelected(true);
        } else if (buttonId == R.id.background) {
            mRoomsButton.setSelected(false);
            mPersonsButton.setSelected(false);
            mCheckInButton.setSelected(false);
            mCheckOutButton.setSelected(false);
        } else {
            mRoomsButton.setSelected(false);
            mPersonsButton.setSelected(false);
            mCheckInButton.setSelected(false);
            mCheckOutButton.setSelected(false);

        }
    }

    private void animatePanelShow(final View panelView) {
        AnimatorCollection collection = new AnimatorCollection();
        if (mTopBoxHeight == -1) {
            mTopBoxHeight = mTopBox.getMeasuredHeight();
            mInstructionsHeight = mInstructions.getMeasuredHeight();
        }
        collection.add(ResizeAnimator.height(mTopBox.getMeasuredHeight(), 0, mTopBox, 300));
        collection.add(revealAnimatorShow(panelView));
        AnimatorSet set = collection.sequential();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTopBox.setVisibility(View.GONE);
            }
        });
        set.start();
        mSelectPanelView = panelView;
    }

    private void animatePanelHide(final View panelView) {
        if (mInstructions.getVisibility() == View.GONE) {
            animateUnFocusAutocomplete();
        }
        mTopBox.setVisibility(View.VISIBLE);
        AnimatorCollection collection = new AnimatorCollection();
        Animator pickerHide = revealAnimatorHide(panelView);
        collection.add(pickerHide);
        collection.add(ResizeAnimator.height(0, mTopBoxHeight, mTopBox, 300));
        AnimatorSet set = collection.sequential();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTopBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        });
        set.start();
        mSelectPanelView = null;
    }

    private void animateUnFocusAutocomplete() {
        mAutocompleteOnFocus = false;
        mInstructions.setVisibility(View.VISIBLE);
        AnimatorCollection collection = new AnimatorCollection();
        collection.add(ResizeAnimator.height(0, mInstructionsHeight, mInstructions, 100));
        collection.add(ResizeAnimator.margin(0, getResources().getDimensionPixelSize(R.dimen.home_instructions_margin), mInstructions, 100));
        AnimatorSet set = collection.sequential();
        set.start();
    }

    private void animateFocusAutocomplete() {
        mAutocompleteOnFocus = true;
        if (mTopBoxHeight == -1) {
            mTopBoxHeight = mTopBox.getMeasuredHeight();
            mInstructionsHeight = mInstructions.getMeasuredHeight();
        }
        AnimatorCollection collection = new AnimatorCollection();
        collection.add(ResizeAnimator.height(mInstructionsHeight, 0, mInstructions, 100));
        collection.add(ResizeAnimator.margin(getResources().getDimensionPixelSize(R.dimen.home_instructions_margin), 0, mInstructions, 100));
        AnimatorSet set = collection.sequential();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInstructions.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    private Animator revealAnimatorHide(final View view) {
        view.getLocationOnScreen(mViewLoc);
        int x = mTouchLoc[0] - mViewLoc[0];
        int y = mTouchLoc[1] - mViewLoc[1];
        return RevealAnimatorCompat.hide(view, x, y, 0);
    }

    private Animator revealAnimatorShow(final View view) {
        view.getLocationOnScreen(mViewLoc);
        int x = mTouchLoc[0] - mViewLoc[0];
        int y = mTouchLoc[1] - mViewLoc[1];
        return RevealAnimatorCompat.show(view, x, y, 0);
    }

    public void onGuestButtonClick() {
        mNumberRoomsSelector.setVisibility(View.INVISIBLE);
        mDatePicker.setVisibility(View.INVISIBLE);

        if (mNumberPersonsSelector.getVisibility() == View.VISIBLE) {
            //highlight
            highlightButton(0);
            animatePanelHide(mNumberPersonsSelector);
        } else {
            highlightButton(R.id.persons_button);
            animatePanelShow(mNumberPersonsSelector);
        }

    }

    private void onRoomsButtonClick() {
        mNumberPersonsSelector.setVisibility(View.INVISIBLE);
        mDatePicker.setVisibility(View.INVISIBLE);

        if (mNumberRoomsSelector.getVisibility() == View.VISIBLE) {
            //highlight
            highlightButton(0);
            animatePanelHide(mNumberRoomsSelector);
        } else {
            highlightButton(R.id.rooms_button);
            animatePanelShow(mNumberRoomsSelector);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ARG_IS_LIGHT_BOX, mIsLightBox);
        outState.putBoolean(ARG_SHOW_LOCATION, mShowLocation);
        if (mLastLocation instanceof Parcelable) {
            outState.putParcelable(LAST_LOCATION, (Parcelable) mLastLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.search_hotels)
    public void onSearchHotelsClick(View button) {
        if (mShowLocation) {
            onSearchHotelsWithLocation();
        } else {
            onSearchHotelsWithoutLocation();
        }

    }

    private void onSearchHotelsWithoutLocation() {
        startSearch(null);
    }

    private void onSearchHotelsWithLocation() {
        try {
            Calendar yesterday = Calendar.getInstance(Locale.getDefault());
            yesterday.add(Calendar.DAY_OF_MONTH, -1);
            if (mSelectedRange.from.before(yesterday)) {
                Toast.makeText(getActivity(), R.string.check_in_less_than_today_worning, Toast.LENGTH_LONG).show();
            } else {
                PlaceAutocompleteAdapter.PlaceItem item = null;
                if (mSelectedPosition == POSITION_UNTOUCHED) {
                    checkPermissionAndstartSearchInCurrentLocation();
                    return;
                } else if (mSelectedPosition == POSITION_UNSELECTED) {
                    item = mAdapter.getFirstSearchItem();
                } else {
                    item = mAdapter.getItem(mSelectedPosition);
                }
                if (item == null || item instanceof PlaceAutocompleteAdapter.PlaceCurrentLocation) {
                    checkPermissionAndstartSearchInCurrentLocation();
                } else if (item instanceof PlaceAutocompleteAdapter.PlaceAutocomplete) {
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                            .getPlaceById(mGoogleApiClient, ((PlaceAutocompleteAdapter.PlaceAutocomplete) item).getPlaceId());
                    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                } else if (item instanceof PlaceAutocompleteAdapter.PlaceHistory) {
                    PlaceAutocompleteAdapter.PlaceHistory historyItem = (PlaceAutocompleteAdapter.PlaceHistory) item;
                    Type locationType = updateLastLocation(item.toString(), historyItem.getLatLng(), historyItem.getLatLngBounds());
                    startSearch(locationType);
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Toast.makeText(getActivity(), R.string.choose_location, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        mSearchButtonClicked = false;
        super.onResume();
    }

    private void startSearchInCurrentLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        onLocationAvailable();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), HomeActivity.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        onLocationAvailable();
                        break;
                }
            }
        });
    }

    private void hideKeyboard() {
        if (mInstructions.getVisibility() == View.GONE) {
            animateUnFocusAutocomplete();
        }
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void startSearch(Type locationType) {
        if (!mSearchButtonClicked) {
            mSearchButtonClicked = true;
            mListener.startSearch(locationType, mSelectedRange, mPersonsButton.getValue(), mRoomsButton.getValue());
        }
    }


    public interface Listener {
        GoogleApiClient getGoogleApiClient();

        void startSearch(Type locationType, DateRange dates, int persons, int rooms);
    }


}
