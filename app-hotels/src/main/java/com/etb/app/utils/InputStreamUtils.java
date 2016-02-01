package com.etb.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author alex
 * @date 2015-07-12
 */
public class InputStreamUtils {

    public static String readToString(InputStream is) {
        StringBuilder total = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            AppLog.e(e);
        }
        return total.toString();
    }
}
