package com.easytobook.api.utils;

import android.text.TextUtils;

/**
 * @author alex
 * @date 2015-10-25
 */
public class ImageUtils {

    private static final String pathPrefix = "images/";
    private static final int pathPrefixLength = pathPrefix.length();

    public static String resizeUrl(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        // http://d1pa4et5htdsls.cloudfront.net/images/hotel/6/114/163075-1330700784-rev1-img1-original.jpg
        int urlLength = url.length();
        int prefixIndex = TextUtils.indexOf(url, pathPrefix, 0, urlLength);
        if (prefixIndex <= 0) {
            // Path is not recognized
            return url;
        }

        String urlStart = TextUtils.substring(url, 0, prefixIndex + pathPrefixLength);
        String urlEnd = TextUtils.substring(url, prefixIndex + pathPrefixLength, urlLength);

        //urlStart + w240-h188-c-0/ +
        return urlStart.concat("w").concat(String.valueOf(width)).concat("-h").concat(String.valueOf(height)).concat("-c-0/").concat(urlEnd);
    }
}
