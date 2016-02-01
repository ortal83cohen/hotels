package com.easytobook.api.contract;

import java.util.ArrayList;

/**
 * @author alex
 * @date 2015-04-28
 */
public class CreditCard {
    public static final String VISA = "VI";
    public static final String MASTERCARD = "MC";
    public static final String AMEX = "AX";
    public static final String DINERS_CLUB = "DN";
    public static final String DISCOVER = "DI";
    public static final String JCB = "JC";
    public static final String LASER_DEBIT = "LD";
    public static final String VISA_DEBIT = "VIED";
    public static final String CARTE_BLEUE = "CB";
    public static final String CHINA_UNIONPAY = "CUP";
    public static final String SWITCH = "S";
    public static final String MAESTRO = "TO";
    public static final String DELTA = "L";
    public static final String DANKORT = "N";
    public static final String CARTE_SI = "T";
    public static final String CARTE_BLANCHE = "CBL";

    private static final int CAPACITY = 16;

    public static ArrayList<String> all() {
        ArrayList<String> all = new ArrayList<>(CAPACITY);
        all.add(VISA);
        all.add(MASTERCARD);
        all.add(AMEX);
        all.add(DINERS_CLUB);
        all.add(DISCOVER);
        all.add(JCB);
        all.add(LASER_DEBIT);
        all.add(VISA_DEBIT);
        all.add(CARTE_BLEUE);
        all.add(CHINA_UNIONPAY);
        all.add(SWITCH);
        all.add(MAESTRO);
        all.add(DELTA);
        all.add(DANKORT);
        all.add(CARTE_SI);
        all.add(CARTE_BLANCHE);
        return all;
    }
}
