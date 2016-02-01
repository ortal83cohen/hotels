package com.etb.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easytobook.api.model.Accommodation;
import com.etb.app.R;
import com.etb.app.activity.BookingSummaryActivity;
import com.etb.app.model.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-28
 */
public class PaymentChoiceFragment extends BaseFragment implements View.OnClickListener {

    public static final HashMap<String, Integer> ccardsList = new HashMap<String, Integer>() {{
        put("VI", R.drawable.credit_card_visa);
        put("MC", R.drawable.credit_card_mastercard);
        put("DN", R.drawable.credit_card_diners_club);
        put("JC", R.drawable.credit_card_jcb);
        put("AX", R.drawable.credit_card_american_express);
        put("DI", R.drawable.credit_card_discover);
    }};
    private static final String EXTRA_ORDER_ITEM = "order";
    final ButterKnife.Setter<View, View.OnClickListener> LISTENER = new ButterKnife.Setter<View, View.OnClickListener>() {
        @Override
        public void set(View view, View.OnClickListener listener, int index) {
            view.setOnClickListener(listener);
        }
    };
    OrderItem mOrderItem;
    List<View> mPaymentButtons = new ArrayList<>();

    public static PaymentChoiceFragment newInstance(OrderItem orderItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_ORDER_ITEM, orderItem);
        PaymentChoiceFragment fragment = new PaymentChoiceFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_ORDER_ITEM, this.getArguments().getParcelable(EXTRA_ORDER_ITEM));
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_booking_payment_choice, container, false);

        mOrderItem = this.getArguments().getParcelable(EXTRA_ORDER_ITEM);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        boolean ccardFlag = false;
        boolean pplFlag = false;
        LinearLayout ccardButton = null;
        for (Accommodation.Rate.PaymentMethods paymentMethod : mOrderItem.paymentMethods) {
            if (paymentMethod.code.equals("PPL")) {
                pplFlag = true;
                ImageButton papalButton = (ImageButton) view.findViewById(R.id.btn_payment_paypal);
                papalButton.setVisibility(View.VISIBLE);
                mPaymentButtons.add(papalButton);
            }
            if (ccardsList.containsKey(paymentMethod.code)) {
                if (!ccardFlag) {
                    ccardFlag = true;
                    ccardButton = (LinearLayout) view.findViewById(R.id.btn_payment_ccard);
                    ccardButton.setVisibility(View.VISIBLE);
                    mPaymentButtons.add(ccardButton);
                }
                FrameLayout frameLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.ccard, ccardButton, false);
                ImageView imageView = (ImageView) frameLayout.findViewById(android.R.id.icon);
                imageView.setImageDrawable(getActivity().getResources().getDrawable(ccardsList.get(paymentMethod.code)));
                ccardButton.addView(frameLayout);
            }
        }
//        mPaymentButtons.add((ImageButton) view.findViewById(R.id.btn_payment_checkout));
        ButterKnife.apply(mPaymentButtons, LISTENER, this);
        if (ccardFlag && !pplFlag) {
            ((BookingSummaryActivity) getActivity()).onCreditCardPayment();
        } else if (pplFlag && !ccardFlag) {
            ((BookingSummaryActivity) getActivity()).onPayPalPayment();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View button) {
        if (button.getId() == R.id.btn_payment_ccard) {
            ((BookingSummaryActivity) getActivity()).onCreditCardPayment();
        } else if (button.getId() == R.id.btn_payment_paypal) {
            ((BookingSummaryActivity) getActivity()).onPayPalPayment();

        }

    }
}
