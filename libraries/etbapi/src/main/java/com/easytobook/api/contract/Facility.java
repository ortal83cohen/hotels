package com.easytobook.api.contract;

/**
 * @author alex
 * @date 2015-04-28
 */
public class Facility {

    public static class Main {
        public static final int DISABLED = 0;
        public static final int INTERNET = 1;
        public static final int PARKING = 2;
        public static final int PETS_ALLOWED = 3;
        public static final int CHILD_DISCOUNTS = 4;
        public static final int SWIMMING_POOL = 5;
        public static final int AIR_CONDITIONING = 6;
        public static final int FITNESS_CENTER = 7;
        public static final int NON_SMOKING = 8;
    }

    public static class Extended {
        public static final String ID_INTERNET_WIRED = "wiredInternet";
        public static final String ID_INTERNET_WIRELESS = "wirelessInternet";
        public static final String ID_PARKING = "parking";
        public static final String ID_SHUTTLE = "shuttle";
    }

    public static class Category {
        public static final int Service = 1;
        public static final int General = 2;
        public static final int Internet = 3;
        public static final int Parking = 4;
        public static final int Shuttle = 5;
        public static final int Kitchen = 6;
        public static final int Bathroom = 7;
        public static final int Electronics = 8;
        public static final int ExtraCommonAreas = 9;
        public static final int Entertainment = 10;
        public static final int BusinessFacilities = 11;
        public static final int Activities = 12;
        public static final int WellnessFacilities = 13;
        public static final int Shops = 14;
        public static final int Connectivity = 15;
        public static final int Views = 16;
        public static final int Bedding = 17;
        public static final int SwimmingPoolFacilities = 18;
        public static final int Television = 19;
    }
}
