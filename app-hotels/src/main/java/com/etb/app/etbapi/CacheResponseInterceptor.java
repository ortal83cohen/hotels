package com.etb.app.etbapi;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author alex
 * @date 2015-07-08
 */
public class CacheResponseInterceptor implements Interceptor {
    public static final int CACHE_AGE_DEFAULT = 600;
    public static final int CACHE_AGE_MAX = 365 * 24 * 60 * 60; // 365 days

    private int cacheAge = CACHE_AGE_DEFAULT;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);

        if (originalResponse.cacheControl().noCache()) {
            // Modify response to store availability for 10 minutes
            if (CacheUtils.isSearchRequest(request.uri())) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "max-age=" + cacheAge)
                        .build();
                // Modify response to store order details for 1 year
            } else if (CacheUtils.isRetrieveOrderRequest(request)) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "max-age=" + CACHE_AGE_MAX)
                        .build();
            }
        }

        return originalResponse;
    }

    public void setCacheAge(int cacheAge) {
        this.cacheAge = cacheAge;
    }
}
