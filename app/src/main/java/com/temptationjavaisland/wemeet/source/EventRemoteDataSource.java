package com.temptationjavaisland.wemeet.source;

import static com.unimib.worldnews.util.Constants.API_KEY_ERROR;
import static com.unimib.worldnews.util.Constants.RETROFIT_ERROR;

import androidx.annotation.NonNull;

import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.service.EventAPIService;
import com.unimib.worldnews.util.ServiceLocator;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;
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

    @Override
    public void getEvents(String city) {
        Call<EventAPIResponse> eventsCall = eventAPIService.getEvents(city, apiKey);

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
