package com.etb.app.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easytobook.api.EtbApi;
import com.easytobook.api.contract.OrderStatus;
import com.easytobook.api.contract.PaymentType;
import com.easytobook.api.contract.Policy;
import com.easytobook.api.model.CancelRequest;
import com.easytobook.api.model.CancelResponse;
import com.easytobook.api.model.DateRange;
import com.easytobook.api.model.ErrorResponse;
import com.easytobook.api.model.Order;
import com.easytobook.api.model.OrderResponse;
import com.easytobook.api.model.TaxesAndFees;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.adapter.SummaryImageViewHolder;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.core.CoreInterface;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.etbapi.RetrofitConverter;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.provider.DbContract;
import com.etb.app.utils.AppLog;
import com.etb.app.utils.CalendarUtils;
import com.etb.app.utils.PriceRender;
import com.etb.app.utils.SharingUtils;
import com.etb.app.widget.NumberBoxView;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;

/**
 * @author alex
 * @date 2015-05-05
 */
public class ConfirmationActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_ORDER_ID = "orderId";
    public static final String FALLBACK_PHONE_NUMBER = "+31 20 531 33 00";
    private static final String EXTRA_CONFIRMATION_ID = "confiramtionId";
    private static final String EXTRA_RATE_ID = "rateId";
    private static final String EXTRA_SAVE = "save";
    private final SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.email)
    TextView mEmail;
    @Bind(R.id.check_in)
    TextView mCheckIn;
    @Bind(R.id.check_out)
    TextView mCheckOut;
    @Bind(R.id.confirmation_id)
    TextView mConfirmationId;
    @Bind(R.id.password)
    TextView mPassword;
    @Bind(R.id.image)
    ImageView mImageView;
    @Bind(R.id.snippet_title)
    TextView mSnippetTitle;
    @Bind(R.id.star_rating)
    RatingBar mRatingBar;
    @Bind(R.id.number_nights)
    NumberBoxView mNights;
    @Bind(R.id.number_rooms)
    NumberBoxView mRooms;
    @Bind(R.id.number_guests)
    NumberBoxView mGuests;
    @Bind(R.id.hotel_address)
    TextView mHotelAddress;
    @Bind(R.id.phone_button)
    Button mPhoneButton;
    @Bind(R.id.email_button)
    Button mEmailButton;
    @Bind(R.id.navigate)
    android.support.design.widget.FloatingActionButton mNavigate;
    @Bind(R.id.payment_information)
    TextView mPaymentInformation;
    @Bind(R.id.cancellation_policy)
    TextView mCancellationPolicy;
    @Bind(R.id.important_information_and_conditions)
    TextView mImportantInformationAndConditions;
    @Bind(R.id.contact_us)
    Button mContactUsButton;
    @Bind(R.id.room_name)
    TextView mRoomName;
    @Bind(R.id.room_extra)
    TextView mExtra;
    @Bind(R.id.room_refundable)
    TextView mRefundable;
    @Bind(R.id.room_persons)
    TextView mRoomPersons;
    @Bind(R.id.price)
    TextView mPrice;
    @Bind(R.id.base_rate)
    TextView mBaseRate;
    @Bind(R.id.room_discount)
    TextView mDiscount;
    @Bind(R.id.total_price)
    TextView mTotalPrice;
    @Bind(R.id.tax)
    TextView mTax;
    @Bind(R.id.tax_text)
    TextView mTaxText;
    @Bind(R.id.ban_cancel_reservation)
    Button mCancelReservationButton;
    @Bind(R.id.add_to_calender)
    android.support.design.widget.FloatingActionButton mAddToCalender;
    @Bind(R.id.share)
    android.support.design.widget.FloatingActionButton mShare;
    private EtbApi mEtbApi;
    private boolean mSave;
    private int mCancelReason;
    private String mRateId;
    private String mReferenceId;
    private int mOrderId;
    private Order.Rate mRate;
    private RetrofitCallback<String> mPhoneCallback = new RetrofitCallback<String>() {

        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            startPhoneActivity(FALLBACK_PHONE_NUMBER);
        }

        @Override
        protected void success(String phoneNumber, Response<String> response) {
            startPhoneActivity(phoneNumber);
        }

        private void startPhoneActivity(String phoneNumber) {
            String number = "tel:" + phoneNumber;
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
            startActivity(callIntent);
        }
    };


    private RetrofitCallback<OrderResponse> mRetrofitCallback = new RetrofitCallback<OrderResponse>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            Toast.makeText(mWeakContext.get(), R.string.cannot_retrieve_order, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void success(OrderResponse orderResponse, Response<OrderResponse> response) {
            ArrayList<String> ids = new ArrayList<>();
            for (Order.Rate rate : orderResponse.order.rates) {
                ids.add(rate.confirmationId);
            }
            if (mSave) {
                ContentValues values = new ContentValues();
                Order.Rate rate = orderResponse.order.rates.get(0);
                values.put(DbContract.BookingsColumns.ORDER_ID, orderResponse.order.orderId);
                values.put(DbContract.BookingsColumns.REFERENCE, orderResponse.order.orderId);
                values.put(DbContract.BookingsColumns.CITY, rate.accommodation.summary.city);
                values.put(DbContract.BookingsColumns.COUNTRY, rate.accommodation.summary.country);
                values.put(DbContract.BookingsColumns.TOTAL_VALUE, rate.charge.totalChargeable);
                values.put(DbContract.BookingsColumns.STARS, String.valueOf(Double.valueOf(rate.accommodation.starRating)));
                values.put(DbContract.BookingsColumns.ROOMS, rate.rateCount);
                values.put(DbContract.BookingsColumns.RATE_NAME, rate.name);
                values.put(DbContract.BookingsColumns.IS_CANCELLED, !OrderStatus.CONFIRMED.equals(rate.statusCode));
                if (rate.accommodation.images.size() > 0) {
                    values.put(DbContract.BookingsColumns.IMAGE, rate.accommodation.images.get(0));
                } else {
                    values.put(DbContract.BookingsColumns.IMAGE, "");
                }
                values.put(DbContract.BookingsColumns.HOTEL_NAME, rate.accommodation.name);
                values.put(DbContract.BookingsColumns.CURRENCY, rate.charge.currency);
                values.put(DbContract.BookingsColumns.ARRIVAL, rate.checkIn);
                values.put(DbContract.BookingsColumns.DEPARTURE, rate.checkOut);
                values.put(DbContract.BookingsColumns.CONFIRMATION_ID, rate.confirmationId);
                values.put(DbContract.BookingsColumns.RATE_ID, rate.rateId);
                getContentResolver().insert(DbContract.Bookings.CONTENT_URI, values);
                AnalyticsCalls.get().transaction(orderResponse);
            }
            setupData(orderResponse, ids);
        }

    };

    public static Intent createIntent(int orderId, boolean saveToDb, Context context) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        intent.putExtra(EXTRA_SAVE, saveToDb);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        mEtbApi = App.provide(this).etbApi();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            mOrderId = extras.getInt(EXTRA_ORDER_ID);
            mSave = extras.getBoolean(EXTRA_SAVE);
        } else {
            mOrderId = savedInstanceState.getInt(EXTRA_ORDER_ID);
            mSave = savedInstanceState.getBoolean(EXTRA_SAVE);
            mReferenceId = savedInstanceState.getString(EXTRA_CONFIRMATION_ID);
            mRateId = savedInstanceState.getString(EXTRA_RATE_ID);
        }

        mCancelReservationButton.setOnClickListener(this);
        mAddToCalender.setOnClickListener(this);
        mNavigate.setOnClickListener(this);
        mShare.setOnClickListener(this);

        mRetrofitCallback.attach(this);
        mEtbApi.retrieve(String.valueOf(mOrderId), null).enqueue(mRetrofitCallback);

        AnalyticsCalls.get().trackBookingConfirmation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_CONFIRMATION_ID, mReferenceId);
        outState.putString(EXTRA_RATE_ID, mRateId);
        outState.putBoolean(EXTRA_SAVE, mSave);
        outState.putInt(EXTRA_ORDER_ID, mOrderId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_confirmation);
    }

    private void setupData(OrderResponse orderResponse, ArrayList<String> ids) {
        mRate = orderResponse.order.rates.get(0);
        mRateId = String.valueOf(mRate.rateId);
        mReferenceId = mRate.confirmationId;
        mAddToCalender.setVisibility(View.VISIBLE);
        mShare.setVisibility(View.VISIBLE);

        String currencyCode = mRate.charge.currency;
        Date arrivalDate = parseDate(mRate.checkIn);
        Date departureDate = parseDate(mRate.checkOut);

        mConfirmationId.setText(String.format(Locale.US, "Reference: %s", TextUtils.join(",", ids)));
        mConfirmationId.setVisibility(View.VISIBLE);
        Resources r = getResources();
        if (OrderStatus.CONFIRMED.equals(mRate.statusCode)) {
            mTitle.setText(Html.fromHtml(r.getString(R.string.booking_confirmed, orderResponse.order.personal.firstName)));
        } else {
            mTitle.setText(Html.fromHtml(r.getString(R.string.booking_cancel, orderResponse.order.personal.firstName, mRate.statusCode)));
        }
        mName.setText(orderResponse.order.personal.firstName + " " + orderResponse.order.personal.lastName);
        mEmail.setText(orderResponse.order.personal.email);
        mCheckIn.setText(Html.fromHtml("<b>" + mRate.checkIn + "</b><br>" + mRate.accommodation.details.checkInTime));
        mCheckOut.setText(Html.fromHtml("<b>" + mRate.checkOut + "</b><br>" + mRate.accommodation.details.checkOutTime));
        mConfirmationId.setText(mRate.confirmationId);
        mPassword.setText(mRate.password);

        SummaryImageViewHolder SummaryImage = new SummaryImageViewHolder(getBaseContext(), mRatingBar, mSnippetTitle, mImageView);
        if (mRate.accommodation.images != null && mRate.accommodation.images.size() > 0) {
            SummaryImage.bind(mRate.accommodation.images.get(0), mRate.accommodation.name, 3);
        }

        DateRange range = new DateRange(arrivalDate.getTime(), departureDate.getTime());
        int days = range.days();
        mNights.setValue(days);
        mNights.setTitle(r.getQuantityString(R.plurals.nights_caps, days));
        mGuests.setValue(mRate.capacity);
        mGuests.setTitle(r.getQuantityString(R.plurals.guests_caps, Integer.valueOf(mRate.capacity)));
        mRooms.setValue(mRate.rateCount);
        mRooms.setTitle(r.getQuantityString(R.plurals.rooms_caps, mRate.rateCount));

        mHotelAddress.setText(getString(R.string.booking_address, mRate.accommodation.summary.address, mRate.accommodation.summary.city, mRate.accommodation.summary.state, mRate.accommodation.summary.country));

        if (!mRate.accommodation.phone.isEmpty()) {
            mPhoneButton.setVisibility(View.VISIBLE);
            mPhoneButton.setText(mRate.accommodation.phone);
            mPhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = "tel:" + mRate.accommodation.phone;
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
                    startActivity(callIntent);
                }
            });
        }
        if (!mRate.accommodation.email.isEmpty()) {
            mEmailButton.setVisibility(View.VISIBLE);
            mEmailButton.setText(mRate.accommodation.email);
            mEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", mRate.accommodation.email, null));
                    startActivity(Intent.createChooser(emailIntent, ""));
                }
            });
        }
        mRoomName.setText(String.format("%d X %s", mRate.rateCount, mRate.name));

        if (mRate.cancellationPolicy.policy == Policy.NONE) {
            mRefundable.setText(R.string.non_refundable);
        } else {
            mRefundable.setText(R.string.refundable);
        }

        if (mRate.breakfast.plan == 2) {
            mExtra.setVisibility(View.VISIBLE);
        } else {
            mExtra.setVisibility(View.GONE);
        }

        PriceRender priceRender = createPriceRender(currencyCode, days);

        int capacity = Integer.valueOf(mRate.capacity);
        mRoomPersons.setText(getResources().getQuantityString(R.plurals.capacity_persons_max, capacity, capacity));
        mPrice.setText(priceRender.render(Double.valueOf(mRate.charge.totalChargeable)));
        mTotalPrice.setText(priceRender.render(Double.valueOf(mRate.charge.totalChargeable)));
        mTaxText.setText(R.string.tax_recovery_charges_and_fees);
        double tax = 0;
        for (TaxesAndFees taf : mRate.taxesAndFees) {
            tax += taf.totalValue.get(currencyCode);
        }
        mTax.setText(priceRender.render(tax));

        mContactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPreferences userPrefs = getUserPrefs();
                CoreInterface.Service coreInterface = CoreInterface.create(getApplicationContext());
                coreInterface.customerServicePhone(userPrefs.getCountryCode().toLowerCase()).enqueue(mPhoneCallback);
            }
        });


        if (OrderStatus.CONFIRMED.equals(mRate.statusCode) && arrivalDate.after(new Date())) {
            mCancelReservationButton.setVisibility(View.VISIBLE);
        } else {
            mCancelReservationButton.setVisibility(View.GONE);
        }

        mCancellationPolicy.setText(Html.fromHtml(mRate.cancellationPolicyText));

        if (TextUtils.isEmpty(mRate.accommodation.details.importantInfo)) {
            findViewById(R.id.section_important_information).setVisibility(View.GONE);
        } else {
            mImportantInformationAndConditions.setText(Html.fromHtml(mRate.accommodation.details.importantInfo));
        }

        if (PaymentType.PREPAID.equals(mRate.charge.paymentType)) {
            if (OrderStatus.CONFIRMED.equals(mRate.statusCode)) {
                mPaymentInformation.setText(R.string.payment_completed);
            } else {
                mPaymentInformation.setText(R.string.payment_pending);
            }
        } else { // Postpaid
            if (mRate.cancellationPolicy.policy == Policy.NONE) {
                mPaymentInformation.setText(R.string.payment_accomodation_charge);
            } else {
                mPaymentInformation.setText(R.string.payment_accomodation_preauthorize);
            }
        }

    }

    public PriceRender createPriceRender(String currencyCode, int numOfDays) {
        return PriceRender.create(UserPreferences.PRICE_SHOW_TYPE_STAY, currencyCode, numOfDays, this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ban_cancel_reservation:
                createCancelDialog().show();
                break;
            case R.id.add_to_calender:
                Date arrivalDate = parseDate(mRate.checkIn);
                Date departureDate = parseDate(mRate.checkOut);
                CalendarUtils.addToCalender(this, arrivalDate, departureDate, getString(R.string.staying_at, mRate.accommodation.name), getString(R.string.add_to_calender_note_text, mRate.confirmationId, mRate.password),
                        mRate.accommodation.location.toString(), mRate.accommodation.email, mRate.accommodation.phone);
                break;
            case R.id.share:
                new android.app.AlertDialog.Builder(this)
                        .setTitle(R.string.note)
                        .setMessage(R.string.sensitive_information)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                share();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                break;
            case R.id.navigate:
                Uri gmmIntentUri = Uri.parse(new StringBuilder().append("geo:").append(mRate.accommodation.location.toString()).
                        append("?q=").append(mRate.accommodation.summary.address).append(" ").append(mRate.accommodation.summary.city).
                        append(" ").append(mRate.accommodation.summary.state).append(" ").append(mRate.accommodation.summary.country).toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);
                break;
        }
    }

    private void share() {
        SharingUtils.shareText(this,
                getString(R.string.staying_at, mRate.accommodation.name),
                getString(R.string.share_text, mRate.accommodation.summary.city, mRate.accommodation.summary.country, mRate.checkIn, mRate.checkOut, mRate.confirmationId, mRate.password)
        );
    }


    private AlertDialog createCancelDialog() {
        final CharSequence[] items = {
                "Cancelled Trip, Unforeseen circumstances (weather conditions, flight delay/cancellation, personal reasons etc.)",
                "Found other hotel on other site",
                "Found better rate for the same hotel on another website",
                "Found another hotel on this website",
                "Found bad reviews for the hotel",
                "Change in Travel Dates",
                "I have been contacted by the hotel",
                "Other"
        };
        final int[] reasons = {2, 6, 8, 9, 12, 13, 14, 11};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_reservation)
                .setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                mCancelReason = reasons[item];
                            }
                        })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelReservationRequest();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        return builder.create();
    }

    private void cancelReservationRequest() {
        // continue with delete
        EtbApi etbApi = App.provide(this).etbApi();
        final Context context = this;
        Callback<CancelResponse> mCancelCallback = new RetrofitCallback<CancelResponse>() {
            @Override
            protected void failure(ResponseBody errorBody, boolean isOffline) {
                if (errorBody != null) {
                    try {
                        ErrorResponse body = (ErrorResponse) RetrofitConverter.getBodyAs(errorBody, ErrorResponse.class);
                        Toast.makeText(context, body.meta.errorMessage, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        AppLog.e(e);
                    }
                }
                Toast.makeText(context, R.string.reservation_didnt_cancelled_unknown_error, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void success(CancelResponse body, Response<CancelResponse> response) {
                context.getContentResolver().delete(DbContract.Bookings.CONTENT_URI, DbContract.BookingsColumns.RATE_ID + " = " + mRateId, null);
                Toast.makeText(context, R.string.reservation_cancelled, Toast.LENGTH_LONG).show();
                mSave = true;
                mEtbApi.retrieve(String.valueOf(mOrderId), null).enqueue(mRetrofitCallback);
            }

        };
//        ContentValues values = new ContentValues();
//        values.put(DbContract.BookingsColumns.IS_CANCELLED, true);
//        context.getContentResolver().update(DbContract.Bookings.CONTENT_URI.buildUpon().appendPath(String.valueOf(mRateId)).build(), values, null, null);
        etbApi.cancel(new CancelRequest(mReferenceId, mCancelReason), String.valueOf(mOrderId), mRateId).enqueue(mCancelCallback);
    }
}
