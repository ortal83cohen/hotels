package com.etb.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.easytobook.api.model.search.ViewPortType;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * @author user
 * @date 2015-10-08
 */
public class MapSelectedViewPort extends ViewPortType implements Parcelable, LocationWithTitle {
    private String mLastSearch = "";
    public static final Creator<MapSelectedViewPort> CREATOR = new Creator<MapSelectedViewPort>() {
        @Override
        public MapSelectedViewPort createFromParcel(Parcel in) {
            return new MapSelectedViewPort(in);
        }

        @Override
        public MapSelectedViewPort[] newArray(int size) {
            return new MapSelectedViewPort[size];
        }
    };
    private String mTitle;

    protected MapSelectedViewPort(Parcel in) {
        mTitle = in.readString();
        setNortheastLat(in.readDouble());
        setNortheastLon(in.readDouble());
        setSouthwestLat(in.readDouble());
        setSouthwestLon(in.readDouble());

    }

    public MapSelectedViewPort(String title, double northeastLat, double northeastLon, double southwestLat, double southwestLon) {
        super(northeastLat, northeastLon, southwestLat, southwestLon);
        mTitle = title;
    }

    public MapSelectedViewPort(String title, LatLngBounds latLngBounds) {
        super(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude, latLngBounds.southwest.latitude, latLngBounds.southwest.longitude);
        mTitle = "Selected on map";
        if (title != mTitle) {
            mLastSearch = title;
        }
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

    public String getLastSearch() {
        return mLastSearch;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        mNortheastLat = latLngBounds.northeast.latitude;
        mNortheastLon = latLngBounds.northeast.longitude;
        mSouthwestLat = latLngBounds.southwest.latitude;
        mSouthwestLon = latLngBounds.southwest.longitude;
    }
}
