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

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface EventAPIService {

    /**
     * Metodo per recuperare eventi in base a parametri di paese, città, parola chiave e dimensione pagina.
     * La chiave API viene passata tramite header Authorization.
     */
    @GET(TOP_HEADLINES_ENDPOINT)
    Call<EventAPIResponse> getEvents(
            @Query(TOP_HEADLINES_COUNTRY_PARAMETER) String country,
            @Query(TOP_HEADLINES_CITY_PARAMETER) String city,
            @Query(TOP_HEADLINES_KEYWORD_PARAMETER) String keyword,
            @Query(TOP_HEADLINES_PAGE_SIZE_PARAMETER) int pageSize,
            @Header("Authorization") String apiKey);

    /**
     * Metodo per ottenere eventi basati su latitudine, longitudine, raggio di ricerca, unità di misura e impostazione locale.
     * La chiave API viene passata come query parameter.
     */
    @GET(TOP_HEADLINES_ENDPOINT)
    Call<EventAPIResponse> getEventsByLocation(
            @Query(TOP_HEADLINES_LATLONG_PARAMETER) String latlong,
            @Query(TOP_HEADLINES_RADIUS_PARAMETER) int radius,
            @Query(TOP_HEADLINES_UNIT_PARAMETER) String unit,
            @Query(TOP_HEADLINES_LOCALE_PARAMETER) String locale,
            @Query(TOP_HEADLINES_PAGE_SIZE_PARAMETER) int pageSize,
            @Query("apikey") String apiKey);

    /**
     * Metodo per effettuare ricerche eventi tramite parola chiave su endpoint dedicato.
     * La chiave API è passata come query parameter.
     */
    @GET("events.json")
    Call<EventAPIResponse> searchEvents(
            @Query("keyword") String keyword,
            @Query("apikey") String apiKey);
}
