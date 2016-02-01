package com.etb.app.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.etb.app.R;

import java.util.Locale;
import java.util.TreeMap;

/**
 * @author alex
 * @date 2015-05-04
 */
public class CountryAdapter extends ArrayAdapter<String> {
    private TreeMap<String, String> mCodesMap;

    public CountryAdapter(Context context) {
        super(context, R.layout.spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String[] codes = Locale.getISOCountries();

        mCodesMap = new TreeMap<>();

        for (String code : codes) {
            Locale locale = new Locale("", code);
            mCodesMap.put(locale.getDisplayCountry(), code);
        }


        addAll(mCodesMap.keySet());


    }

    public String getISOCode(String countryName) {
        return mCodesMap.get(countryName);
    }

    public String getCountryName(String isoCode) {
        Locale locale = new Locale("", isoCode);
        return locale.getDisplayCountry();
    }

    public TreeMap getDate() {
        return mCodesMap;
    }

    public String getISOCodeAt(int position) {
        return mCodesMap.get(super.getItem(position));
    }
}
