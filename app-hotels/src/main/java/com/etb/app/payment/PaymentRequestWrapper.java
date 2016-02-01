package com.etb.app.payment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.easytobook.api.model.OrderRequest;
import com.etb.app.activity.PayPalActivity;
import com.etb.app.model.BillingAddressPage;
import com.etb.app.model.BookingStepsModel;
import com.etb.app.model.CreditCardDetailsPage;

/**
 * @author alex
 * @date 2015-07-01
 */
public class PaymentRequestWrapper {

    public static final String SELECTED_CARD_TYPE = "selected_card_type";
    private BookingStepsModel mBookingStepsModel;
    private int mPaymentType;
    private OrderRequest.Builder mOrderRequestBuilder;

    public PaymentRequestWrapper(BookingStepsModel bookingStepsModel, int paymentType, OrderRequest.Builder orderRequestBuilder, Context context) {
        mBookingStepsModel = bookingStepsModel;
        mPaymentType = paymentType;
        mOrderRequestBuilder = orderRequestBuilder;
    }

    public PaymentRequestWrapper setPayment(Bundle transactionDetails) {

        if (mPaymentType == BookingStepsModel.PROCESS_PAYPAL) {
            setPaypal(transactionDetails);
        } else {
            setCreditCard(transactionDetails);
        }

        return this;
    }

    private void setCreditCard(Bundle transactionDetails) {
        Bundle billingAddressData = mBookingStepsModel.findByKey(BillingAddressPage.KEY).getData();
        Bundle paymentData = mBookingStepsModel.findByKey(CreditCardDetailsPage.KEY).getData();

        String state = billingAddressData.getString(BillingAddressPage.DATA_STATE);
        mOrderRequestBuilder.setBillingAddress(
                billingAddressData.getString(BillingAddressPage.DATA_BILLING_COUNTRY_ISO),
                state,
                billingAddressData.getString(BillingAddressPage.DATA_BILLING_CITY),
                billingAddressData.getString(BillingAddressPage.DATA_BILLING_ADDRESS),
                billingAddressData.getString(BillingAddressPage.DATA_BILLING_ZIP)
        );

        String expYear = paymentData.getString(CreditCardDetailsPage.DATA_CARD_EXP_YEAR);
        String expMonth = paymentData.getString(CreditCardDetailsPage.DATA_CARD_EXP_MONTH);
        int yy = TextUtils.isEmpty(expYear) ? 0 : Integer.parseInt(expYear);
        int mm = TextUtils.isEmpty(expMonth) ? 0 : Integer.parseInt(expMonth);

        mOrderRequestBuilder.setCreditCard(
                transactionDetails.getString(SELECTED_CARD_TYPE),
                paymentData.getString(CreditCardDetailsPage.DATA_CARD_NUMBER),
                paymentData.getString(CreditCardDetailsPage.DATA_CARD_CCV),
                paymentData.getString(CreditCardDetailsPage.DATA_CARD_NAME_FIRST),
                paymentData.getString(CreditCardDetailsPage.DATA_CARD_NAME_LAST),
                mm,
                Integer.parseInt(String.format("20%d", yy))
        );
    }

    private void setPaypal(Bundle transactionDetails) {
        PayPalPaymentData data = new PayPalPaymentData();
        data.authId = transactionDetails.getString(PayPalActivity.AUTHORIZATION_ID);
        mOrderRequestBuilder.setPayment("PPL", data);
    }

}
