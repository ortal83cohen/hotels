package com.etb.app.core;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.easytobook.api.mock.ResultsMockClient;
import com.easytobook.api.model.search.Poi;
import com.etb.app.Config;
import com.etb.app.core.model.Ratings;
import com.etb.app.core.model.ReviewResponse;
import com.etb.app.etbapi.RetrofitLogger;
import com.etb.app.etbapi.UserAgentInterceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author alex
 * @date 2015-04-07
 */
public class CoreInterface {

    public static Service create(Context context) {

        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new UserAgentInterceptor(context));
        client.interceptors().add(RetrofitLogger.create());
        client.interceptors().add(new ResultsMockClient());

        Retrofit restAdapter = new Retrofit.Builder()
                .client(client)
                .baseUrl(Config.getCoreInterfaceEndpoint())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return restAdapter.create(Service.class);
    }


    public interface Service {

        @Headers("Accept: application/json")
//        @GET("/core_interface/sway.php/hotels/{id}/reviews")
//                Call<ReviewResponse> hotelReviews(@Path("id") int hotelId, @Query("lang") String lang, @Query("page") int page);
        @GET("/etbstatic/GetReviews.json")
        Call<ReviewResponse> hotelReviews( @Query("lang") String lang, @Query("page") int page);

        @Headers("Accept: application/json")
//        @GET("/core_interface/sway.php/hotels/{id}/ratings")
//              Call<Ratings> hotelRatings(@Path("id") int hotelId);
        @GET("/etbstatic/GetRatings.json")
        Call<Ratings> hotelRatings();

        @Headers("Accept: application/json")
        @GET("/core_interface/sway.php/phone/customerservice/{country}")
        Call<String> customerServicePhone(@Path("country") String countryCode);

        @Headers("Accept: application/json")
        @GET("/core_interface/sway.php/uripars")
        Call<ArrayMap<String, String>> uriParse(@Query("uri") String uri);


        @Headers("Accept: application/json")
        @GET("/core_interface/sway.php/poi/list")
        Call<List<Poi>> poiList(@Query("lon") String lon, @Query("lat") String lat, @Query("radius") String radiusInMeters);

        @Headers("Accept: application/json")
        @GET("/core_interface/sway.php/poi/list")
        Call<List<Poi>> poiList(@Query("northeastlon") String northeastlon, @Query("northeastlat") String northeastlat, @Query("southwestlon") String southwestlon, @Query("southwestlat") String southwestlat);
    }


}
