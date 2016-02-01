package com.easytobook.api;

import com.easytobook.api.utils.HttpLoggingInterceptor;

/**
 * @author alex
 * @date 2015-04-28
 */
public class EtbApiConfig {

//        public static final String ETB_API_ENDPOINT_DEFAULT = "http://api.easytobook.com";
//    public static final String ETB_API_ENDPOINT_SECURE = "https://api.easytobook.com";
    public static final String ETB_API_ENDPOINT_DEFAULT = "http://maorbolo.com";
    public static final String ETB_API_ENDPOINT_SECURE = "http://maorbolo.com";
    private String mApiKey;
    private String mEndpoint = ETB_API_ENDPOINT_DEFAULT;


    private String mSecureEndpoint = ETB_API_ENDPOINT_SECURE;

    private boolean mDebug;
    private int mCampaignId;
    private HttpLoggingInterceptor.Logger logger;

    public EtbApiConfig(String apiKey, int campaignId) {
        this.mApiKey = apiKey;
        this.mCampaignId = campaignId;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
    }

    public String getEndpoint() {
        return mEndpoint;
    }

    public void setEndpoint(String endpoint) {
        mEndpoint = endpoint;
    }

    public String getSecureEndpoint() {
        return mSecureEndpoint;
    }

    public void setSecureEndpoint(String secureEndpoint) {
        mSecureEndpoint = secureEndpoint;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    public int getCampaignId() {
        return mCampaignId;
    }

    public HttpLoggingInterceptor.Logger getLogger() {
        return logger;
    }

    public void setLogger(HttpLoggingInterceptor.Logger logger) {
        this.logger = logger;
    }
}
