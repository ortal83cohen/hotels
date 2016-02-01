package com.etb.app.member.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author alex
 * @date 2015-08-19
 */
public class BookingEvent extends Event {
    @SerializedName("reference")
    public Booking booking;

    public BookingEvent(Booking booking) {
        this.booking = booking;
    }

    public static class Booking {
        public String orderId;
        @SerializedName("bok_ref")
        public String reference;
        public String city;
        public String country;
        @SerializedName("hotel_name")
        public String hotelName;
        @SerializedName("hotel_stars")
        public float hotelStars;
        @SerializedName("hotel_image")
        public String hotelImage;
        public String arrival;
        public String departure;
        public Integer rooms;
        @SerializedName("rate_name")
        public String rateName;
        public List<Integer> tags;
        public String currency;
        @SerializedName("total_value")
        public Double totalValue;
        @SerializedName("is_cancelled")
        public Boolean isCancelled;
        public String confirmationId;
        public String rateId;

        public Booking(String orderId, String reference, String city, String country, String hotelName, String hotelStars, String hotelImage, String arrival, String departure, String rooms, String rateName, String tags, String currency, String totalValue, String isCancelled, String rateId, String confirmationId) {
            this.orderId = orderId;
            this.reference = reference;
            this.city = city;
            this.country = country;
            this.hotelName = hotelName;
            this.hotelStars = Float.valueOf(hotelStars);
            this.hotelImage = hotelImage;
            this.arrival = arrival;
            this.departure = departure;
            this.rooms = Integer.valueOf(rooms);
            this.rateName = rateName;
//            this.tags = tags;
            this.currency = currency;
            this.totalValue = Double.valueOf(totalValue);
            this.isCancelled = Boolean.valueOf(isCancelled);
            this.rateId = rateId;
            this.confirmationId = confirmationId;
        }

    }
}
