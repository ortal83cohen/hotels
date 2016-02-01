package com.etb.app.core.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author alex
 * @date 2015-06-15
 */
public class Review {
    public Author author;
    @SerializedName("date_of_visit")
    public int timestampVisit;
    @SerializedName("date_published")
    public int timestampPublished;

    public String language;
    @SerializedName("purpose_of_trip")
    public String purposeOfTrip;
    public float rating;
    public int recommend;
    public Ratings subratings;
    public String text;
    public String title;

    public static class Author {
        public String name;
        public String location;
    }
}
