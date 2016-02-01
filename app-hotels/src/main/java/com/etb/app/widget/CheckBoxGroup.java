package com.etb.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.etb.app.R;

/**
 * @author alex
 * @date 2015-06-18
 */
public class CheckBoxGroup extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    public static final int CHECKBOX_COUNT = 5;
    int mPadding;
    private OnCheckedChangeListener mListener;
    private int mMinCheckedIdx = -1;
    private int mMaxCheckedIdx = -1;

    public CheckBoxGroup(Context context) {
        this(context, null);
    }

    public CheckBoxGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);

        mPadding = context.getResources().getDimensionPixelOffset(R.dimen.one_dp) * 2;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxGroup);
        int checkboxLayout = a.getResourceId(R.styleable.CheckBoxGroup_groupCheckboxLayout, 0);
        a.recycle();

        LayoutInflater li = LayoutInflater.from(getContext());
        for (int i = 0; i < CHECKBOX_COUNT; i++) {
            addCheckbox(i, checkboxLayout, li);
        }

    }

    private void addCheckbox(int i, int checkboxLayout, LayoutInflater li) {
        CheckBox view = (CheckBox) li.inflate(checkboxLayout, this, false);
        LayoutParams params = new TableRow.LayoutParams(0, view.getLayoutParams().height, 1f);
        if (i > 0) {
            params.setMargins(mPadding, 0, 0, 0);
        }
        view.setLayoutParams(params);

        view.setCompoundDrawablePadding(0);
        view.setTag(i);
        view.setText(String.valueOf(i + 1));

        view.setOnCheckedChangeListener(this);
        addView(view);
    }

    public void setChecked(SparseBooleanArray states) {
        for (int i = 0; i < CHECKBOX_COUNT; i++) {
            CheckBox view = (CheckBox) getChildAt(i);

            boolean checked = (states != null) && states.get(i + 1);
            if (checked) {
                if (mMinCheckedIdx == -1) {
                    mMinCheckedIdx = i;
                } else {
                    mMaxCheckedIdx = i;
                }
            }
            setCheckedSilently(view, checked);
        }
    }

    private void setCheckedSilently(CheckBox view, boolean checked) {
        view.setOnCheckedChangeListener(null);
        view.setChecked(checked);
        view.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int idx = (int) buttonView.getTag();
        if (isChecked) {
            if (mMinCheckedIdx == -1 || idx < mMinCheckedIdx) {
                //
                if (mMaxCheckedIdx == -1) {
                    mMaxCheckedIdx = mMinCheckedIdx;
                }
                mMinCheckedIdx = idx;
            } else if (idx > mMaxCheckedIdx) {
                mMaxCheckedIdx = idx;
            }
        } else {
            // Recalculate min max
            mMinCheckedIdx = -1;
            mMaxCheckedIdx = -1;
        }

        SparseBooleanArray states = new SparseBooleanArray(CHECKBOX_COUNT);
        for (int i = 0; i < CHECKBOX_COUNT; i++) {
            CheckBox view = (CheckBox) getChildAt(i);

            boolean checked = view.isChecked();
            // Select in range
            if (isChecked) {
                if (!checked && i > mMinCheckedIdx && i < mMaxCheckedIdx) {
                    checked = true;
                    setCheckedSilently(view, true);
                }
            } else {
                if (checked) {
                    if (mMinCheckedIdx == -1) {
                        mMinCheckedIdx = i;
                    } else {
                        mMaxCheckedIdx = i;
                    }
                }
            }
            if (checked) {
                int value = i + 1;
                states.put(value, true);
            }
        }

        if (mListener != null) {
            mListener.onCheckedChanged(this, states);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckBoxGroup group, SparseBooleanArray checked);
    }
}
