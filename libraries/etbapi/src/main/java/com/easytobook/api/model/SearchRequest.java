package com.easytobook.api.model;

import android.os.Parcelable;

import com.easytobook.api.model.search.Filter;
import com.easytobook.api.model.search.Type;

/**
 * @author ortal
 * @date 2015-05-20
 */
public class SearchRequest extends HotelRequest {

    private Type mType;
    private Sort mSort;
    private Filter mFilter;


    public SearchRequest() {
        super();
        setSort(com.easytobook.api.contract.Sort.Type.FAVORITES);
    }

    public Sort getSort() {
        return mSort;
    }

    public void setSort(String type) {
        if (type == null) {
            mSort = null;
            return;
        }
        if (mSort == null) {
            mSort = new Sort();
        }
        mSort.type = type;
    }


    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public static class Sort {
        public String type;
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = createNewFilter();
        }

        return mFilter;
    }

    public void setFilter(Filter filter) {
        mFilter = filter;
    }

    public boolean haveFilter() {
        return mFilter != null && !mFilter.isEmpty();
    }

    public void removeFilter() {
       mFilter = null;
    }

    protected Filter createNewFilter() {
        return new Filter();
    }
}