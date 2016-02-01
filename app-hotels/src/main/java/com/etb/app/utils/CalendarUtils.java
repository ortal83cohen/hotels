package com.etb.app.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;

/**
 * @author alex
 * @date 2015-04-26
 */
public class CalendarUtils {

    public static final int YEAR_INDEX = 2;
    private static final int MONTH_INDEX = 0;
    private static final int DAY_INDEX = 1;

    public static void copyTime(Calendar dest, Calendar src) {
        dest.set(Calendar.HOUR_OF_DAY, src.get(Calendar.HOUR_OF_DAY));
        dest.set(Calendar.MINUTE, src.get(Calendar.MINUTE));
        dest.set(Calendar.SECOND, src.get(Calendar.SECOND));
        dest.set(Calendar.MILLISECOND, src.get(Calendar.MILLISECOND));
    }

    public static int compareDayInMonth(Calendar day, int year, int month) {
        int dayMonth = day.get(Calendar.MONTH);
        int dayYear = day.get(Calendar.YEAR);

        if (dayYear == year) {
            if (dayMonth == month) {
                return 0;
            } else if (dayMonth < month) {
                return -1;
            }
            // dayMonth > month
            return 1;
        } else if (dayYear < year) {
            return -1;
        }
        // dayYear > year
        return 1;
    }

    /**
     * Compute the array representing the order of Month / Day / Year views in their layout.
     * Will be used for I18N purpose as the order of them depends on the Locale.
     */
    public static int[] getMonthDayYearIndexes(String pattern) {
        int[] result = new int[3];

        final String filteredPattern = pattern.replaceAll("'.*?'", "");

        final int dayIndex = filteredPattern.indexOf('d');
        final int monthMIndex = filteredPattern.indexOf("M");
        final int monthIndex = (monthMIndex != -1) ? monthMIndex : filteredPattern.indexOf("L");
        final int yearIndex = filteredPattern.indexOf("y");

        if (yearIndex < monthIndex) {
            result[YEAR_INDEX] = 0;

            if (monthIndex < dayIndex) {
                result[MONTH_INDEX] = 1;
                result[DAY_INDEX] = 2;
            } else {
                result[MONTH_INDEX] = 2;
                result[DAY_INDEX] = 1;
            }
        } else {
            result[YEAR_INDEX] = 2;

            if (monthIndex < dayIndex) {
                result[MONTH_INDEX] = 0;
                result[DAY_INDEX] = 1;
            } else {
                result[MONTH_INDEX] = 1;
                result[DAY_INDEX] = 0;
            }
        }
        return result;
    }

    public static void addToCalender(Context context, Date arrivalDate, Date departureDate, String title, String description, String location, String email, String phone) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, arrivalDate.getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, departureDate.getTime())
                .putExtra(CalendarContract.Events.ALL_DAY, true)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//                        .putExtra(CalendarContract.Events.EVENT_TIMEZONE, "India")
                .putExtra(Intent.EXTRA_EMAIL, email)
                .putExtra(Intent.EXTRA_PHONE_NUMBER, phone);
        context.startActivity(intent);
    }
}
