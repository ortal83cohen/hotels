package com.easytobook.api.model;

public class Payment {
    public String type;
    public Data data;
    public Address billingAddress;

    public static class Data {
    }

    public static class CreditCardData extends Data {
        public String ccNr;
        public String ccCvc;
        public String ccFirstName;
        public String ccLastName;
        public int ccExpiryMonth;
        public int ccExpiryYear;
    }

    public static class Address {
        public String country;
        public String state;
        public String city;
        public String address;
        public String postalCode;
    }
}