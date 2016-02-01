package com.etb.app.events;

import com.etb.app.utils.AppLog;
import com.squareup.otto.Bus;

/**
 * @author alex
 * @date 2015-07-06
 */
public class Events {
    private static final Bus sBus = new Bus();

    public static void post(Object event) {
        AppLog.v("Posting event: " + event);
        sBus.post(event);
    }

    public static void register(Object object) {
        AppLog.v("Registering: " + object);
        sBus.register(object);
    }

    public static void unregister(Object object) {
        AppLog.v("Unregistering: " + object);
        sBus.unregister(object);
    }
}
