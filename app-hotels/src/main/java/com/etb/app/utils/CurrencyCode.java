package com.etb.app.utils;

import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import java.util.Currency;
import java.util.Locale;

/**
 * @author alex
 * @date 2015-07-23
 */
public class CurrencyCode {

    public static String detect(String countryCode) {

        Locale locale;
        if (TextUtils.isEmpty(countryCode)) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(Locale.getDefault().getLanguage(), countryCode);
        }
        String currencyCode;
        try {
            currencyCode = Currency.getInstance(locale).getCurrencyCode();
        } catch (Exception e) {
            AppLog.e(e);
            // Fallback
            currencyCode = "EUR";
        }
        Crashlytics.log("Detected countryCode: '" + countryCode + "', Detected currencyCode: '" + currencyCode + "', locale: '" + locale.toString() + "'");
        AppLog.d("Detected countryCode: '" + countryCode + "', currencyCode: '" + currencyCode + "'");
        return currencyCode;
    }
}
