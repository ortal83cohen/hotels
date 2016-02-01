package com.easytobook.api.contract;

/**
 * @author alex
 * @date 2015-09-22
 */
public class Policy {
    public static final int UNKNOWN = -1; // Unknown for vendors
    public static final int NONE = 0;  // Non Refundable
    public static final int HOURS = 1;  // Until hour on arrival date
    public static final int DAYS = 2;  // Days before arrival
    public static final int BOOK_DATE = 3;
}
