package com.etb.app.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.etb.app.R;

/**
 * @author alex
 * @date 2015-05-20
 */
public class SmallDatePicker extends BaseDatePicker {
    public SmallDatePicker(Context context) {
        this(context, null);
    }

    public SmallDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, R.layout.view_small_datepicker);
    }

}
