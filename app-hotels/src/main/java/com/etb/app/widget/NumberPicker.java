package com.etb.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.etb.app.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-05-25
 */
public class NumberPicker extends LinearLayout implements View.OnClickListener, View.OnTouchListener {
    @Bind({R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, R.id.button_6})
    List<Button> mButtons;

    private int mSelectedValue = -1;
    private OnSelectListener mListener;
    private OnTouchListener mOnTouchListener;
    private ButterKnife.Action<? super Button> SELECT = new ButterKnife.Action<Button>() {
        @Override
        public void apply(Button view, int index) {
            if (mSelectedValue != -1 && index == mSelectedValue - 1) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
    };

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        View view = LayoutInflater.from(context).inflate(R.layout.view_number_picker, this);
        ButterKnife.bind(this, view);

        ButterKnife.apply(mButtons, new ButterKnife.Action<Button>() {
            @Override
            public void apply(Button view, int index) {
                view.setOnClickListener(NumberPicker.this);
                view.setOnTouchListener(NumberPicker.this);
            }
        });
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
        mOnTouchListener = l;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mOnTouchListener != null && mOnTouchListener.onTouch(v, event);
    }

    public void setListener(OnSelectListener mListener) {
        this.mListener = mListener;
    }

    public void setSelected(int number) {
        mSelectedValue = number;
        ButterKnife.apply(mButtons, SELECT);
    }

    @Override
    public void onClick(View v) {
        int number = Integer.valueOf((String) v.getTag());
        setSelected(number);
        if (mListener != null) {
            mListener.onNumberSelect(this, (Button) v, number);
        }
    }

    public int getValue() {
        return mSelectedValue;
    }


    public interface OnSelectListener {
        void onNumberSelect(NumberPicker view, Button button, int number);
    }


}
