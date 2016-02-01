package com.etb.app.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author alex
 * @date 2015-06-15
 */
public class ReviewResponse {
    @SerializedName("current_page")
    public int currentPage;
    @SerializedName("next_page_num")
    public int nextPageNum;

    public List<ReviewInfo> reviews;
}
