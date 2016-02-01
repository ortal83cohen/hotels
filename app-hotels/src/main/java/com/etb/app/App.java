package com.etb.app;

import android.content.Context;

/**
 * @author alex
 * @date 2015-05-11
 */
public class App {
    public static ObjectGraph provide(Context context) {
        return ((Provider) context.getApplicationContext()).getObjectGraph();
    }

    public interface Provider<T extends ObjectGraph> {
        T getObjectGraph();
    }
}
