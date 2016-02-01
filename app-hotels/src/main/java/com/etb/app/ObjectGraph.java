package com.etb.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;

import com.easytobook.api.EtbApi;
import com.easytobook.api.EtbApiConfig;
import com.easytobook.api.mock.ResultsMockClient;
import com.easytobook.api.model.SearchRequest;
import com.etb.app.analytics.Facebook;
import com.etb.app.etbapi.CacheRequestInterceptor;
import com.etb.app.etbapi.CacheResponseInterceptor;
import com.etb.app.etbapi.RetrofitLogger;
import com.etb.app.etbapi.UserAgentInterceptor;
import com.etb.app.member.MemberAdapter;
import com.etb.app.member.MemberService;
import com.etb.app.member.MemberStorage;
import com.etb.app.model.HotelListRequest;
import com.etb.app.preferences.UserPreferences;
import com.etb.app.preferences.UserPreferencesStorage;
import com.etb.app.utils.DefaultHttpClient;
import com.etb.app.utils.NetworkUtilities;
import com.etb.app.utils.PriceRender;
import com.facebook.CallbackManager;
import com.facebook.device.yearclass.YearClass;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

/**
 * @author alex
 * @date 2015-04-07
 */
public class ObjectGraph {


    private static final long DISK_CACHE_SIZE = 5000000; // 5mbX
    private static final long CONNECT_TIMEOUT_MILLIS = 30000;
    private static final long READ_TIMEOUT_MILLIS = 40000;
    protected final Context app;
    private HotelListRequest mHotelsRequest;
    private Facebook mFacebook;
    private UserPreferences mUserPrefs;
    private EtbApi mEtbApi;
    private OkHttpClient mHttpClient;
    private SimpleArrayMap<String, PriceRender> mPriceRender;
    private MemberStorage mMemberStorage;
    private SearchRequest mLastSearchRequest;


    public ObjectGraph(Context applicationContext) {
        this.app = applicationContext;
    }

    public SearchRequest getLastSearchRequest() {
        return mLastSearchRequest;
    }

    public void updateLastSeatchRequest(SearchRequest request) {
        if (mLastSearchRequest == null) {
            mLastSearchRequest = new SearchRequest();
        }
        mLastSearchRequest.setType(request.getType());
        mLastSearchRequest.getDateRange().set(request.getDateRange());
        mLastSearchRequest.setNumberOfPersons(request.getNumberOfPersons());
        mLastSearchRequest.setNumbersOfRooms(request.getNumberOfRooms());
    }

    public HotelListRequest createHotelsRequest() {
        HotelListRequest request = new HotelListRequest();
        UserPreferences userPrefs = getUserPrefs();
        request.setLanguage(userPrefs.getLang());
        request.setCurrency(userPrefs.getCurrencyCode());
        request.setCustomerCountryCode(userPrefs.getCountryCode());
        return request;
    }

    public EtbApi etbApi() {
        if (mEtbApi == null) {
            EtbApiConfig cfg = new EtbApiConfig(Config.ETB_API_KEY, Config.ETB_API_CAMPAIGN_ID);
            cfg.setDebug(BuildConfig.DEBUG);
            cfg.setLogger(new RetrofitLogger());
            mEtbApi = new EtbApi(cfg, apiHttpClient());
        }
        return mEtbApi;
    }

    private OkHttpClient apiHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient(this.app);
            File directory = new File(this.app.getCacheDir(), "responses");

            mHttpClient.setCache(new Cache(directory, DISK_CACHE_SIZE));
            mHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            mHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

            mHttpClient.networkInterceptors().add(new CacheResponseInterceptor());
            mHttpClient.networkInterceptors().add(new UserAgentInterceptor(this.app));
            mHttpClient.interceptors().add(new CacheRequestInterceptor(new NetworkUtilities(connectivityManager())));
            mHttpClient.interceptors().add(RetrofitLogger.create());
            mHttpClient.interceptors().add(new ResultsMockClient());
        }
        return mHttpClient;
    }

    public UserPreferences getUserPrefs() {
        if (mUserPrefs == null) {
            UserPreferencesStorage storage = new UserPreferencesStorage(this.app);
            mUserPrefs = storage.load();
        }
        return mUserPrefs;
    }

    public TelephonyManager getTelephonyManager() {
        return (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public CallbackManager facebookCallbackManager() {
        return CallbackManager.Factory.create();
    }

    public Facebook facebook() {
        if (mFacebook == null) {
            mFacebook = new Facebook(app);
        }
        return mFacebook;
    }

    public ConnectivityManager connectivityManager() {
        return (ConnectivityManager) this.app.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public NetworkUtilities netUtils() {
        return new NetworkUtilities(connectivityManager());
    }


    public PriceRender priceRender(int numOfDays) {
        String currencyCode = getUserPrefs().getCurrencyCode();
        int priceShowType = getUserPrefs().getPriceShowType();
        if (mPriceRender == null || !mPriceRender.containsKey(currencyCode+"-"+numOfDays+"-"+priceShowType)) {
            mPriceRender = new SimpleArrayMap<>(1);
            PriceRender renderer = new PriceRender(priceShowType, getNumberFormatter(currencyCode), numOfDays);
            mPriceRender.put(currencyCode, renderer);
        }
        return mPriceRender.get(currencyCode);
    }

    public MemberStorage memberStorage() {
        if (mMemberStorage == null) {
            mMemberStorage = new MemberStorage(this.app);
        }
        return mMemberStorage;
    }

    public int getDeviceClass() {
        return YearClass.get(this.app);
    }

    public MemberService memberService() {
        DefaultHttpClient httpClient = new DefaultHttpClient(this.app);
        httpClient.setReadTimeout(3, TimeUnit.MINUTES);
        return (new MemberAdapter(memberStorage(), httpClient)).create();
    }

    public NumberFormat getNumberFormatter(String currencyCode) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
        if (formatter instanceof DecimalFormat) {
            formatter.setCurrency(Currency.getInstance(currencyCode));
        }
        return formatter;
    }

}
