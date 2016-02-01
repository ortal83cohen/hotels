package com.etb.app.etbapi;

import android.content.Context;
import android.content.pm.PackageManager;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author ortal
 * @date 2015-07-21
 */
public class UserAgentInterceptor implements Interceptor {

    Context mContext;


    public UserAgentInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String versionName;
        try {
            versionName = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        String agent = System.getProperty("http.agent");
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "ETB-Android/" + versionName + agent.substring(agent.indexOf("(")))
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}