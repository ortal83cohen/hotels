package com.etb.app.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.activity.BaseActivity;
import com.etb.app.model.OrderItem;
import com.etb.app.utils.PriceRender;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author alex
 * @date 2015-04-28
 */
public class PriceBreakdownFragment extends BaseFragment implements View.OnClickListener {

    private static final String ROOMS = "rooms";
    private static final String ROOMS_AMOUNT = "rooms_amount";
    private static final String DISCOUNT = "discount";
    private static final String DISCOUNT_AMOUNT = "discount_amount";
    private static final String TAX_AMOUNT = "tax_amount";
    private static final String TOTAL_AMOUNT = "total_amount";
    @Bind(R.id.background)
    FrameLayout mBackground;
    @Bind(R.id.close_button)
    Button mCloseButton;
    @Bind(R.id.room_text)
    TextView mRoomText;
    @Bind(R.id.room_value)
    TextView mRoomValue;
    @Bind(R.id.discount_text)
    TextView mDiscountText;
    @Bind(R.id.discount_value)
    TextView mDiscountValue;
    @Bind(R.id.tax_value)
    TextView mTaxValue;
    @Bind(R.id.total_price)
    TextView mTotalPrice;

    private int mRooms;
    private Double mRoomsAmount;
    private int mDiscount;
    private Double mDiscountAmount;
    private Double mTaxAmount;
    private Double mTotalAmount;

    public static PriceBreakdownFragment newInstance(int numbersOfRooms, OrderItem mOrderItem, PriceRender priceRender) {
        Double roomsAmount =priceRender.getByCurrency( mOrderItem.displayBaseRate,mOrderItem.currency);
        Double discountAmount = priceRender.getByCurrency(mOrderItem.displayBaseRate,mOrderItem.currency) - priceRender.getByCurrency(mOrderItem.displayPrice,mOrderItem.currency);
        int discount = priceRender.calcDiscountPercent(mOrderItem.displayBaseRate, mOrderItem.displayPrice,mOrderItem.currency);
        Double totalAmount = priceRender.getByCurrency(mOrderItem.displayPrice,mOrderItem.currency);
        Double taxAmount = mOrderItem.includedTax;

        return newInstance(numbersOfRooms, roomsAmount, discount, discountAmount, taxAmount, totalAmount);
    }

    public static PriceBreakdownFragment newInstance(int numbersOfRooms, Accommodation.Rate rate, String currencyCode, PriceRender priceRender) {
        Double taxAmount = 0.0;
        for (Accommodation.Rate.TaxesAndFees taxesAndFees : rate.taxesAndFees) {
            if (taxesAndFees.totalValue.get(currencyCode) != null && taxesAndFees.displayIncluded) {
                taxAmount += taxesAndFees.totalValue.get(currencyCode);
            }
        }
        Double roomsAmount = priceRender.basePrice(rate, currencyCode);
        int discount = priceRender.discount(rate, currencyCode);
        Double discountAmount = priceRender.discountAmount(rate, currencyCode);
        Double totalAmount = priceRender.price(rate, currencyCode);

        return newInstance(numbersOfRooms, roomsAmount, discount, discountAmount, taxAmount, totalAmount);
    }

    public static PriceBreakdownFragment newInstance(int numbersOfRooms, Double roomsAmount, int discount, Double discountAmount, Double taxAmount, Double totalAmount) {
        Bundle bundle = new Bundle();
        bundle.putInt(ROOMS, numbersOfRooms);
        bundle.putDouble(ROOMS_AMOUNT, roomsAmount);
        bundle.putInt(DISCOUNT, discount);
        bundle.putDouble(DISCOUNT_AMOUNT, discountAmount);
        bundle.putDouble(TAX_AMOUNT, taxAmount);
        bundle.putDouble(TOTAL_AMOUNT, totalAmount);
        PriceBreakdownFragment fragment = new PriceBreakdownFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_price_breakdown, container, false);
        ButterKnife.bind(this, view);

        mRooms = this.getArguments().getInt(ROOMS);
        mRoomsAmount = this.getArguments().getDouble(ROOMS_AMOUNT);
        mDiscount = this.getArguments().getInt(DISCOUNT);
        mDiscountAmount = this.getArguments().getDouble(DISCOUNT_AMOUNT);
        mTaxAmount = this.getArguments().getDouble(TAX_AMOUNT);
        mTotalAmount = this.getArguments().getDouble(TOTAL_AMOUNT);
        initViews();

        return view;
    }

    private void initViews() {
        Resources r = getActivity().getResources();
        PriceRender priceRender = getPriceRender();

        mBackground.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);
        mRoomText.setText(String.format("%d %s", mRooms, r.getQuantityString(R.plurals.rooms, mRooms)));
        mRoomValue.setText(priceRender.render(mRoomsAmount, mRooms));
        if (mDiscount > 0) {
            mDiscountText.setText(r.getString(R.string.discount, mDiscount));
            mDiscountValue.setText(priceRender.render(-1 * mDiscountAmount, mRooms));
        } else {
            mDiscountText.setVisibility(View.GONE);
            mDiscountValue.setVisibility(View.GONE);
        }
        mTaxValue.setText(priceRender.render(mTaxAmount, mRooms));
        mTotalPrice.setText(priceRender.render(mTotalAmount, mRooms));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onClick(View v) {
        ((BaseActivity) getActivity()).remove(this);
    }


    public interface Listener {
        void onPriceBreakdownClick(Accommodation.Rate rate);
    }
}
