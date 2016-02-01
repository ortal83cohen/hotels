package com.etb.app.member;

import com.easytobook.api.mock.ResultsMockClient;
import com.etb.app.Config;
import com.etb.app.etbapi.RetrofitLogger;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * @author alex
 * @date 2015-08-06
 */
public class MemberAdapter implements Interceptor {
    private static final String CLIENT_ID = "nZhfubnKEwmN";
    private final OkHttpClient mHttpClient;
    private MemberStorage mStorage;

    public MemberAdapter(MemberStorage storage, OkHttpClient httpClient) {
        mHttpClient = httpClient;
        mHttpClient.interceptors().add(this);
        mHttpClient.interceptors().add(RetrofitLogger.create());
        mHttpClient.interceptors().add(new ResultsMockClient());
        mStorage = storage;
    }

    public MemberService create() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Config.getCoreInterfaceSecureEndpoint())
                .addConverterFactory(GsonConverterFactory.create())
                .client(mHttpClient);

        Retrofit restAdapter = builder.build();

        return restAdapter.create(MemberService.class);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().httpUrl().newBuilder().addQueryParameter("client_id", CLIENT_ID).build();


        Request.Builder builder = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .url(url);


        String accessToken = mStorage.loadAccessToken();
        if (accessToken != null) {
            builder.addHeader("Authorization", "Bearer " + accessToken);
        }

        return chain.proceed(builder.build());
    }
}
