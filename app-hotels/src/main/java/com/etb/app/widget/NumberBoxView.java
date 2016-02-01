package com.etb.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etb.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-08-20
 */
public class NumberBoxView extends LinearLayout {
    @Bind(android.R.id.icon)
    ImageView mIcon;
    @Bind(R.id.number)
    TextView mValue;
    @Bind(android.R.id.text1)
    TextView mTitle;
    @Bind(android.R.id.text2)
    TextView mSubtitle;

    public NumberBoxView(Context context) {
        this(context, null);
    }

    public NumberBoxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.view_number_box, this);
        ButterKnife.bind(this, view);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberBoxView, defStyleAttr, 0);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.NumberBoxView_numberBoxIcon) {
                mIcon.setImageDrawable(ta.getDrawable(attr));
            } else if (attr == R.styleable.NumberBoxView_numberBoxTitle) {
                mTitle.setText(ta.getString(attr));
            } else if (attr == R.styleable.NumberBoxView_numberBoxSubtitle) {
                String subtitle = ta.getString(attr);
                if (subtitle != null) {
                    mSubtitle.setText(subtitle);
                } else {
                    mSubtitle.setVisibility(View.INVISIBLE);
                }
            } else if (attr == R.styleable.NumberBoxView_numberBoxValue) {
                mValue.setText(ta.getString(attr));
            }
        }

        ta.recycle();
    }

    public void setValue(String value) {
        mValue.setText(value);
    }

    public int getValue() {
        return Integer.valueOf(mValue.getText().toString());
    }

    public void setValue(int number) {
        mValue.setText(String.valueOf(number));
    }

    public void setSubtitle(String subtitle) {
        mSubtitle.setText(subtitle);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
