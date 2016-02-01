package com.etb.app.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author alex
 * @date 2015-07-08
 */
public class NetworkUtilities {
    private ConnectivityManager mConnectivityManager;

    public NetworkUtilities(ConnectivityManager connectivityManager) {
        mConnectivityManager = connectivityManager;
    }

    public boolean isConnected() {
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return (netInfo != null) && (netInfo.isAvailable()) && (netInfo.isConnected());
    }

}
