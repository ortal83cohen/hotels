package com.easytobook.api.model;

import com.easytobook.api.utils.DateRangeUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author alex
 * @date 2015-04-26
 */
public class DateRange implements Serializable {
    public static final int RANGE_SET_FROM = 1;
    public static final int RANGE_SET_TO = 2;
    private static final int RANGE_SET_NONE = 0;
    public Calendar from;
    public Calendar to;
    private int mSelectState = RANGE_SET_NONE;

    public DateRange() {
    }

    public DateRange(long fromMillis, long toMillis) {
        from = Calendar.getInstance(Locale.getDefault());
        to = Calendar.getInstance(Locale.getDefault());
        from.setTimeInMillis(fromMillis);
        to.setTimeInMillis(toMillis);
    }

    public DateRange(DateRange dateRange) {
        this(dateRange.from.getTimeInMillis(), dateRange.to.getTimeInMillis());
    }

    public static DateRange getInstance() {
        DateRange range = new DateRange();
        range.from = Calendar.getInstance(Locale.getDefault());
        range.to = Calendar.getInstance(Locale.getDefault());
        range.to.add(Calendar.DAY_OF_MONTH, 1);
        return range;
    }

    public void set(DateRange range) {
        from.setTimeInMillis(range.from.getTimeInMillis());
        to.setTimeInMillis(range.to.getTimeInMillis());
    }

    public void setDay(Calendar day) {
        if (mSelectState == RANGE_SET_NONE || mSelectState == RANGE_SET_FROM) {
            setFrom(day);
            mSelectState = RANGE_SET_TO;
        } else {
            setTo(day);
            //mSelectState = RANGE_SET_FROM;
        }

    }

    private void setFrom(Calendar day) {
        from.setTimeInMillis(day.getTimeInMillis());
        if (day.after(to)) {
            to.setTimeInMillis(day.getTimeInMillis());
            to.add(Calendar.DAY_OF_MONTH, 1);
            mSelectState = RANGE_SET_FROM;
        }
    }

    private void setTo(Calendar day) {
        if (day.before(from)) {
            from.setTimeInMillis(day.getTimeInMillis());
            to.setTimeInMillis(day.getTimeInMillis());
            to.add(Calendar.DAY_OF_MONTH, 1);
            mSelectState = RANGE_SET_TO;
        } else {
            to.setTimeInMillis(day.getTimeInMillis());
        }
    }


    public int days() {
        return DateRangeUtils.days(from.getTimeInMillis(), to.getTimeInMillis());
    }

    public int getState() {
        return mSelectState;
    }

    public void setState(int state) {
        mSelectState = state;
    }
}
