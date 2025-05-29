package com.temptationjavaisland.wemeet.service;

import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_CITY_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_COUNTRY_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_ENDPOINT;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_KEYWORD_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_LATLONG_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_LOCALE_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_PAGE_SIZE_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_RADIUS_PARAMETER;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_UNIT_PARAMETER;

import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.util.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface EventAPIService {
    @GET(TOP_HEADLINES_ENDPOINT)
    Call<EventAPIResponse> getEvents(
            @Query(TOP_HEADLINES_COUNTRY_PARAMETER) String country, //Ricerca Paese
            @Query(TOP_HEADLINES_CITY_PARAMETER) String city, //Ricerca per Località
            @Query(TOP_HEADLINES_KEYWORD_PARAMETER) String keyword, //Ricerca per Artista/Evento
            @Query(TOP_HEADLINES_PAGE_SIZE_PARAMETER) int pageSize,
            @Header("Authorization")String apiKey);

    @GET(TOP_HEADLINES_ENDPOINT)
    Call<EventAPIResponse> getEventsByLocation(
            @Query(TOP_HEADLINES_LATLONG_PARAMETER) String latlong,
            @Query(TOP_HEADLINES_RADIUS_PARAMETER) int radius,
            @Query(TOP_HEADLINES_UNIT_PARAMETER) String unit,
            @Query(TOP_HEADLINES_LOCALE_PARAMETER) String locale,
            @Query("apikey") String apiKey);
}
