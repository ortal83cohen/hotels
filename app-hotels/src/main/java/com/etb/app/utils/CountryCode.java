package com.etb.app.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.etb.app.App;

import java.util.Locale;

/**
 * @author alex
 * @date 2015-07-23
 */
public class CountryCode {

    public static String detect(Context context) {

        String countryCode = getNetworkCountry(context);

        if (TextUtils.isEmpty(countryCode)) {
            return context.getResources().getConfiguration().locale.getCountry().toUpperCase(Locale.US);
        }

        return countryCode;
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getNetworkCountry(Context context) {
        try {
            final TelephonyManager tm = App.provide(context).getTelephonyManager();
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toUpperCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        } catch (Exception ex) {
            AppLog.e(ex);
        }
        return null;
    }
}
