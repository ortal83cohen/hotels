package com.etb.app;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.etb.app.events.EventProducers;
import com.etb.app.events.Events;
import com.etb.app.utils.AppLog;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.SilentLogger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class HotelsApplication extends Application implements App.Provider {

    private ObjectGraph mObjectGraph;

    public static HotelsApplication get(Context context) {
        return (HotelsApplication) context.getApplicationContext();
    }

    public static ObjectGraph provide(Context context) {
        return get(context).getObjectGraph();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mRefWatcher = LeakCanary.install(this);
        Fabric.with((new Fabric.Builder(this)).kits(new Crashlytics()).logger(new SilentLogger()).build());
        mObjectGraph = new ObjectGraph(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("OpenSans_Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Events.register(new EventProducers(this));

        if (BuildConfig.DEBUG) {
//            Picasso.with(this).setIndicatorsEnabled(true);
            Picasso.with(this).setLoggingEnabled(true);
        }

        AppLog.d("Device class: " + mObjectGraph.getDeviceClass());

    }

    @Override
    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }


}
