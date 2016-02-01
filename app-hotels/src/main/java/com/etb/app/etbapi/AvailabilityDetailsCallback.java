package com.etb.app.etbapi;

import android.widget.Toast;

import com.easytobook.api.model.DetailsResponse;
import com.etb.app.R;
import com.etb.app.utils.AppLog;
import com.squareup.okhttp.ResponseBody;

import retrofit.Response;

/**
 * @author alex
 * @date 2015-11-03
 */
public abstract class AvailabilityDetailsCallback extends RetrofitCallback<DetailsResponse> {
    protected boolean isDatesRequest = true;

    public void setIsDatesRequest(boolean isDatesRequest) {
        this.isDatesRequest = isDatesRequest;
    }

    @Override
    protected void failure(ResponseBody response, boolean isOffline) {
        if (!isOffline) {
            onNoAvailability(null);
        }
    }

    @Override
    protected void success(DetailsResponse detailsResponse, Response<DetailsResponse> response) {
        if (mWeakContext == null || mWeakContext.get() == null) {
            return;
        }

        if (isDatesRequest && detailsResponse.accommodation.rates == null) {
            onNoAvailability(detailsResponse);
            AppLog.e(new Throwable("No availability"));
            return;
        }
        onDetailsResponse(detailsResponse);
    }

    protected abstract void onDetailsResponse(DetailsResponse detailsResponse);

    protected void onNoAvailability(DetailsResponse detailsResponse) {
        if (mWeakContext != null && mWeakContext.get() != null) {
            Toast.makeText(mWeakContext.get(), R.string.hotel_not_available, Toast.LENGTH_SHORT).show();
        }
    }
}
