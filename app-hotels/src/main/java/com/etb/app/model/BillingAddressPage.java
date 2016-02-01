package com.etb.app.model;

import android.support.v4.app.Fragment;

import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.fragment.BillingAddressFragment;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;

/**
 * @author alex
 * @date 2015-05-03
 */
public class BillingAddressPage extends AbstractPage {
    public static final String KEY = "payment_details";
    public static final String DATA_BILLING_COUNTRY_ISO = "billcountry_iso";
    public static final String DATA_BILLING_COUNTRY = "billcountry";
    public static final String DATA_BILLING_CITY = "billcity";
    public static final String DATA_BILLING_ADDRESS = "billaddress";
    public static final String DATA_BILLING_ZIP = "billzip";
    public static final String DATA_STATE = "state";
    public static final String DATA_CARD_TYPE_POS = "cctype_pos";

    public BillingAddressPage(ModelCallbacks callbacks) {
        super(callbacks, KEY);
        setRequired(true);
    }

    @Override
    public Fragment createFragment() {
        return BillingAddressFragment.newInstance();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> arrayList) {

    }

    @Override
    public void onPageSelected() {
        AnalyticsCalls.get().trackBookingFormAddress();
    }
}
