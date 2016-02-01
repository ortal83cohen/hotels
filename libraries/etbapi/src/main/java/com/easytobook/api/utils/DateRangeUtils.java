package com.easytobook.api.utils;

/**
 * @author alex
 * @date 2015-11-18
 */
public class DateRangeUtils {

    private static final int DAY_IN_MILLIS = 86400000; //(24 * 60 * 60 * 1000)

    public static int days(long fromMillis, long toMillis) {
        return (int) (toMillis - fromMillis) / DAY_IN_MILLIS;
    }
}
