package com.etb.app.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.etb.app.utils.MaskFormatter;
import com.etb.app.utils.TextWatcherAdapter;

/**
 * @author alex
 * @date 2015-06-29
 */
public class CreditCardEditText extends EditText {
    public static final int CREDITCARD_MAX_LENGTH = 19;
    private static final String SEPARATOR = " ";
    private final MaskFormatter mFormatter;
    /**
     * Indicates the change was caused by ourselves.
     */
    private boolean mSelfChange = false;

    public CreditCardEditText(Context context) {
        this(context, null);
    }

    public CreditCardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public CreditCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TextWatcher mTextChangedListener = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mSelfChange) {
                    // Ignore the change caused by s.replace().
                    return;
                }
                if (s.length() < 4) {
                    return;
                }
                String formatted = mFormatter.valueToString(s);
                mSelfChange = true;
                if (!TextUtils.equals(s, formatted)) {
                    InputFilter[] filters = s.getFilters();
                    s.setFilters(new InputFilter[]{});
                    s.replace(0, s.length(), formatted);
                    s.setFilters(filters);
                }
                mSelfChange = false;
            }
        };
        addTextChangedListener(mTextChangedListener);
        setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(CREDITCARD_MAX_LENGTH)});

        // TODO: check what are those: 34NN NNNNNN NNNNN;37NN NNNNNN NNNNN
        mFormatter = new MaskFormatter("#### #### #### ####");
    }

    public String getNumber() {
        return getText().toString().replace(SEPARATOR, "");
    }
}
