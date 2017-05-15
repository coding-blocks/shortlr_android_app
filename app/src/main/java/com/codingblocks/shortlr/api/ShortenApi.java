package com.codingblocks.shortlr.api;

import com.codingblocks.shortlr.models.PostBody;
import com.codingblocks.shortlr.models.Result;

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
