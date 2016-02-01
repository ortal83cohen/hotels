package com.etb.app.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easytobook.api.EtbApi;
import com.easytobook.api.model.Order;
import com.easytobook.api.model.OrderResponse;
import com.etb.app.HotelsApplication;
import com.etb.app.R;
import com.etb.app.activity.MemberActivity;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.provider.DbContract;
import com.squareup.okhttp.ResponseBody;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;

/**
 * @author alex
 * @date 2015-08-17
 */
public class AddMoreBookingFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.booking_number)
    EditText mBookingNumber;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.btn_save)
    Button mSaveButton;
    private RetrofitCallback<OrderResponse> mResultsCallback = new RetrofitCallback<OrderResponse>() {
        @Override
        protected void failure(ResponseBody response, boolean isOffline) {
            mSaveButton.setEnabled(true);
            bookingNotFound();
        }

        @Override
        protected void success(OrderResponse orderResponse, Response<OrderResponse> response) {
            if (getActivity() == null) {
                return;
            }
            Order.Rate rate = orderResponse.order.rates.get(0);
            if (rate.password.equals(mPassword.getText().toString())) {
                ContentValues values = new ContentValues();
                values.put(DbContract.BookingsColumns.ORDER_ID, orderResponse.order.orderId);
                values.put(DbContract.BookingsColumns.REFERENCE, orderResponse.order.orderId);
                values.put(DbContract.BookingsColumns.CITY, rate.accommodation.summary.city);
                values.put(DbContract.BookingsColumns.COUNTRY, rate.accommodation.summary.country);
                values.put(DbContract.BookingsColumns.TOTAL_VALUE, rate.charge.totalChargeable);
                values.put(DbContract.BookingsColumns.STARS, String.valueOf(Double.valueOf(rate.accommodation.starRating)));
                values.put(DbContract.BookingsColumns.ROOMS, rate.rateCount);
                values.put(DbContract.BookingsColumns.RATE_NAME, rate.name);
                values.put(DbContract.BookingsColumns.IS_CANCELLED, false);
                if (rate.accommodation.images.size() > 0) {
                    values.put(DbContract.BookingsColumns.IMAGE, rate.accommodation.images.get(0));
                } else {
                    values.put(DbContract.BookingsColumns.IMAGE, "");
                }
                values.put(DbContract.BookingsColumns.HOTEL_NAME, rate.accommodation.name);
                values.put(DbContract.BookingsColumns.CURRENCY, rate.charge.currency);
                values.put(DbContract.BookingsColumns.ARRIVAL, rate.checkIn);
                values.put(DbContract.BookingsColumns.DEPARTURE, rate.checkOut);
                values.put(DbContract.BookingsColumns.RATE_ID, rate.rateId);
                values.put(DbContract.BookingsColumns.CONFIRMATION_ID, rate.confirmationId);
                getActivity().getContentResolver().insert(DbContract.Bookings.CONTENT_URI, values);
            } else {
                mSaveButton.setEnabled(true);
                Toast.makeText(getActivity(), R.string.booking_or_password_not_match, Toast.LENGTH_LONG).show();
            }
            removeThisFragment();
        }


    };

    public static AddMoreBookingFragment newInstance() {
        return new AddMoreBookingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_more_booking, container, false);
        ButterKnife.bind(this, view);


        mSaveButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        EtbApi etbApi = HotelsApplication.provide(getActivity()).etbApi();

        etbApi.retrieve("B-" + mBookingNumber.getText().toString(), mPassword.getText().toString()).enqueue(mResultsCallback);
        mSaveButton.setEnabled(false);

    }

    private void removeThisFragment() {
        ((MemberActivity) getActivity()).notifyDataSetChanged();
        ((MemberActivity) getActivity()).remove(this);
    }

    private void bookingNotFound() {
        Toast.makeText(getActivity(), R.string.booking_or_password_not_match, Toast.LENGTH_LONG).show();
    }
}
