package com.etb.app.widget.datepicker;

import com.easytobook.api.model.DateRange;

import java.util.Calendar;

/**
 * @author alex
 * @date 2015-11-17
 */
public interface DayPickerView extends SimpleMonthView.OnDayClickListener {


    void showNextMonth();

    void showPrevMonth();

    void setFirstDayOfWeek(int firstDayOfWeek);

    void setMinRange(long min);

    void setMinMaxRange(long minMillis, long maxMillis);

    void setSelectedRange(DateRange range);

    void goTo(long dayMillis);

    void setOnChangeListener(OnChangeListener listener);

    @Override
    void onDayClick(SimpleMonthView view, Calendar day);

    interface OnChangeListener {
        void onDaySelected(DayPickerView view, Calendar day);

        void onMonthChanged(DayPickerView view, Calendar date);
    }
}
