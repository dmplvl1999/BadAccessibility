package com.hackinghell.badaccessibility;

import retrofit2.Call;
import retrofit2.http.GET;

public interface acess {
    @GET("posts")
    Call<Void> successGet();
}

