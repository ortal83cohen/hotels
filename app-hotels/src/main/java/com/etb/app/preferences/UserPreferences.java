package com.etb.app.preferences;

import com.easytobook.api.contract.Language;

/**
 * @author alex
 * @date 2015-06-14
 */
public class UserPreferences {
    public static final int PRICE_SHOW_TYPE_NIGHT = 0;
    public static final int PRICE_SHOW_TYPE_STAY = 1;

    private String mCurrencyCode;
    private String mLang = null;
    private int mPriceShowType;
    private String mCountryCode;

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        mCurrencyCode = currencyCode;
    }

    public void setLanguageCode(String languageCode) {
        mLang = languageCode;
    }

    public String getLang() {
        if (mLang == null) {
            mLang = Language.getDefault();
        }
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
    }

    public int getPriceShowType() {
        return mPriceShowType;
    }

    public void setPriceShowType(int priceShowType) {
        mPriceShowType = priceShowType;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }
}
