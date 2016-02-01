package com.etb.app.etbapi;

import com.easytobook.api.EtbApi;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.URI;

/**
 * @author alex
 * @date 2015-09-22
 */
public class CacheUtils {

    public static boolean isSearchRequest(URI uri) {
        return uri.getPath().startsWith(EtbApi.PATH_ACCOMMODATIONS);
    }

    public static boolean isRetrieveOrderRequest(Request request) throws IOException {
        // retrieve order request
        return "GET".equals(request.method()) && request.uri().getPath().startsWith(EtbApi.PATH_ORDERS);
    }

    public static boolean isCachableRequest(Request request) throws IOException {
        return isSearchRequest(request.uri()) || isRetrieveOrderRequest(request);
    }
}
