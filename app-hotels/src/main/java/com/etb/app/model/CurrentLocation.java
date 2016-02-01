package com.etb.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentLocation extends Location {
    public static final Parcelable.Creator<CurrentLocation> CREATOR = new Parcelable.Creator<CurrentLocation>() {
        public CurrentLocation createFromParcel(Parcel in) {
            return new CurrentLocation(in);
        }

        public CurrentLocation[] newArray(int size) {
            return new CurrentLocation[size];
        }
    };

    public CurrentLocation(Parcel in) {
        super(in);
    }

    public CurrentLocation() {
    }
}