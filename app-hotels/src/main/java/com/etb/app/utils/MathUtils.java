package com.etb.app.utils;

/**
 * @author alex
 * @date 2015-04-22
 */
public class MathUtils {

    public static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
