package com.easytobook.api.contract;

/**
 * @author alex
 * @date 2015-05-25
 */
public class Sort {

    public static class Type {
        public static final String FAVORITES = "fav_asc";
        public static final String PRICE_HIGH_TO_LOW = "price_desc";
        public static final String PRICE_LOW_TO_HIGH = "price_asc";
        public static final String STARS_HIGH_TO_LOW = "stars_desc";
        public static final String STARS_LOW_TO_HIGH = "stars_asc";
        public static final String RATING_HIGH_TO_LOW = "rating_desc";
        public static final String RATING_LOW_TO_HIGH = "rating_asc";
        public static final String DISTANCE = "distance_asc";
    }
}
