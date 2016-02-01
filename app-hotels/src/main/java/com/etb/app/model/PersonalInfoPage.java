package com.etb.app.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;

import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.analytics.Omniture;
import com.etb.app.fragment.PersonalInfoFragment;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * @author alex
 * @date 2015-05-03
 */
public class PersonalInfoPage extends AbstractPage {
    public static final String KEY = "personal_details";
    public static final String DATA_EMAIL = "email";
    public static final String DATA_FIRST_NAME = "fname";
    public static final String DATA_LAST_NAME = "lname";
    public static final String DATA_PHONE = "phone";

    public PersonalInfoPage(ModelCallbacks callbacks) {
        super(callbacks, KEY);
        setRequired(true);
    }


    @Override
    public Fragment createFragment() {
        return PersonalInfoFragment.newInstance();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> list) {

    }

    @Override
    public boolean isCompleted() {
        if (TextUtils.isEmpty(mData.getString(DATA_EMAIL)) || !Patterns.EMAIL_ADDRESS.matcher(mData.getString(DATA_EMAIL)).matches()) {
            return false;
        }
        return !(TextUtils.isEmpty(mData.getString(DATA_PHONE)) || !Patterns.PHONE.matcher(mData.getString(DATA_PHONE)).matches()) && !TextUtils.isEmpty(mData.getString(DATA_FIRST_NAME)) && !TextUtils.isEmpty(mData.getString(DATA_LAST_NAME));
    }

    @Override
    public Bundle getData() {
//        if(!isCompleted()){
//            throw new InvalidParameterException("All fields must be filled");
//        }
        return super.getData();
    }


    @Override
    public void onPageSelected() {
        AnalyticsCalls.get().trackBookingFormPersonal();
    }
}
