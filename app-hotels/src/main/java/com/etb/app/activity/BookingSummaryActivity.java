package com.etb.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.easytobook.api.model.Accommodation;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.fragment.BookingSummaryFragment;
import com.etb.app.fragment.PaymentChoiceFragment;
import com.etb.app.fragment.PriceBreakdownFragment;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.BookingStepsModel;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.OrderItem;
import com.etb.app.utils.PriceRender;

public class BookingSummaryActivity extends BaseActivity implements PriceBreakdownFragment.Listener {
    private static final String FRAGMENT_SUMMARY = "summary";
    private static final String FRAGMENT_PAYMENT_CHOICE = "payment_choice";
    private static final String FRAGMENT_PRICE_BREAKDOWN = "price_breakdown";
    private OrderItem mOrderItem;

    private HotelSnippet mHotelSnippet;
    private HotelListRequest mRequest;

    public static Intent createIntent(OrderItem orderItem, HotelSnippet hotelSnippet, HotelListRequest request, Context context) {
        Intent bookIntent = new Intent(context, BookingSummaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SNIPPET, hotelSnippet);
        bundle.putParcelable(EXTRA_ORDER_ITEM, orderItem);
        bundle.putParcelable(EXTRA_REQUEST, request);
        bookIntent.putExtras(bundle);
        return bookIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHotelSnippet = getIntent().getParcelableExtra(EXTRA_SNIPPET);
        mOrderItem = getIntent().getParcelableExtra(EXTRA_ORDER_ITEM);
        mRequest = getIntent().getParcelableExtra(EXTRA_REQUEST);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.raise, R.anim.shrink)
                    .replace(R.id.fragment_container,
                            BookingSummaryFragment.newInstance(mOrderItem, mHotelSnippet),
                            FRAGMENT_SUMMARY)
                    .commit();
        }

        AnalyticsCalls.get().trackBookingSummary(mRequest, mHotelSnippet, mOrderItem);
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_booking_summary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_booking_summary, menu);
        return true;
    }

    public void onCreditCardPayment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_PAYMENT_CHOICE);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.raise, R.anim.shrink)
                    .remove(fragment)
                    .commit();
        }
        startActivity(BookingProcessActivity.createIntent(mOrderItem, BookingStepsModel.PROCESS_CREDITCARD_PREPAID, mRequest, mHotelSnippet, this));
    }

    public void onPayPalPayment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_PAYMENT_CHOICE);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.raise, R.anim.shrink)
                    .remove(fragment)
                    .commit();
        }
        startActivity(BookingProcessActivity.createIntent(mOrderItem, BookingStepsModel.PROCESS_PAYPAL, mRequest, mHotelSnippet, this));
    }

    public void bookNow() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.raise, R.anim.shrink)
                .replace(R.id.fragment_overlay_container,
                        PaymentChoiceFragment.newInstance(mOrderItem),
                        FRAGMENT_PAYMENT_CHOICE)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_PAYMENT_CHOICE);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.raise, R.anim.shrink)
                    .remove(fragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPriceBreakdownClick(Accommodation.Rate rate) {
        PriceRender priceRender = getPriceRender();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.raise, R.anim.shrink)
                .replace(R.id.fragment_overlay_container,
                        PriceBreakdownFragment.newInstance(mRequest.getNumberOfRooms(), mOrderItem, priceRender),
                        FRAGMENT_PRICE_BREAKDOWN)
                .commit();
    }

}
