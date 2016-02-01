package com.etb.app.events;

import com.easytobook.api.model.SearchRequest;

/**
 * @author alex
 * @date 2015-07-06
 */
public class SearchRequestEvent {
    private final int mOffset;
    private SearchRequest mHotelListRequest;

    public SearchRequestEvent(SearchRequest hotelsRequest, int offset) {
        mHotelListRequest = hotelsRequest;
        mOffset = offset;
    }

    public SearchRequest getSearchRequest() {
        return mHotelListRequest;
    }

    public int getOffset() {
        return mOffset;
    }
}
