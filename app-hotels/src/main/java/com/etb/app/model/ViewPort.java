package com.etb.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.easytobook.api.model.search.ViewPortType;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * @author user
 * @date 2015-10-08
 */
public class ViewPort extends ViewPortType implements Parcelable, LocationWithTitle {
    public static final Creator<ViewPort> CREATOR = new Creator<ViewPort>() {
        @Override
        public ViewPort createFromParcel(Parcel in) {
            return new ViewPort(in);
        }

        @Override
        public ViewPort[] newArray(int size) {
            return new ViewPort[size];
        }
    };
    private String mTitle;

    protected ViewPort(Parcel in) {
        mTitle = in.readString();
        setNortheastLat(in.readDouble());
        setNortheastLon(in.readDouble());
        setSouthwestLat(in.readDouble());
        setSouthwestLon(in.readDouble());

    }

    public ViewPort() {
    }

    public ViewPort(String title, double northeastLat, double northeastLon, double southwestLat, double southwestLon) {
        super(northeastLat, northeastLon, southwestLat, southwestLon);
        mTitle = title;
    }

    public ViewPort(String title, LatLngBounds latLngBounds) {
        super(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude, latLngBounds.southwest.latitude, latLngBounds.southwest.longitude);
        mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeDouble(getNortheastLat());
        dest.writeDouble(getNortheastLon());
        dest.writeDouble(getSouthwestLat());
        dest.writeDouble(getSouthwestLon());
    }


}
