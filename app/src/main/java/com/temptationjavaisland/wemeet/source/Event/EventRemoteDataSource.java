package com.temptationjavaisland.wemeet.source.Event;

import static com.temptationjavaisland.wemeet.util.Constants.API_KEY_ERROR;
import static com.temptationjavaisland.wemeet.util.Constants.RETROFIT_ERROR;
import static com.temptationjavaisland.wemeet.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.service.EventAPIService;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRemoteDataSource extends BaseEventRemoteDataSource {

    private final EventAPIService eventAPIService;
    private final String apiKey;


    public EventRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.eventAPIService = ServiceLocator.getInstance().getEventAPIService();
    }


    //effettua una chiamata all’API remota per recuperare eventi basati sulla posizione e altri parametri
    @Override
    public void getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        Call<EventAPIResponse> eventsResponseCall = eventAPIService.getEventsByLocation(
                latlong, radius, unit, locale, TOP_HEADLINES_PAGE_SIZE_VALUE, apiKey);

        eventsResponseCall.enqueue(new Callback<EventAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventAPIResponse> call,
                                   @NonNull Response<EventAPIResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    EventAPIResponse responseBody = response.body();
                    Log.d("API_RESPONSE", "Raw response: " + new Gson().toJson(response.body()));
                    if (responseBody.getEmbedded() != null && responseBody.getEmbedded().getEvents() != null) {
                        eventCallback.onSuccessFromRemote(responseBody, System.currentTimeMillis());
                    } else {
                        eventCallback.onFailureFromRemote(new Exception("Nessun evento trovato nella risposta."));
                    }
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


    //effettua una chiamata all’API remota per la ricerca di eventi in base a una parola chiave
    @Override
    public void searchEvents(String keyword) {
        Call<EventAPIResponse> call = eventAPIService.searchEvents(keyword, apiKey);

        call.enqueue(new Callback<EventAPIResponse>() {
            @Override
            public void onResponse(Call<EventAPIResponse> call, Response<EventAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                } else {
                    eventCallback.onFailureFromRemote(new Exception("Errore risposta API"));
                }
            }

            @Override
            public void onFailure(Call<EventAPIResponse> call, Throwable t) {
                eventCallback.onFailureFromRemote(new Exception(t));
            }
        });
    }
}
