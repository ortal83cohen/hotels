package com.easytobook.api.model.search;

import java.util.ArrayList;

public class ListType extends Type {

    private ArrayList<String> mHotels = null;

    public ListType() {
        super(LIST);
    }


    public ListType(ArrayList<String> hotels) {
        super(LIST);
        mHotels = hotels;
    }

    @Override
    public String getContext() {
        return mHotels.toString().replace("[", "").replace("]", "")
                .replace(", ", ",");
    }


}