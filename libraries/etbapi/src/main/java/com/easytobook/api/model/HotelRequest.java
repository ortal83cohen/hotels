package com.easytobook.api.model;

import com.easytobook.api.contract.Language;

/**
 * @author alex
 * @date 2015-05-26
 */
public class HotelRequest {

    protected DateRange mDateRange;
    protected int mNumbersOfRooms;
    protected int mNumberOfPersons;

    protected String mCurrency = "EUR";
    protected String mLanguage = Language.DEFAULT;
    private String mCustomerCountryCode;


    public HotelRequest() {
        mNumbersOfRooms = 1;
        mNumberOfPersons = 2;
        mDateRange = DateRange.getInstance();
    }

    public String getCustomerCountryCode() {
        return mCustomerCountryCode;
    }

    public void setCustomerCountryCode(String customerCountryCode) {
        mCustomerCountryCode = customerCountryCode;
    }

    public DateRange getDateRange() {
        return mDateRange;
    }

    public void setDateRange(DateRange mDateRange) {
        this.mDateRange = mDateRange;
    }

    public void setNoDatesRequest() {
        mDateRange = null;
    }

    public boolean isDatesRequest() {
        return mDateRange != null;
    }

    public int getNumberOfRooms() {
        return mNumbersOfRooms;
    }

    public void setNumbersOfRooms(int numbersOfRooms) {
        this.mNumbersOfRooms = numbersOfRooms;
    }


    public int getNumberOfPersons() {
        return mNumberOfPersons;
    }

    public void setNumberOfPersons(int numberOfPersons) {
        mNumberOfPersons = numberOfPersons;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }


}
