package com.easytobook.api.model;

import java.util.HashMap;

public class Meta {

    public int statusCode;

    public int totalNrOverall;

    public int totalNr;

    public int limit;

    public int offset;

    public String clientCurrency;

    public int errorCode;

    public String errorMessage;

    public FilterNumbers filterNrs;

    public Price priceFrom;

    public Price priceTo;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Code] ");
        sb.append(statusCode);
        if (errorCode > 0) {
            sb.append("[Error] ");
            sb.append(errorCode);
            sb.append(" ");
            sb.append(errorMessage);
        }
        return sb.toString();
    }

    public static class FilterNumbers {
        // TODO: format issue public List<Integer> stars;
        public HashMap<Integer, Integer> rating;
        public HashMap<Integer, Integer> accType;
        public HashMap<Integer, Integer> facilities;
    }

    public static class Price {
        public HashMap<String, Double> totalNetRate;
        public HashMap<String, Double> displayPrice;
    }
}