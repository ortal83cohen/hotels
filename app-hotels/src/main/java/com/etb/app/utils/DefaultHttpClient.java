package com.etb.app.utils;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author alex
 * @date 2015-07-01
 */
public class DefaultHttpClient extends OkHttpClient {
    static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s

    public DefaultHttpClient(Context context) {
        super();
        setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        HttpClientConfig.apply(this, context);
    }

}
