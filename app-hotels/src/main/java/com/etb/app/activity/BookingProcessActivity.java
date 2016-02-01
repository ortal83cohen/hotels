package com.etb.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.easytobook.api.EtbApi;
import com.easytobook.api.model.ErrorResponse;
import com.easytobook.api.model.Meta;
import com.easytobook.api.model.OrderRequest;
import com.easytobook.api.model.OrderResponse;
import com.easytobook.api.utils.CreditCardUtils;
import com.easytobook.api.utils.NetworkUtils;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.etbapi.RetrofitCallback;
import com.etb.app.etbapi.RetrofitConverter;
import com.etb.app.fragment.PageFragmentInterface;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.AbstractPage;
import com.etb.app.model.BookingStepsModel;
import com.etb.app.model.CreditCardDetailsPage;
import com.etb.app.model.HotelListRequest;
import com.etb.app.model.OrderItem;
import com.etb.app.model.PersonalInfoPage;
import com.etb.app.payment.PaymentRequestWrapper;
import com.etb.app.utils.AppLog;
import com.etb.app.widget.BookingStepsView;
import com.squareup.okhttp.ResponseBody;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;

import java.io.IOException;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import retrofit.Response;

/**
 * @author alex
 * @date 2015-04-28
 */
public class BookingProcessActivity extends BaseActivity implements PageFragmentInterface, ModelCallbacks {
    public static final int EAN_VENDOR_ID = 69;
    public static final int HTTP_CODE_OK = 200;


    private static final String EXTRA_PROCESS_TYPE = "type";
    private static final int REQUEST_CODE_PAYPAL = 100;

    ViewPager mPager;
    Button mNextButton;
    BookingStepsView mStepPagerStrip;

    private StepsPagerAdapter mPagerAdapter;

    private BookingStepsModel mBookingStepsModel;

    private boolean mConsumePageSelectedEvent;
    private List<Page> mCurrentPageSequence;

    private OrderItem mOrderItem;
    private HotelSnippet mHotelSnippet;
    private HotelListRequest mRequest;

    private HashMap<String, FormData> mPageForms = new HashMap<>();
    private int mProcessType;

    private View mPaypalLogo;
    private ImageView mLoaderImage;
    private FrameLayout mLoaderFrame;

    private RetrofitCallback<OrderResponse> mResultsCallback = new RetrofitCallback<OrderResponse>() {
        @Override
        protected void notifyFailure(Throwable exception) {
            AppLog.e(exception);
        }

        @Override
        protected void failure(ResponseBody errorBody, boolean isOffline) {
            if (errorBody != null) {
                try {
                    ErrorResponse body = (ErrorResponse) RetrofitConverter.getBodyAs(errorBody, ErrorResponse.class);
                    if (body != null && body.meta != null) {
                        fail(body.meta.errorMessage);
                    } else {
                        fail("Unexpected Error");
                    }
                } catch (IOException e) {
                    AppLog.e(e);
                    fail("Unexpected Error");
                }
            } else {
                fail("Unexpected Error");
            }
        }

        @Override
        protected void success(OrderResponse orderResponse, Response<OrderResponse> response) {
            mNextButton.setEnabled(true);
            mLoaderFrame.setVisibility(View.GONE);
            if (orderResponse.meta.statusCode == 200) {//HTTP OK
                startActivity(ConfirmationActivity.createIntent(orderResponse.order.orderId, true, BookingProcessActivity.this));
                BookingProcessActivity.this.finish();
            } else {
                showErrorMessage(orderResponse.meta.errorMessage);
                AppLog.e(new BookingException(metaToMessage(orderResponse.meta)));

            }
        }

        private void fail(String toastMessage) {
            mNextButton.setEnabled(true);
            mLoaderFrame.setVisibility(View.GONE);
            showErrorMessage(toastMessage);
        }
    };

    public static Intent createIntent(OrderItem orderItem, int processType, HotelListRequest request, HotelSnippet hotelSnippet, Context context) {
        Intent bookIntent = new Intent(context, BookingProcessActivity.class);
        bookIntent.putExtra(EXTRA_SNIPPET, hotelSnippet);
        bookIntent.putExtra(EXTRA_ORDER_ITEM, orderItem);
        bookIntent.putExtra(EXTRA_REQUEST, request);
        bookIntent.putExtra(EXTRA_PROCESS_TYPE, processType);
        return bookIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectViews();

        mHotelSnippet = getIntent().getParcelableExtra(EXTRA_SNIPPET);
        mOrderItem = getIntent().getParcelableExtra(EXTRA_ORDER_ITEM);
        mProcessType = getIntent().getIntExtra(EXTRA_PROCESS_TYPE, -1);
        mRequest = getIntent().getParcelableExtra(EXTRA_REQUEST);

        if (savedInstanceState != null) {
            mBookingStepsModel = BookingStepsModel.create(mProcessType, this);
            mBookingStepsModel.load(savedInstanceState.getBundle("model"));
        } else {
            mBookingStepsModel = BookingStepsModel.create(mProcessType, this);
        }

        if (mProcessType == BookingStepsModel.PROCESS_PAYPAL) {
            mPaypalLogo.setVisibility(View.VISIBLE);
        } else {
            mPaypalLogo.setVisibility(View.GONE);
        }

        mBookingStepsModel.registerListener(this);
        if (mLoaderImage != null) {
            mLoaderImage.setBackgroundResource(R.drawable.logo_animation);
            AnimationDrawable logoAnimation;
            logoAnimation = (AnimationDrawable) mLoaderImage.getBackground();
            logoAnimation.start();
            mLoaderFrame.setVisibility(View.GONE);
        }
        setupViewPager();

        onPageTreeChanged();
    }

    @Override
    protected void onCreateContentView() {
        setContentView(R.layout.activity_booking);
    }

    private void injectViews() {
        mPager = ButterKnife.findById(this, R.id.pager);
        mNextButton = ButterKnife.findById(this, R.id.next_button);
        mStepPagerStrip = ButterKnife.findById(this, R.id.strip);
        mPaypalLogo = ButterKnife.findById(this, R.id.paypal_logo);
        mLoaderImage = ButterKnife.findById(this, R.id.loader_image);
        mLoaderFrame = ButterKnife.findById(this, R.id.overlay_container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYPAL) {
            if (resultCode == RESULT_OK) {
                confirmOrder(data.getExtras());
            } else {
                mLoaderFrame.setVisibility(View.GONE);
                mNextButton.setEnabled(true);
                Toast.makeText(this, R.string.paypal_transaction_cancelled, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupViewPager() {
        mPagerAdapter = new StepsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);
                AbstractPage page = (AbstractPage) mCurrentPageSequence.get(position);
                page.onPageSelected();

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page page = mCurrentPageSequence.get(mPager.getCurrentItem());
                try {
                    notifyPageSave(page.getKey());
                    if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                        mNextButton.setEnabled(false);
                        mLoaderFrame.setVisibility(View.VISIBLE);
                        startOrder();
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                } catch (InvalidParameterException invalidParameterException) {
                    Toast.makeText(getBaseContext(), invalidParameterException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void startOrder() {
        if (mProcessType == BookingStepsModel.PROCESS_PAYPAL) {
            startActivityForResult(PayPalActivity.createIntent(mOrderItem, this), REQUEST_CODE_PAYPAL);
        } else {
            confirmOrder(new Bundle());
        }
    }

    @Override
    public void addPageFormInterface(FormData form) {
        mPageForms.put(form.getPageKey(), form);
    }

    @Override
    public void removePageFormInterface(FormData form) {
        mPageForms.remove(form.getPageKey());
    }

    protected void notifyPageSave(String key) {
        if (mPageForms.containsKey(key)) {
            mPageForms.get(key).savePageData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_ORDER_ITEM, mOrderItem);
        outState.putParcelable(EXTRA_SNIPPET, mHotelSnippet);
        outState.putBundle("model", mBookingStepsModel.save());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookingStepsModel.unregisterListener(this);
    }

    @Override
    public void onPageDataChanged(Page page) {
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mBookingStepsModel.getCurrentPageSequence();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size());
        if (mStepPagerStrip.getPageCount() == 1) {
            mStepPagerStrip.setVisibility(View.GONE);
        }
        AbstractPage page = (AbstractPage) mCurrentPageSequence.get(mPager.getCurrentItem());
        page.onPageSelected();

        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    @Override
    public Page onGetPage(String key) {
        if (mBookingStepsModel == null) {
            return null;
        }
        return mBookingStepsModel.findByKey(key);
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == (mCurrentPageSequence.size() - 1)) {
            mNextButton.setText(R.string.proceed_to_confirmation);
        } else {
            mNextButton.setText(R.string.next);
        }
    }

    private void confirmOrder(@NonNull Bundle transactionData) {
        try {
            OrderRequest request = createOrderRequest(transactionData);

            EtbApi etbApi = App.provide(this).etbApi();

            mResultsCallback.attach(this);
            etbApi.order(request).enqueue(mResultsCallback);
        } catch (InvalidParameterException invalidParameterException) {
            Toast.makeText(this, invalidParameterException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String metaToMessage(Meta meta) {
        StringBuilder message = new StringBuilder();
        message
                .append("StatusCode: ").append(meta.statusCode)
                .append(", ErrorCode: ").append(meta.errorCode)
                .append(", ErrorMessage: ").append(meta.errorMessage)
        ;
        return message.toString();
    }

    private void showErrorMessage(String message) {
        new AlertDialog.Builder(BookingProcessActivity.this)
                .setTitle(R.string.reservation_problem)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private OrderRequest createOrderRequest(@NonNull Bundle transactionDetails) throws InvalidParameterException {
        String customerIp = "";
        try {
            customerIp = NetworkUtils.getIPAddress();
        } catch (SocketException e) {
            AppLog.e(e);
        }
        OrderRequest.Builder builder = new OrderRequest.Builder()
                .setCurrency(mOrderItem.currency)
                .setLang("en")
                .setCustomerIP(customerIp)
                .addRate(mOrderItem.rateKey, mRequest.getNumberOfRooms(), null, "");

        if (mProcessType == BookingStepsModel.PROCESS_CREDITCARD_PREPAID) {
            // TODO: handle multiple types
            Bundle paymentData = mBookingStepsModel.findByKey(CreditCardDetailsPage.KEY).getData();
            ArrayList<String> cardTypes = CreditCardUtils.recognize(paymentData.getString(CreditCardDetailsPage.DATA_CARD_NUMBER));
            if (cardTypes.size() > 0) {
                transactionDetails.putString(PaymentRequestWrapper.SELECTED_CARD_TYPE, cardTypes.get(0));
            }
        }

        PaymentRequestWrapper paymentBuilder = new PaymentRequestWrapper(mBookingStepsModel, mProcessType, builder, this);
        paymentBuilder.setPayment(transactionDetails);

        String networkCountryIso = App.provide(this).getTelephonyManager().getNetworkCountryIso();
        if (TextUtils.isEmpty(networkCountryIso)) {
            networkCountryIso = Locale.getDefault().getCountry();
        }
        Bundle personalData = mBookingStepsModel.findByKey(PersonalInfoPage.KEY).getData();
        builder.setPersonal(
                personalData.getString(PersonalInfoPage.DATA_FIRST_NAME),
                personalData.getString(PersonalInfoPage.DATA_LAST_NAME),
                personalData.getString(PersonalInfoPage.DATA_PHONE),
                networkCountryIso.toUpperCase(),
                personalData.getString(PersonalInfoPage.DATA_EMAIL)
        );

        return builder.build();
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static class BookingException extends Throwable {
        public BookingException(String detailMessage) {
            super(detailMessage);
        }

        public BookingException(String detailMessage, Throwable cause) {
            super(detailMessage, cause);
        }
    }

    public class StepsPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mPrimaryItem;

        public StepsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return mCurrentPageSequence == null ? 0 : mCurrentPageSequence.size();
        }

    }
}
