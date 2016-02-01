package com.easytobook.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 * @date 2015-04-28
 */
public class Order {
    public int orderId;
    public Personal personal;
    public Payment payment;
    public List<Rate> rates;

    public static class Rate {
        public int accommodationId;
        public Accommodation accommodation;
        public int rateId;
        public String statusCode;
        public String confirmationId;
        public String capacity;
        public String password;
        public String name;
        public int rateCount;
        public String checkIn;
        public String checkOut;
        // TODO: public Charge charge
        public Breakfast breakfast;
        public CancellationPolicy cancellationPolicy;
        public String cancellationPolicyText;
        public List<String> beds;
        public String remarks;
        public Charge charge;
        public ArrayList<TaxesAndFees> taxesAndFees;

    }
}
