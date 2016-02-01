package com.etb.app.model;

import android.content.Context;

import com.tech.freak.wizardpager.model.AbstractWizardModel;

/**
 * @author alex
 * @date 2015-05-03
 */
public abstract class BookingStepsModel extends AbstractWizardModel {

    public static final int PROCESS_CREDITCARD_PREPAID = 0;
    public static final int PROCESS_CREDITCARD_POSTPAID = 1;
    public static final int PROCESS_PAYPAL = 2;
    public static final int PROCESS_CHECKOUT = 3;

    public BookingStepsModel(Context context) {
        super(context);
    }

    public static BookingStepsModel create(int processType, Context context) {
        if (processType == PROCESS_PAYPAL) {
            return new PayPalBookingStepsModel(context);
        }
        return new CreditCardBookingStepsModel(context);
    }

}
