package com.etb.app.etbapi;

import com.easytobook.api.utils.HttpLoggingInterceptor;
import com.etb.app.BuildConfig;
import com.etb.app.utils.AppLog;

/**
 * @author alex
 * @date 2015-10-02
 */
public class RetrofitLogger implements HttpLoggingInterceptor.Logger {

    public static HttpLoggingInterceptor create() {
        HttpLoggingInterceptor.Level level = BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC;
        return new HttpLoggingInterceptor(new RetrofitLogger(), level);
    }

    @Override
    public void log(String message) {
        AppLog.d(message);
    }
}
