package com.etb.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.easytobook.api.model.DateRange;
import com.easytobook.api.utils.DateRangeUtils;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.ConfirmationActivity;
import com.etb.app.member.model.BookingEvent;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.CalendarUtils;
import com.etb.app.utils.PriceRender;
import com.etb.app.widget.NumberBoxView;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-08-19
 */
public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final Context mContext;
    private final SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SummaryImageViewHolder mSummaryImageHolder;
    private final SimpleDateFormat mDayFormatter = new SimpleDateFormat("dd", Locale.getDefault());
    private final SimpleDateFormat mMonthFormatter = new SimpleDateFormat("MMM", Locale.getDefault());

    @Bind(R.id.image)
    ImageView mImageView;
    @Bind(R.id.snippet_title)
    TextView mSnippetTitle;
    @Bind(R.id.star_rating)
    RatingBar mRatingBar;
    @Bind(R.id.city)
    TextView mCity;
    @Bind(R.id.country)
    TextView mCountry;
    @Bind(R.id.check_in_date)
    NumberBoxView mCheckIn;
    @Bind(R.id.check_out_date)
    NumberBoxView mCheckOut;
    @Bind(R.id.number_nights)
    NumberBoxView mNights;
    @Bind(R.id.number_rooms)
    NumberBoxView mRooms;
    @Bind(R.id.btn_manage)
    Button mManageButton;
    @Bind(R.id.price)
    TextView mPrice;
    @Bind(R.id.room_name)
    TextView mRoomName;
    private String orderId;

    public BookingViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = context;
        mManageButton.setOnClickListener(this);
        mSummaryImageHolder = new SummaryImageViewHolder(context, mRatingBar, mSnippetTitle, mImageView);
    }

    public void attachBookingEvent(BookingEvent event) {
        BookingEvent.Booking booking = event.booking;
        orderId = booking.orderId;

        mSummaryImageHolder.bind(booking.hotelImage, booking.hotelName, (int)booking.hotelStars);

        mCity.setText(booking.city);
        mCountry.setText(booking.country);

        Date arrivalDate = parseDate(booking.arrival);
        mCheckIn.setValue(mDayFormatter.format(arrivalDate));
        mCheckIn.setSubtitle(mMonthFormatter.format(arrivalDate));

        Date departureDate = parseDate(booking.departure);
        mCheckOut.setValue(mDayFormatter.format(departureDate));
        mCheckOut.setSubtitle(mMonthFormatter.format(departureDate));

        Resources r = mContext.getResources();

        DateRange range = new DateRange(arrivalDate.getTime(), departureDate.getTime());
        int days = range.days();
        mNights.setValue(days);
        mNights.setTitle(r.getQuantityString(R.plurals.nights_caps, days));

        mRooms.setValue(booking.rooms);
        mRooms.setTitle(r.getQuantityString(R.plurals.rooms_caps, booking.rooms));

        PriceRender priceRender = createPriceRender(booking.currency, DateRangeUtils.days(arrivalDate.getTime(), departureDate.getTime()));

        mPrice.setText(priceRender.render(booking.totalValue));

        mRoomName.setText(booking.rateName);
    }

    public PriceRender createPriceRender(String currencyCode, int numOfDays) {
        return PriceRender.create(UserPreferences.PRICE_SHOW_TYPE_STAY, currencyCode, numOfDays, mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_manage:
                mContext.startActivity(ConfirmationActivity.createIntent(Integer.valueOf(orderId), false, mContext));
                break;
        }

    }

    private Date parseDate(String dateString) {
        Date date = new Date();
        try {
            date = mFormatter.parse(dateString);
        } catch (ParseException e) {
            AppLog.e(e);
        }
        return date;
    }
}
