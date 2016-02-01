package com.etb.app.member.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author alex
 * @date 2015-08-19
 */
public abstract class Event {
    public int id;

    public int type;

    @SerializedName("ref_id")
    public int refId;

    @SerializedName("ts")
    public int timestamp;
}
