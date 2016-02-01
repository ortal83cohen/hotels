package com.etb.app.payment;

import android.content.Context;
import android.content.Intent;

import com.etb.app.BuildConfig;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

/**
 * @author alex
 * @date 2015-07-01
 */
public class PayPal {

    private static String CLIENT_ID_PRODUCTION = "AdZWyVWDtB7WzGiSDjJdsapsbm6iFkVxEjBnLMcYghHPG0bsT3j8nFaCXyz_uKMGAJgTLfV0v1pSVW-D";
    private static String CLIENT_ID_SANDBOX = "AQCBFQ2FoEJWMFyHCis-VuOO4EOaZjt_bvt2X68RhU7rP5zkQ_D6LVoIiqhREChkloL74xlBWkacsDhz";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(BuildConfig.DEBUG ? PayPalConfiguration.ENVIRONMENT_SANDBOX : PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(BuildConfig.DEBUG ? CLIENT_ID_SANDBOX : CLIENT_ID_PRODUCTION)
            .acceptCreditCards(false);

    public static Intent createServiceIntent(Context context) {
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        return intent;
    }

    public static Intent createPaymentIntent(double price, String currency, String item, Context context) {
        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        // TODO: Check what website use for additional parameters
        PayPalPayment payment = new PayPalPayment(BigDecimal.valueOf(price), currency, item,
                PayPalPayment.PAYMENT_INTENT_AUTHORIZE);
        Intent intent = new Intent(context, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        return intent;
    }
}
