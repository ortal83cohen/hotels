package com.etb.app.utils;

import android.content.Context;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.preferences.UserPreferences;

import java.text.NumberFormat;

/**
 * @author alex
 * @date 2015-05-06
 */
public class PriceRender {
    private final int mNumOfDays;
    private final NumberFormat mNumberFormatter;
    private final int mPriceShowType;

    public PriceRender(int priceShowType, NumberFormat numberFormatter, int numOfDays) {
        mPriceShowType = priceShowType;
        mNumOfDays = numOfDays;
        mNumberFormatter = numberFormatter;
    }

    public PriceRender(int priceShowType, int numOfDays) {
        mPriceShowType = priceShowType;
        mNumberFormatter = null;
        mNumOfDays = numOfDays;
    }

    public static PriceRender create(int priceShowType, String currencyCode, int numOfDays, Context context) {
        NumberFormat numberFormatter = App.provide(context).getNumberFormatter(currencyCode);
        // We can cache the instance
        return new PriceRender(priceShowType, numberFormatter, numOfDays);
    }

    public double price(Accommodation acc, String currency) {
        double fromPrice;
        if (acc.getFirstRate() == null) {
            if (acc.fromPrice == null) {
                return 0;
            }
            fromPrice = acc.fromPrice.get(currency);
        } else {
            fromPrice = acc.getFirstRate().displayPrice.get(currency);
        }

        return adjustPriceToType(fromPrice);
    }

    public double price(Accommodation.Rate accRate, String currencyCode) {
        return adjustPriceToType(accRate.displayPrice.get(currencyCode));
    }

    public double basePrice(Accommodation.Rate rate, String currencyCode) {
        return adjustPriceToType(rate.displayBaseRate.get(currencyCode));
    }

    public String render(double price) {
        return render(price, 1);
    }

    public String render(double price, int numberRooms) {
        return mNumberFormatter.format(price * numberRooms);
    }

    public double discountAmount(Accommodation.Rate rate, String currencyCode) {
        return adjustPriceToType(rate.displayBaseRate.get(currencyCode) - rate.displayPrice.get(currencyCode));
    }

    public int calcDiscountPercent(double baseRate, double price) {
        double diff = baseRate - price;
        if (diff > 0.1) {
            return (int) Math.ceil(100 - (price * 100.0f) / baseRate);
        }
        return 0;
    }


    public int discount(Accommodation.Rate rate, String currencyCode) {
        int disc = calcDiscountPercent(rate.displayBaseRate.get(currencyCode), rate.displayPrice.get(currencyCode));
        return disc;
    }

    public double adjustPriceToType(double price) {
        if (mPriceShowType == UserPreferences.PRICE_SHOW_TYPE_NIGHT) {
            return price / mNumOfDays;
        }
        return price;
    }

}
