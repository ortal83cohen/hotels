package com.easytobook.api.contract;

/**
 * @author alex
 * @date 2015-04-28
 */
public class Cancellation {

    public static class Policy {
        public static final int UNKNOWN = -1;
        public static final int NON_REFUNDABLE = 0;
        public static final int REFUNDABLE_HOUR_ON_ARRIVAL_DAY = 1;
        public static final int REFUNDABLE_DAYS_BEFORE_ARRIVAL = 2;
    }

    public static class Penalty {
        public static final int UNKNOWN = 0;
        public static final int FIXED_PRICE = 1;
        public static final int PERCENTAGE = 2;
        public static final int NIGHTS = 3;
    }

    public static class NoShowType {
        public static final int FIRST_NIGHT = 1;
        public static final int FULL_STAY = 2;
    }
}
