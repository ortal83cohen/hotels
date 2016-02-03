package com.etb.app.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.BookingSummaryActivity;
import com.etb.app.adapter.SummaryImageViewHolder;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.OrderItem;
import com.etb.app.utils.PriceRender;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Ortal
 * @date 2015-08-28
 */
public class BookingSummaryFragment extends BaseFragment implements View.OnClickListener {
    public static final String EXTRA_SNIPPET_DETAILS = "snippet_details";
    private static final String EXTRA_ORDER_ITEM = "order_item";

    @Bind(R.id.book_button)
    Button mBookButton;
    @Bind(R.id.image)
    ImageView mImage;
    //    @Bind(R.id.user_name)
//    TextView mUserName;
    @Bind(R.id.snippet_title)
    TextView mSnippetTitle;
    @Bind(R.id.star_rating)
    RatingBar mRatingBar;
    @Bind(R.id.check_in_day)
    TextView mCheckInDay;
    @Bind(R.id.check_out_day)
    TextView mCheckOutDay;
    @Bind(R.id.check_in_date)
    TextView mCheckInDate;
    @Bind(R.id.check_out_date)
    TextView mCheckOutDate;
    @Bind(R.id.number_nights)
    TextView mNumberNights;
    @Bind(R.id.text_nights)
    TextView mTextNights;
    @Bind(R.id.number_persons)
    TextView mNumberPersons;
    @Bind(R.id.text_persons)
    TextView mTextPersons;
    @Bind(R.id.number_rooms)
    TextView mNumberRooms;
    @Bind(R.id.text_rooms)
    TextView mTextRooms;
    @Bind(R.id.room_name)
    TextView mRoomName;
    @Bind(R.id.total_price)
    TextView mTotalPrice;
    @Bind(R.id.postpay)
    TextView mPostpay;
    @Bind(R.id.prepay)
    TextView mPrepay;
    @Bind(R.id.discount_text)
    TextView mDiscountText;
    @Bind(R.id.room_extra)
    TextView mExtra;
    @Bind(R.id.room_refundable)
    TextView mRefundable;
    @Bind(R.id.text_summary)
    TextView mTextSummary;
    @Bind(R.id.text_rooms_price)
    TextView mTextRoomsPrice;

    OrderItem mOrderItem;
    private SimpleDateFormat mDayFormatter = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat mMonthFormatter = new SimpleDateFormat("MMM", Locale.getDefault());
    private HotelSnippet mHotelSnippetDetails;

    public static BookingSummaryFragment newInstance(OrderItem orderItem, HotelSnippet hotelSnippet) {
        BookingSummaryFragment fragment = new BookingSummaryFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ORDER_ITEM, orderItem);
        args.putParcelable(EXTRA_SNIPPET_DETAILS, hotelSnippet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_summary, container, false);
        ButterKnife.bind(this, view);
        SearchRequest request = getHotelsRequest();
        if (savedInstanceState != null) {
            mOrderItem = savedInstanceState.getParcelable(EXTRA_ORDER_ITEM);
            mHotelSnippetDetails = savedInstanceState.getParcelable(EXTRA_SNIPPET_DETAILS);
        } else {
            mHotelSnippetDetails = getArguments().getParcelable(EXTRA_SNIPPET_DETAILS);
            mOrderItem = getArguments().getParcelable(EXTRA_ORDER_ITEM);
        }
        fillDate(request);

        return view;
    }

    private void fillDate(SearchRequest request) {

//        MemberStorage memberStorage = App.provide(getActivity()).memberStorage();
//        User user = memberStorage.loadUser();
//        if(user!=null) {
//            if (TextUtils.isEmpty(user.profile.firstName) && TextUtils.isEmpty(user.profile.lastName)) {
//                mUserName.setText(user.email);
//            } else {
//                mUserName.setText(user.profile.firstName + " " + user.profile.lastName);
//            }
//        }
        String currency = mOrderItem.currency;
        Resources r = getResources();
        PriceRender priceRender = getPriceRender();

        SummaryImageViewHolder SummaryImage = new SummaryImageViewHolder(getActivity(), mRatingBar, mSnippetTitle, mImage);
        SummaryImage.bind(mHotelSnippetDetails.getImageUrl(), mHotelSnippetDetails.getName(), mHotelSnippetDetails.getStarRating());

        mBookButton.setOnClickListener(this);


        updateDatesViews(request.getDateRange());

        mNumberRooms.setText(String.valueOf(request.getNumberOfRooms()));
        mTextRooms.setText(r.getQuantityString(R.plurals.rooms_caps, request.getNumberOfRooms()));
        mNumberPersons.setText(String.valueOf(request.getNumberOfPersons()));
        mTextPersons.setText(r.getQuantityString(R.plurals.persons_caps, request.getNumberOfPersons()));
        if (request.getNumberOfRooms() == 1) {
            mRoomName.setText(mOrderItem.rateName);
        } else {
            mRoomName.setText(String.format("%d x %s", request.getNumberOfRooms(), mOrderItem.rateName));
        }
        int discountPercent = priceRender.calcDiscountPercent(mOrderItem.displayBaseRate, mOrderItem.displayPrice,currency);
        if (discountPercent < 2) {
            mDiscountText.setVisibility(View.GONE);
        } else {
            mDiscountText.setText(getString(R.string.special_deal, discountPercent));
        }
        if (mOrderItem.tags.freeCancellation) {
            mRefundable.setText(R.string.free_cancellation);
        } else if (mOrderItem.tags.nonRefundable) {
            mRefundable.setText(R.string.non_refundable);
        } else {
            mRefundable.setText("");
        }

        if (mOrderItem.tags.breakfastIncluded) {
            mExtra.setVisibility(View.VISIBLE);
        } else {
            mExtra.setVisibility(View.GONE);
        }
        mTotalPrice.setText(priceRender.render(mOrderItem.displayPrice, request.getNumberOfRooms(),currency));
        mTotalPrice.setOnClickListener(this);
        if (mOrderItem.postpaid.get(mOrderItem.postPaidCurrency) != null) {
            NumberFormat formatter = App.provide(getActivity()).getNumberFormatter(mOrderItem.postPaidCurrency);
            mPostpay.setText(formatter.format(mOrderItem.postpaid.get(mOrderItem.postPaidCurrency)));
        } else {
            NumberFormat formatter = App.provide(getActivity()).getNumberFormatter(currency);
            mPostpay.setText(formatter.format(priceRender.getByCurrency(mOrderItem.postpaid,currency)));
        }
        mPrepay.setText(priceRender.render(mOrderItem.prepaid, request.getNumberOfRooms(),currency));
        String summary = r.getQuantityString(R.plurals.text_summary, request.getDateRange().days(), request.getDateRange().days());
        if (mOrderItem.extraTax > 0) {
            summary += " " + r.getString(R.string.text_tax_summary, priceRender.render(mOrderItem.extraTax, request.getNumberOfRooms()));
        }
        mTextSummary.setText(summary);
        mTextRoomsPrice.setText(r.getQuantityString(R.plurals.room_price, request.getNumberOfRooms(), request.getNumberOfRooms()));
    }

    private void updateDatesViews(DateRange range) {
        mCheckInDay.setText(mDayFormatter.format(range.from.getTime()));
        mCheckOutDay.setText(mDayFormatter.format(range.to.getTime()));
        mCheckInDate.setText(mMonthFormatter.format(range.from.getTime()));
        mCheckOutDate.setText(mMonthFormatter.format(range.to.getTime()));
        mNumberNights.setText(String.valueOf(range.days()));
        mTextNights.setText(getResources().getQuantityString(R.plurals.nights_caps, range.days()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_booking_summary, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_SNIPPET_DETAILS, mHotelSnippetDetails);
        outState.putParcelable(EXTRA_ORDER_ITEM, mOrderItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.total_price) {
            ((PriceBreakdownFragment.Listener) getActivity()).onPriceBreakdownClick(null);
        } else if (v.getId() == R.id.book_button) {
            ((BookingSummaryActivity) getActivity()).bookNow();
        }
    }


}
