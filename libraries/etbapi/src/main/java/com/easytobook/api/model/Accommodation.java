package com.easytobook.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Accommodation {

    public int id;

    public String name;

    public String email;

    public String phone;

    public int starRating;

    public Location location;

    public HashMap<String, Double> fromPrice;

    public ArrayList<String> images;

    public String postpaidCurrency;

    public Details details;

    public Summary summary;

    public ArrayList<Integer> mainFacilities;

    public ArrayList<Facility> facilities;

    public ArrayList<Rate> rates;
    private boolean mUnavailable = false;

    public boolean isUnavailable() {
        return mUnavailable;
    }

    public Rate getFirstRate() {
        return this.rates != null && this.rates.size() > 0 ? this.rates.get(0) : null;
    }

    public Rate getRateById(int rateId) {
        if (this.rates == null || this.rates.size() == 0 || rateId == 0) {
            return null;
        }

        for (Rate rate : rates) {
            if (rate.rateId == rateId) {
                return rate;
            }
        }

        return null;
    }

    public String getPostpaidCurrency() {
        return postpaidCurrency;
    }

    public void markUnavailable() {
        mUnavailable = true;
    }

    public static class Location {

        public float lat;

        public float lon;

        @Override
        public String toString() {
            return String.valueOf(lat) + "," + String.valueOf(lon);
        }
    }


    public static class Details {

        public String generalDescription;
        public String importantInfo;
        public String location;
        public String publicTransportation;
        public String checkInFrom;
        public String checkInUntil;
        public String checkOutFrom;
        public String checkOutUntil;
        public String checkInTime;
        public String checkOutTime;
        public int nrOfRooms;
        public PetsPolicy petsPolicy;
        public ArrayList<PostpaidCreditCard> postpaidCreditCards;


        public static class PostpaidCreditCard {
            public String name;
            public String code;

            public PostpaidCreditCard(String name, String code) {
                this.name = name;
                this.code = code;
            }
        }

        public static class PetsPolicy {
            public int petsAllowed;
            public boolean petsAllowedOnRequest;
            public String petsSurcharge;

            public PetsPolicy() {
                this.petsAllowed = 0;
                this.petsAllowedOnRequest = false;
                this.petsSurcharge = "";
            }
        }

    }

    public static class Facility {


        public String id;
        public String name;
        public int category;
        public Data data;

        public Facility(String id, String name, int category, Data data) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.data = data;
        }

        public static class Data {
            public boolean free;
            public int location;

            public Data(boolean free, int location) {
                this.free = free;
                this.location = location;

            }

        }

    }

    public static class Rate {

        public int rateId;

        public String name;

        public ArrayList<String> images;

        public ArrayList<TaxesAndFees> taxesAndFees;

        public Payment payment;

        public ArrayList<PaymentMethods> paymentMethods;

        public int capacity;

//        public HashMap<Integer, ArrayList<String>> beds;

        public String description;

        public String rateKey;

        public Tags tags;

        public HashMap<String, Double> baseRate;

        public HashMap<String, Double> totalNetRate;

        public HashMap<String, Double> displayPrice;

        public HashMap<String, Double> displayBaseRate;

        public CancellationPolicy cancellationPolicy;

        public static class Payment {
            public HashMap<String, Double> prepaid;
            public HashMap<String, Double> postpaid;
        }

        public static class PaymentMethods implements Serializable {
            public String code;
            public String name;
        }

        public static class TaxesAndFees implements Serializable {
            public String name;
            public int type;
            public HashMap<String, Double> totalValue;
            public boolean prepaid;
            public boolean displayIncluded;
        }

        public static class Tags {
            public boolean breakfastIncluded;
            public boolean earlyBird;
            public boolean freeCancellation;
            public boolean lastMinute;
            public boolean nonRefundable;
        }
    }

    public static class Summary {

        public int accType;

        public String address;

        public String city;

        public String country;

        public String state;

        public float erank;

        public int reviewCount;

        public float reviewScore;

        public String zipcode;
    }


}