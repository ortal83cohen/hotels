package com.easytobook.api.contract;


import android.support.v4.util.SimpleArrayMap;

import java.util.Locale;

/**
 * @author alex
 * @date 2015-04-19
 */
public class Language {

    public static final String DEFAULT = "en";
    private static final int LANGUAGES_COUNT = 11;
    private static final SimpleArrayMap<String, Boolean> sSupportedLangs = new SimpleArrayMap<>(LANGUAGES_COUNT);

    static {
        sSupportedLangs.put("en", true);
        sSupportedLangs.put("nl", true);
        sSupportedLangs.put("es", true);
        sSupportedLangs.put("de", true);
        sSupportedLangs.put("fr", true);
        sSupportedLangs.put("it", true);
        sSupportedLangs.put("pt", true);
        sSupportedLangs.put("sv", true);
        sSupportedLangs.put("no", true);
        sSupportedLangs.put("ru", true);
        sSupportedLangs.put("ja", true);
    }

    public static String getDefault() {
        synchronized (sSupportedLangs) {
            String langCode = Locale.getDefault().getLanguage();
            if (sSupportedLangs.get(langCode) == null) {
                return DEFAULT;
            }
            return langCode;
        }
    }

    public static String getSupported(String langCode) {
        return getSupported(langCode, DEFAULT);
    }

    public static String getSupported(String langCode, String defaultLang) {
        synchronized (sSupportedLangs) {
            if (sSupportedLangs.get(langCode) == null) {
                return defaultLang;
            }
            return langCode;
        }
    }

}
