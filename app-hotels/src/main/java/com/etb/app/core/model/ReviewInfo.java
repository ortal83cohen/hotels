package com.etb.app.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author alex
 * @date 2015-06-11
 */
public class ReviewInfo {
    @SerializedName("iso_date")
    public String isoDate;
    public String purpose;
    public Review review;
    @SerializedName("traveller_id")
    public int travellerId;
}
