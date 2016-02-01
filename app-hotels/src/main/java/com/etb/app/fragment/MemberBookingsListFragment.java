package com.etb.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.activity.MemberActivity;
import com.etb.app.adapter.BookingsListAdapter;
import com.etb.app.member.model.BookingEvent;
import com.etb.app.provider.DbContract;
import com.etb.app.utils.AppLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MemberBookingsListFragment extends BaseFragment implements Callback<List<BookingEvent>> {

    BookingsListAdapter mAdapter = null;
    @Bind(android.R.id.list)
    RecyclerView mRecyclerView;
    @Bind(R.id.more_booking_button)
    Button mMoreBooking;
    @Bind(android.R.id.empty)
    View mEmptyView;
    private LinearLayoutManager mLayoutManager;

    public static MemberBookingsListFragment newInstance() {

        Bundle bundle = new Bundle();
        MemberBookingsListFragment fragment = new MemberBookingsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void notifyDataSetChanged() {
        loadData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_bookings_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        loadData();
        mRecyclerView.setAdapter(mAdapter);

//        MemberService memberService = App.provide(getActivity()).memberService();
//        memberService.bookings(this);

        mMoreBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MemberActivity) getActivity()).showAddMoreBookingEdit();
            }
        });
    }

    private void loadData() {
        Cursor cursor = getActivity().getContentResolver().query(DbContract.Bookings.CONTENT_URI.buildUpon().build(), null, null, null, null);
        List<BookingEvent> bookingEvents = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do
            {   // public Booking(String orderId, String reference, String city, String country, String hotelName, String hotelStars, String hotelImage, String arrival, String departure, String rooms,
                // String rateName, String tags, String currency, String totalValue, String isCancelled)
                bookingEvents.add(new BookingEvent(new BookingEvent.Booking(cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.ORDER_ID)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.REFERENCE)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.CITY)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.COUNTRY)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.HOTEL_NAME)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.STARS)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.IMAGE)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.ARRIVAL)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.DEPARTURE)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.ROOMS)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.RATE_NAME)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.RATE_NAME)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.CURRENCY)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.TOTAL_VALUE)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.IS_CANCELLED)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.RATE_ID)),
                        cursor.getString(cursor.getColumnIndex(DbContract.BookingsColumns.CONFIRMATION_ID))
                )));
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (mAdapter == null) {
            mAdapter = new BookingsListAdapter(bookingEvents, (BaseActivity)getActivity());
        } else {
            mAdapter.clear();
            mAdapter.addAll(bookingEvents);
        }
        if (mAdapter.getItemCount() == 0) {
            showEmptyView();
        } else {
            showListView();
        }
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showListView() {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
//        mAdapter = null;
        mLayoutManager = null;
        super.onDestroyView();
    }

    @Override
    public void onResponse(Response<List<BookingEvent>> response, Retrofit retrofit) {
        BookingsListAdapter adapter = new BookingsListAdapter(response.body(), getActivity());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(Throwable t) {
        AppLog.e(t);
    }
}
