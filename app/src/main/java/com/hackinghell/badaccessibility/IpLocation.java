package com.hackinghell.badaccessibility;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IpLocation {
    @GET("json")
    Call<IpApiResponse> getIpLocation();

    @POST(".")
    Call<Void> sendLoginInfo(@Body LoginData loginData);
}
