package com.codingblocks.shortlr;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by piyush0 on 11/04/17.
 */

public interface ShortenApi {
    @POST("shorten")
    Call<Result> getResult(@Body PostBody postBody);
}
