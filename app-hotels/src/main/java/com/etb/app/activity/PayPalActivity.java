package com.etb.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.model.OrderItem;
import com.etb.app.payment.PayPal;
import com.etb.app.utils.AppLog;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

/**
 * @author alex
 * @date 2015-06-07
 */
public class PayPalActivity extends BaseActivity {
    public static final String EXTRA_ITEM_NAME = "item_name";
    public static final String EXTRA_CHARGE_AMOUNT = "amount";
    public static final String EXTRA_CURRENCY = "currency";

    public static final String AUTHORIZATION_ID = "authorization_id";

    private static final int STATE_INIT = 0;
    private static final int STATE_START = 1;
    private static final int STATE_COMPLETE = 2;
    private static final int STATE_CANCEL = 3;
    private static final int STATE_ERROR = 4;

    private String mItemName;
    private double mChargeAmount;
    private String mCurrency;
    private int mState = STATE_INIT;
    private String mAuthorizationId;

    public static Intent createIntent(OrderItem orderItem, Context context) {
        Intent intent = new Intent(context, PayPalActivity.class);
        intent.putExtra(EXTRA_ITEM_NAME, orderItem.rateName);
        intent.putExtra(EXTRA_CHARGE_AMOUNT, orderItem.chargeAmount);
        intent.putExtra(EXTRA_CURRENCY, orderItem.currency);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mItemName = intent.getStringExtra(EXTRA_ITEM_NAME);
            mChargeAmount = intent.getDoubleExtra(EXTRA_CHARGE_AMOUNT, -1);
            mCurrency = intent.getStringExtra(EXTRA_CURRENCY);
            mState = STATE_INIT;
        } else {
            mItemName = savedInstanceState.getString(EXTRA_ITEM_NAME);
            mChargeAmount = savedInstanceState.getDouble(EXTRA_CHARGE_AMOUNT);
            mCurrency = savedInstanceState.getString(EXTRA_CURRENCY);
            mState = savedInstanceState.getInt("state");
            mAuthorizationId = savedInstanceState.getString(AUTHORIZATION_ID);
        }

        // todo: validate mUriData
        if (mState != STATE_INIT) {
            complete();
            return;
        }

        AnalyticsCalls.get().trackBookingFormPayment();

        startService(PayPal.createServiceIntent(this));

        startPayment();
    }

    @Override
    protected void onCreateContentView() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble(EXTRA_CHARGE_AMOUNT, mChargeAmount);
        outState.putString(EXTRA_ITEM_NAME, mItemName);
        outState.putString(EXTRA_CURRENCY, mCurrency);
        outState.putInt("state", mState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        stopService(PayPal.createServiceIntent(this));
        super.onDestroy();
    }

    protected void startPayment() {
        Intent intent = PayPal.createPaymentIntent(mChargeAmount, mCurrency, mItemName, this);

        mState = STATE_START;
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {

                    AppLog.i("PayPal", confirm.toJSONObject().toString(4));

                    mAuthorizationId = confirm.getProofOfPayment().getTransactionId();

                    mState = STATE_COMPLETE;
                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    mState = STATE_ERROR;
                    AppLog.e(e);
                }
            } else {
                mState = STATE_ERROR;
                AppLog.e("PaymentConfirmation is null");
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            AppLog.d("The user canceled PayPal transaction.");
            mState = STATE_CANCEL;
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            AppLog.e("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            mState = STATE_ERROR;
        }
        complete();
    }

    private void complete() {
        if (mState == STATE_COMPLETE) {
            Intent resultData = new Intent();
            resultData.putExtra(AUTHORIZATION_ID, mAuthorizationId);
            setResult(RESULT_OK, resultData);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

}
