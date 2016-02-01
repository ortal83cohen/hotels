package com.easytobook.api.utils;

import android.support.v4.util.SimpleArrayMap;

import com.easytobook.api.contract.CreditCard;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author alex
 * @date 2015-06-29
 */
public class CreditCardUtils {
    /*
     * Maximum years credit card expiry
     */
    private static final int MAX_YEARS_EXPIRY = 20;
    private static final SimpleArrayMap<String, Rule> sRules = new SimpleArrayMap<>(16);

    static {
        sRules.put(CreditCard.VISA, new Rule(CreditCard.VISA, "^4(\\d{12}|\\d{15})$", true));
        sRules.put(CreditCard.MASTERCARD, new Rule(CreditCard.MASTERCARD, "^(51|52|53|54|55)\\d{14}$", true));
        sRules.put(CreditCard.AMEX, new Rule(CreditCard.AMEX, "^(34|37)\\d{13}$", true));
        sRules.put(CreditCard.DINERS_CLUB, new Rule(CreditCard.DINERS_CLUB, "^((36|38|60)\\d|304|305)\\d{11}$", true));
        sRules.put(CreditCard.DISCOVER, new Rule(CreditCard.DISCOVER, "^(60110|60112|60113|60114|60119)\\d{11}$", true));
        sRules.put(CreditCard.JCB, new Rule(CreditCard.JCB, "^(3\\d{3}|1800|2131)\\d{11,12}$", true));
        sRules.put(CreditCard.LASER_DEBIT, new Rule(CreditCard.LASER_DEBIT, "^(6304|6706|6771|6709)\\d{12,15}$", false));
        sRules.put(CreditCard.VISA_DEBIT, new Rule(CreditCard.VISA_DEBIT, "^4(\\d{12}|\\d{15})$", true));
        sRules.put(CreditCard.CARTE_BLEUE, new Rule(CreditCard.CARTE_BLEUE, "^4(\\d{12}|\\d{15})$", true));
        /**
         * 1) 622126...
         *    622
         *       1
         *        2[6-9]	622126-622129
         *        [3-9]		622130-622199
         *       [2-8]      622200-622899
         *       9
         *        [0-1]		622900-622919
         *        2[0-5]    622920-622925
         * ...622925,
         * 2) 624...
         *    62[4-6]		624000-626999
         * ...626,
         * 3) 6282...
         *    628[2-8]		628200-628899
         * ...6288
         */
        sRules.put(CreditCard.CHINA_UNIONPAY, new Rule(CreditCard.CHINA_UNIONPAY, "^(62212[6-9]|6221[3-9]\\d|622[2-8]\\d{2}|6229[0-1]\\d|62292[0-5]|62[4-6]\\d{3}|628[2-8]\\d{2})\\d{10,13}$", false));
        sRules.put(CreditCard.SWITCH, new Rule(CreditCard.SWITCH, "^((63\\d{2}|4903|4911|4936|6759)\\d{2}|564182)(\\d{10}|\\d{12,13})$", true));
        sRules.put(CreditCard.MAESTRO, new Rule(CreditCard.MAESTRO, "^(5020|5038|6304|6759)(\\d{12}|\\d{14,15})$", true));
        sRules.put(CreditCard.DELTA, new Rule(CreditCard.DELTA, "^((45|46|48|49)\\d{2}|4137|4462)\\d{12}$", true));
        sRules.put(CreditCard.DANKORT, new Rule(CreditCard.DANKORT, "^(4571)\\d{12}$", true));
        sRules.put(CreditCard.CARTE_SI, new Rule(CreditCard.CARTE_SI, "^(4)\\d{15}$", true));
        sRules.put(CreditCard.CARTE_BLANCHE, new Rule(CreditCard.CARTE_BLANCHE, "^((94|95)\\d|389)\\d{11}$", true));

    }

    /**
     * Recognize credit card type by its number
     */
    public static ArrayList<String> recognize(String number) {
        ArrayList<String> types = CreditCard.all();
        ArrayList<String> recognized = new ArrayList<>();

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            if (validate(number, type)) {
                recognized.add(type);
            }
        }
        return recognized;
    }

//
//            self::KEY_NAME => 'Visa Electron',
//            self::KEY_REGEX => '^((4844|4917)\d{2}|450875|491880)\d{10}$',
//            self::KEY_CRC => true,

    /**
     * Validate credit card type by its number and type
     */
    public static boolean validate(String number, String type) {
        Rule rule = sRules.get(type);

        if (rule == null || number == null) {
            return false;
        }

        if (!number.matches(rule.regex)) {
            return false;
        }
//        if (rule.crcCheck) {
//            return checkCrc(number);
//        }

        return true;
    }

    /**
     * Check crc
     *
     * @return bool
     */
    public static boolean checkCrc(String number) {
        int checksum = 0;
        int j = 1;
        for (int i = number.length() - 1; i >= 0; i--) {
            int calc = Integer.parseInt(String.valueOf(number.charAt(i))) * j;
            if (calc > 9) {
                checksum = checksum + 1;
                calc = calc - 10;
            }
            checksum = checksum + calc;
            if (j == 1) {
                j = 2;
            } else {
                j = 1;
            }
        }
        return checksum % 10 == 0;
    }

    /**
     * get maximum years for credit card expiry
     */
    public static int getMaxExpiryYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + MAX_YEARS_EXPIRY;
    }

    public static class Rule {
        String type;
        String regex;
        boolean crcCheck;

        public Rule(String type, String regex, boolean crcCheck) {
            this.type = type;
            this.regex = regex;
            this.crcCheck = crcCheck;
        }
    }

}
