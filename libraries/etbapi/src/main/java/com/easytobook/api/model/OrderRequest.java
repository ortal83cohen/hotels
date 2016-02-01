package com.easytobook.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 * @date 2015-04-19
 */
public class OrderRequest {

    private String lang;
    private String currency;
    private String customerIP;
    private Personal personal;
    private Payment payment;
    private List<Rate> rates;

    OrderRequest() {
    }

    private static class Rate {
        String rateKey;
        int rateCount;
        List<String> beds;
        String remarks;
    }

    public static class Builder {
        private String mLang;
        private String mCurrency;
        private String mCustomerIP;
        private Payment mPayment;
        private List<Rate> mRates;
        private Personal mPersonal;

        public Builder setLang(String lang) {
            mLang = lang;
            return this;
        }

        public Builder setCurrency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder setCustomerIP(String customerIP) {
            mCustomerIP = customerIP;
            return this;
        }

        public Builder setCreditCard(String type, String number, String cvc, String firstName, String lastName, int expMonth, int expYear) {
            if (mPayment == null) {
                mPayment = new Payment();
            }
            mPayment.type = type;
            Payment.CreditCardData data = new Payment.CreditCardData();
            data.ccNr = number;
            data.ccCvc = cvc;
            data.ccFirstName = firstName;
            data.ccLastName = lastName;
            data.ccExpiryMonth = expMonth;
            data.ccExpiryYear = expYear;
            mPayment.data = data;
            return this;
        }

        public Builder setPayment(String type, Payment.Data data) {
            if (mPayment == null) {
                mPayment = new Payment();
            }
            mPayment.type = type;
            mPayment.data = data;
            return this;
        }

        public Builder setBillingAddress(String country, String state, String city, String address, String postalCode) {
            if (mPayment == null) {
                mPayment = new Payment();
            }
            Payment.Address billingAddress = new Payment.Address();

            billingAddress.country = country;
            billingAddress.state = state;
            billingAddress.city = city;
            billingAddress.address = address;
            billingAddress.postalCode = postalCode;

            mPayment.billingAddress = billingAddress;
            return this;
        }

        public Builder setRates(List<Rate> rates) {
            mRates = rates;
            return this;
        }

        public Builder addRate(String rateKey, int rateCount, List<String> beds, String remarks) {
            if (mRates == null) {
                mRates = new ArrayList<>();
            }
            Rate rate = new Rate();
            rate.rateKey = rateKey;
            rate.rateCount = rateCount;
            rate.beds = beds;
            rate.remarks = remarks;
            mRates.add(rate);
            return this;
        }

        public Builder setPersonal(String firstName, String lastName, String phone, String country, String email) {
            mPersonal = new Personal();
            mPersonal.firstName = firstName;
            mPersonal.lastName = lastName;
            mPersonal.phone = phone;
            mPersonal.country = country;
            mPersonal.email = email;
            return this;
        }


        public OrderRequest build() {
            OrderRequest request = new OrderRequest();
            request.lang = mLang;
            request.currency = mCurrency;
            request.customerIP = mCustomerIP;
            request.payment = mPayment;
            request.personal = mPersonal;
            request.rates = mRates;
            return request;
        }


    }
}
