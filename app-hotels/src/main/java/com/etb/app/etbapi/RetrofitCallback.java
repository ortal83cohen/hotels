package com.etb.app.etbapi;

import android.content.Context;
import android.widget.Toast;

import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.analytics.AnalyticsCalls;
import com.etb.app.utils.AppLog;
import com.squareup.okhttp.ResponseBody;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author alex
 * @date 2015-07-12
 */
public abstract class RetrofitCallback<T> implements Callback<T> {
    protected WeakReference<Context> mWeakContext;

    public void attach(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public void detach() {
        mWeakContext.clear();
    }

    protected abstract void failure(ResponseBody errorBody, boolean isOffline);

    protected abstract void success(T body, Response<T> response);

    protected void notifyFailure(Throwable exception) {
        AppLog.e(exception);
    }


    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            success(response.body(), response);
        } else {
            notifyFailure(new RetrofitError(response));
            failure(response.errorBody(), false);
            AnalyticsCalls.get().trackRetrofitFailure(response.raw().request().url().toString(), response.message(), response.raw().request().body() != null ? response.raw().request().body().toString() : "");
        }
    }

    @Override
    public void onFailure(Throwable t) {
        boolean isOffline = false;
        if (mWeakContext != null && mWeakContext.get() != null) {
            if (!App.provide(mWeakContext.get()).netUtils().isOnline()) {
                isOffline = true;
                failureOffline();
            }
            AnalyticsCalls.get().trackRetrofitFailure("Offline", t.getMessage(), "");
        }

        notifyFailure(t);
        failure(null, isOffline);
    }

    protected void failureOffline() {
        Toast.makeText(mWeakContext.get(), R.string.you_are_offline, Toast.LENGTH_SHORT).show();
    }

}
