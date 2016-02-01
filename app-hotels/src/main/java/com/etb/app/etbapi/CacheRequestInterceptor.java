package com.etb.app.etbapi;

import com.easytobook.api.EtbApi;
import com.etb.app.utils.NetworkUtilities;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author alex
 * @date 2015-07-08
 */
public class CacheRequestInterceptor implements Interceptor {
    private final static int SEARCH_CACHE_MAX_AGE = 300;

    private NetworkUtilities mNetworkUtilities;

    private int mSearchMaxAge = SEARCH_CACHE_MAX_AGE; // 5 min

    public CacheRequestInterceptor(NetworkUtilities networkUtilities) {
        mNetworkUtilities = networkUtilities;
    }

    public void setSearchMaxAge(int searchMaxAge) {
        mSearchMaxAge = searchMaxAge;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // Modify request to read from cache
        if (CacheUtils.isCachableRequest(request)) {
            if (mNetworkUtilities.isConnected()) {
                request = connectedRequest(request);
            } else {
                request = disconnectedRequest(request);
            }

        }

        return chain.proceed(request);
    }

    private Request disconnectedRequest(Request request) throws IOException {
        URI uri = request.uri();
        // Disconnected take from cache
        if (CacheUtils.isSearchRequest(uri) || CacheUtils.isRetrieveOrderRequest(request)) {
            return request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }
        return request;
    }


    private Request connectedRequest(Request request) throws IOException {
        URI uri = request.uri();

        CacheControl cacheControl;
        if (uri.getPath().equals(EtbApi.PATH_SEARCH)) {
            cacheControl = new CacheControl.Builder().maxAge(mSearchMaxAge, TimeUnit.SECONDS).build();
            return request.newBuilder().cacheControl(cacheControl).build();
        } else if (uri.getPath().startsWith(EtbApi.PATH_ACCOMMODATIONS)) {
            return request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
        } else if (CacheUtils.isRetrieveOrderRequest(request)) {
            return request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
        }


        return request;
    }

}
