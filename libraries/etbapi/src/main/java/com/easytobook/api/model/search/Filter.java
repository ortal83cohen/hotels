package com.easytobook.api.model.search;

import android.util.SparseBooleanArray;

/**
 * @author alex
 * @date 2015-12-03
 */
public class Filter {
    private SparseBooleanArray mStars;
    private SparseBooleanArray mRating;
    private SparseBooleanArray mAccTypes;
    private SparseBooleanArray mMainFacilities;

    private int mMinRate;
    private int mMaxRate;

    public Filter() {
    }

    public int getMinRate() {
        return mMinRate;
    }

    public void setMinRate(int minRate) {
        mMinRate = minRate;
    }

    public int getMaxRate() {
        return mMaxRate;
    }

    public void setMaxRate(int maxRate) {
        mMaxRate = maxRate;
    }

    public SparseBooleanArray getStars() {
        return mStars;
    }

    public void setStars(SparseBooleanArray stars) {
        mStars = stars;
    }

    public SparseBooleanArray getRating() {
        return mRating;
    }

    public void setRating(SparseBooleanArray rating) {
        mRating = rating;
    }

    public SparseBooleanArray getAccTypes() {
        return mAccTypes;
    }

    public SparseBooleanArray getMainFacilities() {
        return mMainFacilities;
    }

    public void setMainFacilities(SparseBooleanArray mainFacilities) {
        mMainFacilities = mainFacilities;
    }

    public void setAccTypes(SparseBooleanArray accTypes) {
        mAccTypes = accTypes;
    }

    public void addMainFacility(int facility) {
        if (mMainFacilities == null) {
            mMainFacilities = new SparseBooleanArray();
        }
        mMainFacilities.put(facility, true);
    }

    public void addAccType(int accType) {
        if (mAccTypes == null) {
            mAccTypes = new SparseBooleanArray();
        }
        mAccTypes.put(accType, true);
    }

    public void removeAccType(int accType) {
        if (mAccTypes == null) {
            return;
        }
        mAccTypes.delete(accType);
    }

    public void removeMainFacility(int facility) {
        if (mMainFacilities == null) {
            return;
        }
        mMainFacilities.delete(facility);
    }

    public boolean isEmpty() {
        return mStars == null &&
                mRating == null &&
                mAccTypes == null &&
                mMainFacilities == null &&
                mMinRate == 0 &&
                mMaxRate == 0;
    }

}
