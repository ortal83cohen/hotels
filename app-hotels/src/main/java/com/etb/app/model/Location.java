package com.etb.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.easytobook.api.model.search.SprType;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author alex
 * @date 2015-04-26
 */
public class Location extends SprType implements Parcelable {

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    private String mTitle;


    public Location() {

    }

    public Location(String title, double latitude, double longitude) {
        super(latitude, longitude);
        mTitle = title;
    }

    public Location(String title, LatLng loc) {
        super(loc.latitude, loc.longitude);
        mTitle = title;
    }

    public Location(Parcel in) {
        mTitle = in.readString();
        setLatLng(in.readDouble(), in.readDouble());
    }


    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeDouble(getLatitude());
        dest.writeDouble(getLongitude());
    }

}
