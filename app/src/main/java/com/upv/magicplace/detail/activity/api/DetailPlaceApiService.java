package com.upv.magicplace.detail.activity.api;

import com.upv.magicplace.response.DetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DetailPlaceApiService {


    @GET("details/json")
    Call<DetailResponse> getDetail(@Query("placeid") String id, @Query("key") String key);

}
