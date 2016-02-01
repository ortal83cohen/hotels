package com.etb.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.member.model.BookingEvent;
import com.etb.app.utils.PriceRender;
import com.etb.app.widget.recyclerview.ArrayAdapter;

import java.util.List;

public class BookingsListAdapter extends ArrayAdapter<BookingEvent, BookingViewHolder> {

    private Context mContext;

    public BookingsListAdapter(List<BookingEvent> objects, Context context) {
        super(objects);
        mContext = context;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.member_booking_item, parent, false);
        return new BookingViewHolder(view, mContext);
    }


    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        holder.attachBookingEvent(getItem(position));
    }

}
