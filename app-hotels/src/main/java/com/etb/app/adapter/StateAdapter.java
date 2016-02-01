package com.etb.app.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.etb.app.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author alex
 * @date 2015-05-04
 */
public class StateAdapter extends ArrayAdapter<String> {
    final static public String US = "US";
    final static public String CA = "CA";
    final static public String AU = "AU";
    /// Use LinkedHashMap to preserve keys order
    final static public HashMap<String, String> mUsMap = new LinkedHashMap<String, String>() {{
        put("Alaska", "AK");
        put("Alabama", "AL");
        put("Arkansas", "AR");
        put("Arizona", "AZ");
        put("California", "CA");
        put("Colorado", "CO");
        put("Connecticut", "CT");
        put("District of Columbia", "DC");
        put("Delaware", "DE");
        put("Florida", "FL");
        put("Georgia", "GA");
        put("Hawaii", "HI");
        put("Iowa", "IA");
        put("Idaho", "ID");
        put("Illinois", "IL");
        put("Indiana", "IN");
        put("Kansas", "KS");
        put("Kentucky", "KY");
        put("Louisiana", "LA");
        put("Massachusetts", "MA");
        put("Maryland", "MD");
        put("Maine", "ME");
        put("Michigan", "MI");
        put("Minnesota", "MN");
        put("Missouri", "MO");
        put("Mississippi", "MS");
        put("Montana", "MT");
        put("North Carolina", "NC");
        put("North Dakota", "ND");
        put("Nebraska", "NE");
        put("New Hampshire", "NH");
        put("New Jersey", "NJ");
        put("New Mexico", "NM");
        put("Nevada", "NV");
        put("New York", "NY");
        put("Ohio", "OH");
        put("Oklahoma", "OK");
        put("Oregon", "OR");
        put("Pennsylvania", "PA");
        put("Rhode Island", "RI");
        put("South Carolina", "SC");
        put("South Dakota", "SD");
        put("Tennessee", "TN");
        put("Texas", "TX");
        put("Utah", "UT");
        put("Virginia", "VA");
        put("Vermont", "VT");
        put("Washington", "WA");
        put("Wisconsin", "WI");
        put("West Virginia", "WV");
        put("Wyoming", "WY");
    }};
    final static public HashMap<String, String> mCaMap = new LinkedHashMap<String, String>() {{
        put("Alberta", "AB");
        put("British Columbia", "BC");
        put("Labrador", "LB");
        put("Manitoba", "MB");
        put("New Brunswick", "NB");
        put("Newfoundland", "NF");
        put("Nova Scotia", "NS");
        put("Nunavut", "NU");
        put("North West Terr.", "NW");
        put("Ontario", "ON");
        put("Prince Edward Is.", "PE");
        put("Quebec", "QC");
        put("Saskatchewen", "SK");
        put("Yukon", "YU");
    }};
    final static public HashMap<String, String> mAuMap = new LinkedHashMap<String, String>() {{
        put("Australian Capital Territory", "AC");
        put("New South Wales", "NS");
        put("Northern Territory", "NT");
        put("Queensland", "QL");
        put("South Australia", "SA");
        put("Tasmania", "TS");
        put("Victoria", "VI");
        put("Western Australia", "WA");
    }};
    private HashMap<String, String> mCodesMap;


    public StateAdapter(Context context) {
        super(context, R.layout.spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void setCountryCode(String countryCode) {
        switch (countryCode) {
            case US:
                mCodesMap = mUsMap;
                break;
            case CA:
                mCodesMap = mCaMap;
                break;
            case AU:
                mCodesMap = mAuMap;
                break;
        }

        clear();
        if (mCodesMap != null) {
            addAll(mCodesMap.keySet());
        }
    }

    public String getISOCode(String stateName) {
        if (mCodesMap == null) {
            return null;
        }
        return mCodesMap.get(stateName);
    }

}
