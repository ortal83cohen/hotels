package com.etb.app.adapter;


import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.fragment.PriceBreakdownFragment;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.utils.PriceRender;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private final BookNowListener mBookNowListener;
    private final PriceRender mPriceRender;
    @Bind(android.R.id.title)
    TextView mTitle;
    @Bind(R.id.price)
    TextView mPrice;
    @Bind(R.id.triangle)
    ImageView mTriangle;
    @Bind(R.id.base_rate)
    TextView mBaseRate;
    @Bind(R.id.discount)
    TextView mDiscount;
    @Bind(R.id.room_extra)
    TextView mExtra;
    @Bind(R.id.room_refundable)
    TextView mRefundable;
    @Bind(R.id.max_guest)
    @Nullable
    TextView mMaxGuest;
    @Bind(R.id.book_button)
    Button mBookButton;
    private Context mContext;
    private Accommodation.Rate mRate;
    private HotelSnippet mHotelSnippet = null;

    public RoomViewHolder(View v, PriceRender priceRender, Context context, BookNowListener bookNowListener) {
        super(v);
        ButterKnife.bind(this, v);
        mBookNowListener = bookNowListener;
        mContext = context;
        mPriceRender = priceRender;
    }

    public void assignItem(Accommodation.Rate item, int numberRooms) {
        mRate = item;

        if (item.tags.freeCancellation) {
            mRefundable.setText(R.string.free_cancellation);
        } else if (item.tags.nonRefundable) {
            mRefundable.setText(R.string.non_refundable);
        } else {
            mRefundable.setText("");
        }

        if (item.tags.breakfastIncluded) {
            mExtra.setVisibility(View.VISIBLE);
        } else {
            mExtra.setVisibility(View.GONE);
        }

        UserPreferences userPrefs = App.provide(mContext).getUserPrefs();
        String currencyCode = userPrefs.getCurrencyCode();

        mTitle.setText(item.name);
        mPrice.setText(mPriceRender.render(mPriceRender.price(item, currencyCode), numberRooms));
        mPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PriceBreakdownFragment.Listener) mContext).onPriceBreakdownClick(mRate);
            }
        });


        int discountNumber = mPriceRender.discount(item, currencyCode);
        if (discountNumber < 10) {
            mDiscount.setVisibility(View.GONE);
            mBaseRate.setVisibility(View.GONE);
            mTriangle.setVisibility(View.INVISIBLE); // Keep layout height
        } else {
            mDiscount.setVisibility(View.VISIBLE);
            mBaseRate.setVisibility(View.VISIBLE);
            mTriangle.setVisibility(View.VISIBLE);
            mBaseRate.setText(mPriceRender.render(mPriceRender.basePrice(item, currencyCode), numberRooms));
            mBaseRate.setPaintFlags(mBaseRate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mDiscount.setText(String.valueOf(discountNumber).concat("%"));
        }
        if (mMaxGuest != null) {
            mMaxGuest.setText(mContext.getString(R.string.max_guests, item.capacity));
        }

        mBookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mBookNowListener.onBookNowClicked(mRate);
    }

    public interface BookNowListener {
        void onBookNowClicked(Accommodation.Rate mRate);
    }
}