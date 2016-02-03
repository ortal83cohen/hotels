package com.etb.app.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.easytobook.api.model.Accommodation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 * @date 2015-07-01
 */
public class OrderItem implements Parcelable {
    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
    public int rateId;
    public String rateKey;
    public String rateName;
    public double chargeAmount;
    public String currency;
    public String postPaidCurrency;
    public HashMap<String, Double> prepaid;
    public HashMap<String, Double> postpaid;
    public HashMap<String, Double> displayBaseRate;
    public HashMap<String, Double> displayPrice;

    public ArrayList<Accommodation.Rate.PaymentMethods> paymentMethods;
    public Accommodation.Rate.Tags tags;
    public Double extraTax;
    public Double includedTax;

    public OrderItem() {

    }

    public OrderItem(Parcel in) {
        rateId = in.readInt();
        rateKey = in.readString();
        rateName = in.readString();
        chargeAmount = in.readDouble();
        currency = in.readString();
        postPaidCurrency = in.readString();
        displayBaseRate = (HashMap<String, Double>) in.readSerializable();
        displayPrice = (HashMap<String, Double>) in.readSerializable();
        postpaid = (HashMap<String, Double>) in.readSerializable();
        prepaid = (HashMap<String, Double>) in.readSerializable();
        boolean[] values = new boolean[5];
        in.readBooleanArray(values);
        tags = new Accommodation.Rate.Tags();
        tags.breakfastIncluded = values[0];
        tags.earlyBird = values[1];
        tags.freeCancellation = values[2];
        tags.lastMinute = values[3];
        tags.nonRefundable = values[4];
        extraTax = in.readDouble();
        includedTax = in.readDouble();
        Bundle ratesBundle = in.readBundle();
        paymentMethods = (ArrayList<Accommodation.Rate.PaymentMethods>) ratesBundle.getSerializable("paymentMethods");
    }

    public static OrderItem from(Accommodation.Rate rate, String currency, String postPayedCurrency) {
        OrderItem item = new OrderItem();
        item.rateId = rate.rateId;
        item.rateKey = rate.rateKey;
        item.chargeAmount = rate.payment.prepaid.containsKey(currency)?rate.payment.prepaid.get(currency):123.4; //todo remove this if
        item.rateName = rate.name;
        item.currency = currency;
        item.postPaidCurrency = postPayedCurrency;
        item.prepaid = rate.payment.prepaid;
        item.postpaid = rate.payment.postpaid;
        item.displayBaseRate = rate.displayBaseRate;
        item.displayPrice = rate.displayPrice;
        item.tags = rate.tags;
        item.extraTax = 0.0;
        item.includedTax = 0.0;
        for (Accommodation.Rate.TaxesAndFees taxesAndFees : rate.taxesAndFees) {
            if (taxesAndFees.displayIncluded) {
                item.includedTax += taxesAndFees.totalValue.containsKey(item.currency)?taxesAndFees.totalValue.get(item.currency):123.4;//todo remove this if
            } else {
                item.extraTax += taxesAndFees.totalValue.containsKey(item.currency)?taxesAndFees.totalValue.get(item.currency):123.4;//todo remove this if
            }
        }
        item.paymentMethods = rate.paymentMethods;
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rateId);
        dest.writeString(rateKey);
        dest.writeString(rateName);
        dest.writeDouble(chargeAmount);
        dest.writeString(currency);
        dest.writeString(postPaidCurrency);
        dest.writeSerializable(displayBaseRate);
        dest.writeSerializable(displayPrice);
        dest.writeSerializable(postpaid);
        dest.writeSerializable(prepaid);
        boolean[] values = new boolean[]{
                tags.breakfastIncluded,
                tags.earlyBird,
                tags.freeCancellation,
                tags.lastMinute,
                tags.nonRefundable
        };
        dest.writeBooleanArray(values);
        dest.writeDouble(extraTax);
        dest.writeDouble(includedTax);
        Bundle rateBundle = new Bundle();
        rateBundle.putSerializable("paymentMethods", paymentMethods);
        dest.writeBundle(rateBundle);
    }

}
