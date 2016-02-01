package com.etb.app.member.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author alex
 * @date 2015-08-06
 */
public class User {

    public int id;

    public String email;

    public int status;

    public Profile profile;

    @SerializedName("provider_id")
    public int providerId;


}
