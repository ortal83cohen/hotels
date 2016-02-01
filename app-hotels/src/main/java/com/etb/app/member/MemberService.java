package com.etb.app.member;

import com.etb.app.member.model.AccessToken;
import com.etb.app.member.model.BookingEvent;
import com.etb.app.member.model.JoinRequest;
import com.etb.app.member.model.Profile;
import com.etb.app.member.model.User;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * @author alex
 * @date 2015-08-06
 */
public interface MemberService {
    @GET("/membership/rest.php/login")
    Call<AccessToken> login(@Query("email") String email, @Query("password") String password);

    @GET("/membership/rest.php/login?provider=lr")
    Call<AccessToken> loginRadius(@Query("token") String token);

    @POST("/membership/rest.php/join")
    Call<AccessToken> join(@Body JoinRequest request);

    @GET("/membership/rest.php/join/confirm")
    Call<AccessToken> confirm(@Query("token") String token);

    @GET("/membership/rest.php/profile")
    Call<User> profile();

    @PUT("/membership/rest.php/profile")
    Call<Void> profile(@Body Profile profile);

    @GET("/membership/rest.php/profile/events?type=1")
    Call<List<BookingEvent>> bookings();

    @GET("/membership/rest.php/logout")
    Call<Void> logout();

    @GET("/membership/rest.php/password/reset")
    Call<Void> passwordReset();

    @GET("/membership/rest.php/password/change")
    Call<Void> passwordChange();
}
