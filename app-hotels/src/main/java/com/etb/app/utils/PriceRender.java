package com.etb.app.utils;

import android.content.Context;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.preferences.UserPreferences;

import java.text.NumberFormat;
import java.util.HashMap;

/**
 * @author alex
 * @date 2015-05-06
 */
public class PriceRender {
    private final int mNumOfDays;
    private final NumberFormat mNumberFormatter;
    private final int mPriceShowType;

    public static PriceRender create(int priceShowType, String currencyCode, int numOfDays, Context context) {
        NumberFormat numberFormatter = App.provide(context).getNumberFormatter(currencyCode);
        // We can cache the instance
        return new PriceRender(priceShowType, numberFormatter, numOfDays);
    }

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

    public double price(Accommodation acc, String currency) {
        double fromPrice;
        if (acc.getFirstRate() == null) {
            if (acc.fromPrice == null) {
                return 0;
            }
            fromPrice = acc.fromPrice.get(currency);
        } else {
            if (acc.getFirstRate().displayPrice.containsKey(currency)) { //todo remove this if
                fromPrice = acc.getFirstRate().displayPrice.get(currency);
            } else if (acc.getFirstRate().displayPrice.containsValue("EUR")) {
                fromPrice = acc.getFirstRate().displayPrice.get("EUR");
            } else {
                fromPrice = 123.4;
            }
        }

        return adjustPriceToType(fromPrice);
    }

    public double price(Accommodation.Rate accRate, String currencyCode) {
        if (accRate.displayPrice.containsKey(currencyCode)) {//todo remove this if
            return adjustPriceToType(accRate.displayPrice.get(currencyCode));
        } else if (accRate.displayPrice.containsValue("EUR")) {
            return adjustPriceToType(accRate.displayPrice.get("EUR"));
        } else return 123.4;
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

    public String render(HashMap<String, Double> priceMap, String currencyCode) {
        return render(priceMap, 1, currencyCode);
    }

    public String render(HashMap<String, Double> priceMap, int numberRooms, String currencyCode) {
        double price;
        if (priceMap.containsKey(currencyCode)) {//todo remove this if
            price = priceMap.get(currencyCode);
        } else {
            price = 123.4;
        }

        return render(price, numberRooms);
    }

    public double discountAmount(Accommodation.Rate rate, String currencyCode) {
        return adjustPriceToType(rate.displayBaseRate.get(currencyCode) - rate.displayPrice.get(currencyCode));
    }

    public int calcDiscountPercent(HashMap<String, Double> baseRateMap, HashMap<String, Double> priceMap, String currencyCode) {
        double baseRate;
        double price;
        if (baseRateMap.containsKey(currencyCode)) {//todo remove this if
            baseRate = baseRateMap.get(currencyCode);
            price = priceMap.get(currencyCode);
        } else {
            baseRate = 123.4;
            price = 123.4;
        }

        double diff = baseRate - price;
        if (diff > 0.1) {
            return (int) Math.ceil(100 - (price * 100.0f) / baseRate);
        }
        return 0;
    }


    public int discount(Accommodation.Rate rate, String currencyCode) {
        int disc;
        if (rate.displayPrice.containsKey(currencyCode)) {//todo remove this if
            disc = calcDiscountPercent(rate.displayBaseRate, rate.displayPrice, currencyCode);
        } else {
            disc = 0;
        }
        return disc;
    }

    public double adjustPriceToType(double price) {
        if (mPriceShowType == UserPreferences.PRICE_SHOW_TYPE_NIGHT) {
            return price / mNumOfDays;
        }
        return price;
    }

    public Double getByCurrency(HashMap<String, Double> displayBaseRate, String currency) {
        if (displayBaseRate.containsKey(currency)) {//todo remove this if
            return displayBaseRate.get(currency);
        }
        return 123.4;
    }
}
