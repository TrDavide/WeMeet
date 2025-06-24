package com.temptationjavaisland.wemeet.source;

import static com.temptationjavaisland.wemeet.util.Constants.API_KEY_ERROR;
import static com.temptationjavaisland.wemeet.util.Constants.RETROFIT_ERROR;

import androidx.annotation.NonNull;

import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.service.EventAPIService;
import com.temptationjavaisland.wemeet.service.ServiceLocator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class to get events from a remote source using Retrofit.
 */
public class EventRemoteDataSource extends BaseEventRemoteDataSource {

    private final EventAPIService eventAPIService;
    private final String apiKey;

    public EventRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.eventAPIService = ServiceLocator.getInstance().getEventAPIService();
    }

    /*@Override
    public void getEvents(String country, String city, String keyword, int pageSize, long lastUpdate) {
        Call<EventAPIResponse> eventsCall = eventAPIService.getEvents(country,city, keyword,pageSize,apiKey);
        eventsCall.enqueue(new Callback<EventAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventAPIResponse> call,
                                   @NonNull Response<EventAPIResponse> response) {

                if (response.body() != null && response.isSuccessful()) {
                    eventCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                } else {
                    eventCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventAPIResponse> call, @NonNull Throwable t) {
                eventCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }*/

    @Override
    public void getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        Call<EventAPIResponse> eventsCall = eventAPIService.getEventsByLocation(latlong,radius, unit,locale,apiKey);
        eventsCall.enqueue(new Callback<EventAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventAPIResponse> call,
                                   @NonNull Response<EventAPIResponse> response) {

                if (response.body() != null && response.isSuccessful()) {
                    eventCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                } else {
                    eventCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventAPIResponse> call, @NonNull Throwable t) {
                eventCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }

}