package com.temptationjavaisland.wemeet.model;

import com.temptationjavaisland.wemeet.model.EventApiResponseRetrofit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventService {

    @GET("events")
    Call<EventApiResponseRetrofit> getEvents(@Query("apikey") String apiKey);

    @GET("events")
    Call<EventApiResponseRetrofit> searchEvents(@Query("apikey") String apiKey, @Query("keyword") String keyword);
}
