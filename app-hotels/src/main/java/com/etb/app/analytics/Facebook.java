package com.etb.app.analytics;

import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * @author alex
 * @date 2015-05-26
 */
public class Facebook {

    private android.content.Context app;

    public Facebook(Context app) {
        this.app = app;
    }

    public void initialize() {
        FacebookSdk.sdkInitialize(this.app);

    }

    public void resume(Context context) {
        AppEventsLogger.activateApp(context);
    }

    public void pause(Context context) {
        AppEventsLogger.deactivateApp(context);
    }


}
