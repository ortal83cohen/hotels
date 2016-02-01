package com.etb.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.easytobook.api.model.search.Filter;

/**
 * @author alex
 * @date 2015-12-03
 */
public class HotelListFilter extends Filter implements Parcelable {

    public HotelListFilter() {
        super();
    }

    protected HotelListFilter(Parcel in) {
        setMinRate(in.readInt());
        setMaxRate(in.readInt());
        setAccTypes(in.readSparseBooleanArray());
        setMainFacilities(in.readSparseBooleanArray());
        setStars(in.readSparseBooleanArray());
        setRating(in.readSparseBooleanArray());
    }

    public static final Creator<HotelListFilter> CREATOR = new Creator<HotelListFilter>() {
        @Override
        public HotelListFilter createFromParcel(Parcel in) {
            return new HotelListFilter(in);
        }

        @Override
        public HotelListFilter[] newArray(int size) {
            return new HotelListFilter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getMinRate());
        dest.writeInt(getMaxRate());
        dest.writeSparseBooleanArray(getAccTypes());
        dest.writeSparseBooleanArray(getMainFacilities());
        dest.writeSparseBooleanArray(getStars());
        dest.writeSparseBooleanArray(getRating());
    }
}
