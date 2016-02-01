package com.etb.app.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.etb.app.BuildConfig;

import java.util.IllegalFormatException;
import java.util.Locale;

/**
 * @author alex
 * @date 2015-05-11
 */
public class AppLog {

    public static final boolean DEBUG;
    private final static String TAG = "EtbApp";

    static {
        DEBUG = BuildConfig.DEBUG ? BuildConfig.DEBUG : Log.isLoggable("EtbApp", Log.DEBUG);
    }

    public static void d(String msg) {
        if (DEBUG) Log.d(TAG, format(msg));
        Crashlytics.log(Log.DEBUG, TAG, msg);
    }

    public static void d(final String msg, final Object... params) {
        if (DEBUG) Log.d(TAG, format(msg, params));
        Crashlytics.log(Log.DEBUG, TAG, format(msg, params));
    }

    public static void v(String msg) {
        Log.v(TAG, format(msg));
    }

    public static void e(String msg) {
        Log.e(TAG, format(msg));
        Crashlytics.log(Log.ERROR, TAG, msg);
    }

    public static void e(String msg, Throwable tr) {
        Log.e(TAG, format(msg), tr);
        Crashlytics.logException(tr);
    }

    public static void e(Throwable tr) {
        Log.e(TAG, tr.getMessage(), tr);
        Crashlytics.logException(tr);
    }

    public static void e(String msg, final Object... params) {
        Log.e(TAG, format(msg, params));
        Crashlytics.log(Log.ERROR, TAG, msg);
    }

    public static void w(String msg) {
        Log.v(TAG, format(msg));
    }

    public static void v(String msg, final Object... params) {
        Log.v(TAG, format(msg, params));
    }

    private static String format(final String msg, final Object... array) {
        String formatted;
        if (array == null || array.length == 0) {
            formatted = msg;
        } else {
            try {
                formatted = String.format(Locale.US, msg, array);
            } catch (IllegalFormatException ex) {
                e("IllegalFormatException: formatString='%s' numArgs=%d", msg, array.length);
                formatted = msg + " (An error occurred while formatting the message.)";
            }
        }
        final StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
        String string = "<unknown>";
        for (int i = 2; i < stackTrace.length; ++i) {
            final String className = stackTrace[i].getClassName();
            if (!className.equals(AppLog.class.getName())) {
                final String substring = className.substring(1 + className.lastIndexOf(46));
                string = substring.substring(1 + substring.lastIndexOf(36)) + "." + stackTrace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), string, formatted);
    }


    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        Crashlytics.log(Log.INFO, TAG, msg);
    }

}
