package com.etb.app.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.etb.app.R;
import com.etb.app.model.CreditCardDetailsPage;
import com.etb.app.utils.AutoAdvanceTextWatcher;
import com.etb.app.widget.CreditCardEditText;

import java.security.InvalidParameterException;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-28
 */
public class CreditCardDetailsFragment extends PageFragment {
    EditText mCardFirstNameView;
    EditText mCardLastNameView;
    CreditCardEditText mCardNumberView;
    EditText mExpMonthView;
    EditText mExpYearView;
    EditText mCardCvcView;


    public static CreditCardDetailsFragment newInstance() {
        return new CreditCardDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_booking_payment_ccard, container, false);
        initViews(view);

        initViewsData();
        return view;
    }

    private void initViews(View view) {
        mCardFirstNameView = ButterKnife.findById(view, R.id.ccard_name_first);
        mCardLastNameView = ButterKnife.findById(view, R.id.ccard_name_last);
        mCardNumberView = ButterKnife.findById(view, R.id.ccard_number);

        mExpMonthView = ButterKnife.findById(view, R.id.ccard_exp_month);
        mExpYearView = ButterKnife.findById(view, R.id.ccard_exp_year);
        mCardCvcView = ButterKnife.findById(view, R.id.ccard_ccv);

        mCardNumberView.addTextChangedListener(new AutoAdvanceTextWatcher(mCardNumberView, CreditCardEditText.CREDITCARD_MAX_LENGTH));
        mExpMonthView.addTextChangedListener(new AutoAdvanceTextWatcher(mExpMonthView, 2));
        mExpYearView.addTextChangedListener(new AutoAdvanceTextWatcher(mExpYearView, 2));

    }

    private void initViewsData() {
        if (mPage != null) {
            Bundle pageData = mPage.getData();

            mCardFirstNameView.setText(pageData.getString(CreditCardDetailsPage.DATA_CARD_NAME_FIRST));
            mCardLastNameView.setText(pageData.getString(CreditCardDetailsPage.DATA_CARD_NAME_LAST));
            mCardNumberView.setText(pageData.getString(CreditCardDetailsPage.DATA_CARD_NUMBER));

            mExpMonthView.setText(pageData.getString(CreditCardDetailsPage.DATA_CARD_EXP_MONTH));
            mExpYearView.setText(pageData.getString(CreditCardDetailsPage.DATA_CARD_EXP_YEAR));
            mCardCvcView.setText(pageData.getString(CreditCardDetailsPage.DATA_CARD_CCV));
        }
    }
    private void validate() {
        Resources r = getActivity().getResources();
        if (mCardFirstNameView.length() == 0) {
            mCardFirstNameView.requestFocus();
            mCardFirstNameView.setError("First name"  +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mCardLastNameView.length() == 0) {
            mCardLastNameView.requestFocus();
            mCardLastNameView.setError("Last name" +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mCardNumberView.length() == 0) {
            mCardNumberView.requestFocus();
            mCardNumberView.setError("Card number" +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mExpMonthView.length() == 0) {
            mExpMonthView.requestFocus();
            mExpMonthView.setError("Month"  +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mExpYearView.length() == 0) {
            mExpYearView.requestFocus();
            mExpYearView.setError("Year"  +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
        if (mCardCvcView.length() == 0) {
            mCardCvcView.requestFocus();
            mCardCvcView.setError("CCV"  +r.getString(R.string.is_required));
            throw new InvalidParameterException(r.getString(R.string.all_fields_must_be_filled));
        }
    }

    @Override
    public String getPageKey() {
        return CreditCardDetailsPage.KEY;
    }

    @Override
    protected void onPageDataSave(Bundle pageData) {
        validate();
        pageData.putString(CreditCardDetailsPage.DATA_CARD_NAME_FIRST, mCardFirstNameView.getText().toString());
        pageData.putString(CreditCardDetailsPage.DATA_CARD_NAME_LAST, mCardLastNameView.getText().toString());
        pageData.putString(CreditCardDetailsPage.DATA_CARD_NUMBER, mCardNumberView.getNumber());
        pageData.putString(CreditCardDetailsPage.DATA_CARD_EXP_MONTH, mExpMonthView.getText().toString());
        pageData.putString(CreditCardDetailsPage.DATA_CARD_EXP_YEAR, mExpYearView.getText().toString());
        pageData.putString(CreditCardDetailsPage.DATA_CARD_CCV, mCardCvcView.getText().toString());

    }
}
