package com.etb.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easytobook.api.EtbApi;
import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.ResultsResponse;
import com.easytobook.api.model.SearchRequest;
import com.easytobook.api.model.search.ListType;
import com.easytobook.api.utils.RequestUtils;
import com.etb.app.App;
import com.etb.app.HotelsApplication;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.adapter.FavoritesHotelListAdapter;
import com.etb.app.adapter.HotelViewHolder;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.events.Events;
import com.etb.app.events.SearchResultsEvent;
import com.etb.app.model.HotelListRequest;
import com.etb.app.provider.DbContract;
import com.etb.app.provider.LikedHotels;
import com.etb.app.utils.AppLog;
import com.etb.app.widget.recyclerview.EndlessRecyclerView;
import com.squareup.okhttp.ResponseBody;
import com.squareup.otto.Subscribe;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;

/**
 * @author alex
 * @date 2015-05-17
 */

public class FavoritesListFragment extends BaseFragment {


    private static final String ARG_CITY = "city";
    private static final String ARG_COUNTRY = "country";
    private static final String ARG_COUNT = "count";
    private static final String ARG_REQUEST = "date_range";
    @Bind(android.R.id.list)
    EndlessRecyclerView mRecyclerView;
    @Bind(R.id.hotel_list_no_result)
    LinearLayout mNoResult;
    @Bind(R.id.loader_image)
    ImageView mLoaderImage;
    @Bind(R.id.loader_text)
    TextView mLoaderText;
    @Bind(R.id.modify_preferences)
    Button mModifyPreferences;
    @Bind(R.id.reset_filters)
    Button mResetFilters;

    EtbApi mEtbApi;

    private LinearLayoutManager mLayoutManager;
    private FavoritesHotelListAdapter mAdapter;
    private HotelListRequest mHotelsRequest;
    private String mCity;
    private String mCountry;
    private HotelViewHolder.Listener mListener;


    private RetrofitCallback<ResultsResponse> mResultsCallback = new RetrofitCallback<ResultsResponse>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            Events.post(new SearchResultsEvent(true, 0));
        }

        @Override
        protected void success(ResultsResponse apiResponse, Response<ResultsResponse> response) {
            mLoaderImage.setVisibility(View.GONE);
            mLoaderText.setVisibility(View.GONE);
            if (mRecyclerView == null) {
                return;
            }
            if (apiResponse.accommodations == null || apiResponse.accommodations.isEmpty()) {
                mNoResult.setVisibility(View.VISIBLE);
            }
            if (apiResponse.accommodations == null || apiResponse.accommodations.isEmpty() || apiResponse.accommodations.size() != EtbApi.LIMIT) {
                mRecyclerView.setHasMoreData(false);
            }
            // Fragment destroyed
            if (mAdapter == null) {
                return;
            }
            mAdapter.addHotels(apiResponse.accommodations, !mHotelsRequest.isDatesRequest());
            Events.post(new SearchResultsEvent(apiResponse.meta.totalNr));
        }

    };

    public static FavoritesListFragment newInstance(String city, String country, String count, HotelListRequest request) {
        FavoritesListFragment fragment = new FavoritesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CITY, city);
        bundle.putString(ARG_COUNTRY, country);
        bundle.putString(ARG_COUNT, count);
        bundle.putParcelable(ARG_REQUEST, request);
        fragment.setArguments(bundle);
        return fragment;
    }

    //    @OnClick(R.id.delete_list)
    public void deleteList() {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Cursor cursor = getActivity().getContentResolver().query(DbContract.LikedHotels.CONTENT_URI.buildUpon().
                                appendQueryParameter("where", DbContract.LikedHotelsColumns.CITY + "='" + mCity + "' AND " + DbContract.LikedHotelsColumns.COUNTRY + "='" + mCountry + "'").build(), null, null, null, null);

                        if (cursor != null && cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            do {
                                getActivity().getContentResolver().delete(DbContract.LikedHotels.CONTENT_URI.buildUpon().appendPath(cursor.getString(cursor.getColumnIndex("_id"))).build(), null, null);
                            } while (cursor.moveToNext());
                            cursor.close();
                        }
                        getActivity().onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mResultsCallback.attach(context);
        try {
            mListener = (HotelViewHolder.Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HotelViewHolder.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mResultsCallback.detach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        mCity = bundle.getString(ARG_CITY);
        mCountry = bundle.getString(ARG_COUNTRY);
        String count = bundle.getString(ARG_COUNT);
        mHotelsRequest = bundle.getParcelable(ARG_REQUEST);

        getActivity().setTitle(Html.fromHtml("<b>" + mCity + "</b> " + mCountry + " (" + count + ")"));

        ArrayList<String> hotels = LikedHotels.loadHotels(mCity, mCountry, getActivity());

        mHotelsRequest.setType(new ListType(hotels));
        mHotelsRequest.setSort(null);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new FavoritesHotelListAdapter((BaseActivity) getActivity(), mListener);
        mEtbApi = HotelsApplication.provide(getActivity()).etbApi();

        mRecyclerView.init(mLayoutManager, mAdapter, EtbApi.LIMIT);

        mLoaderImage.setBackgroundResource(R.drawable.logo_animation);
        AnimationDrawable logoAnimation = (AnimationDrawable) mLoaderImage.getBackground();
        logoAnimation.start();
        mModifyPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ModifyPreferencesListener) getActivity()).modifyPreferences();
            }
        });
        mResetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchRequest request = getHotelsRequest();
                request.removeFilter();
                refresh();
            }
        });

        refresh();
    }

    private void loadSearchResults(int offset) {
        try {
            mEtbApi.results(mHotelsRequest, offset).enqueue(mResultsCallback);
        } catch (InvalidParameterException e) {
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        Events.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Events.unregister(this);
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
        mAdapter = null;
        mLayoutManager = null;
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_favorites_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refresh() {
        mLoaderImage.setVisibility(View.VISIBLE);
        mLoaderText.setVisibility(View.VISIBLE);
        mNoResult.setVisibility(View.GONE);

        if (mAdapter != null) {
            mAdapter.clear();
            mRecyclerView.setHasMoreData(true);
            AppLog.e("mEtbApi.results");
            loadSearchResults(0);
        }
    }

    @Subscribe
    public void onSearchResults(SearchResultsEvent event) {


    }

    public void refresh(HotelListRequest request) {
        RequestUtils.apply(mHotelsRequest, request.getDateRange(), request.getNumberOfPersons(), request.getNumberOfRooms());
        refresh();
    }

    public interface ModifyPreferencesListener {
        void modifyPreferences();
    }
}
