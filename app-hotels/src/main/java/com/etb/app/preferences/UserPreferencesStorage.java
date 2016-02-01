package com.etb.app.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.etb.app.App;
import com.etb.app.utils.CountryCode;
import com.etb.app.utils.CurrencyCode;

/**
 * @author alex
 * @date 2015-06-14
 */
public class UserPreferencesStorage {
    private static final String FILE_NAME = "user_prefs";

    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_PRICE_SHOW_TYPE = "price_show_type";

    private final SharedPreferences mPrefs;
    private Context mContext;

    public UserPreferencesStorage(Context context) {
        mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mContext = context.getApplicationContext();
    }

    public UserPreferences load() {
        UserPreferences up = new UserPreferences();

        String countryCode = CountryCode.detect(mContext);
        up.setCountryCode(countryCode);

        String currencyCode = mPrefs.getString(KEY_CURRENCY, null);
        if (currencyCode == null) {
            currencyCode = CurrencyCode.detect(countryCode);
        }
        up.setCurrencyCode(currencyCode);

        // TODO: Set according to device/user location?
        up.setPriceShowType(mPrefs.getInt(KEY_PRICE_SHOW_TYPE, UserPreferences.PRICE_SHOW_TYPE_STAY));
        return up;
    }

    public void save(UserPreferences prefs) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(KEY_CURRENCY, prefs.getCurrencyCode());
        edit.putInt(KEY_PRICE_SHOW_TYPE, prefs.getPriceShowType());
        edit.apply();
    }
}
