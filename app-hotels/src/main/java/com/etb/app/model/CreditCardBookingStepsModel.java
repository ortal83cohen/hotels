package com.etb.app.model;

import android.content.Context;

import com.tech.freak.wizardpager.model.PageList;

/**
 * @author alex
 * @date 2015-05-03
 */
public class CreditCardBookingStepsModel extends BookingStepsModel {
    private OrderItem mOrderItem;

    public CreditCardBookingStepsModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new PersonalInfoPage(this),
                new CreditCardDetailsPage(this),
                new BillingAddressPage(this)
        );
    }
}
