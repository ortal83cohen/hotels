package com.etb.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easytobook.api.EtbApi;
import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.DetailsResponse;
import com.etb.app.App;
import com.etb.app.HotelsApplication;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.activity.BookingSummaryActivity;
import com.etb.app.adapter.RoomListAdapter;
import com.etb.app.adapter.RoomViewHolder;
import com.etb.app.etbapi.AvailabilityDetailsCallback;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.OrderItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RoomListFragment extends BaseFragment implements RoomViewHolder.BookNowListener {

    private static final String ARG_HOTEL_ID = "hotel_id";
    private static final String ARG_REQUEST = "request";
    @Bind(android.R.id.list)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private RoomListAdapter mAdapter;
    private Accommodation mAccommodation;
    private int mHotelId;
    private HotelListRequest mRequest;

    private RetrofitCallback<DetailsResponse> mResultsCallback = new AvailabilityDetailsCallback() {
        @Override
        protected void onDetailsResponse(DetailsResponse detailsResponse) {
            if (getActivity() != null) {
                getActivity().setTitle(detailsResponse.accommodation.name);
                mAccommodation = detailsResponse.accommodation;
                showRooms(detailsResponse.accommodation);
            }
        }
    };

    public static RoomListFragment newInstance(int hotelId, HotelListRequest request) {

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_HOTEL_ID, hotelId);
        bundle.putParcelable(ARG_REQUEST, request);
        RoomListFragment fragment = new RoomListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RoomListAdapter((BaseActivity)getActivity(), this);
        Bundle bundle = this.getArguments();
        mHotelId = bundle.getInt(ARG_HOTEL_ID);
        mRequest = bundle.getParcelable(ARG_REQUEST);
        //adapter

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);  // requires *wrapped* adapter
        loadHotel();
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
        inflater.inflate(R.menu.menu_room_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onBookNowClicked(Accommodation.Rate rate) {
        String currencyCode = App.provide(getActivity()).getUserPrefs().getCurrencyCode();
        OrderItem orderItem = OrderItem.from(rate, currencyCode, mAccommodation.getPostpaidCurrency());
        getActivity().startActivity(BookingSummaryActivity.createIntent(orderItem, new HotelSnippet(mAccommodation, rate.rateId), mRequest, getActivity()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mResultsCallback.attach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mResultsCallback.detach();
    }

    public void loadHotel() {
        EtbApi etb = App.provide(getActivity()).etbApi();
        etb.details(mHotelId, mRequest).enqueue(mResultsCallback);
    }

    private void showRooms(Accommodation acc) {
        mAdapter.setRooms(acc);
    }


}
