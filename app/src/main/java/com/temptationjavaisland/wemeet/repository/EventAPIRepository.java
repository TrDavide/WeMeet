package com.temptationjavaisland.wemeet.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventDAO;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.service.EventAPIService;
import com.temptationjavaisland.wemeet.service.ServiceLocator;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.ResponseCallBack;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAPIRepository implements IEventRepository{

    private static final String TAG = EventAPIRepository.class.getSimpleName();

    private final EventAPIService eventAPIService;
    private final EventDAO eventDAO;
    private Application application;
    private ResponseCallBack responseCallback;

    public EventAPIRepository(Application application, ResponseCallBack responseCallback){
        this.application = application;
        this.eventAPIService = ServiceLocator.getInstance().getEventAPIService();
        EventRoomDatabase eventRoomDatabase = ServiceLocator.getInstance().getEventsDB(application);
        this.eventDAO = eventRoomDatabase.eventsDao();
        this.responseCallback = responseCallback;
    }


    @Override
    public void fetchEvents(String country, String city, String keyword, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (true) {
            Call<EventAPIResponse> eventResponseCall = eventAPIService.getEvents(country,
                    city,
                    keyword,
                    Constants.TOP_HEADLINES_PAGE_SIZE_VALUE,
                    application.getString(R.string.ticketmaster_key)
            );

            eventResponseCall.enqueue(new Callback<EventAPIResponse>() {
                @Override
                public void onResponse(@NonNull Call<EventAPIResponse> call,
                                       @NonNull Response<EventAPIResponse> response) {

                    if (response.body() != null && response.isSuccessful()) {
                        List<Event> eventList = response.body().getEmbedded().getEvents();
                        //Event.filterArticles(articleList);
                        saveDataInDatabase(eventList);
                    } else {
                        responseCallback.onFailure(application.getString(R.string.error_retrieving_events));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EventAPIResponse> call, @NonNull Throwable t) {
                    readDataFromDatabase(lastUpdate);
                }
            });
        } else {
            readDataFromDatabase(lastUpdate);
        }

    }

    public void fetchEventsByLocation(String latlong, int radius, long lastUpdate) {
        Call<EventAPIResponse> eventResponseCall = eventAPIService.getEventsByLocation(
                latlong,
                radius,
                "km",           // oppure "miles"
                "*",            // tutte le lingue/localizzazioni
                application.getString(R.string.ticketmaster_key)
        );

        eventResponseCall.enqueue(new Callback<EventAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventAPIResponse> call, @NonNull Response<EventAPIResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("API_DEBUG", "Risposta ricevuta: " + response.body());

                    if (response.body() != null && response.body().getEmbedded() != null) {
                        List<Event> eventList = response.body().getEmbedded().getEvents();
                        saveDataInDatabase(eventList);
                    } else {
                        Log.d("API_DEBUG", "Nessun evento trovato");
                        responseCallback.onFailure("Nessun evento trovato");
                    }
                } else {
                    Log.e("API_DEBUG", "Risposta NON OK: " + response.code());
                    responseCallback.onFailure("Errore nella risposta");
                }
            }


            @Override
            public void onFailure(@NonNull Call<EventAPIResponse> call, @NonNull Throwable t) {
                readDataFromDatabase(lastUpdate);
            }
        });
    }


    @Override
    public void updateEvents(Event event) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDAO.updateEvent(event);
        });
    }

    @Override
    public void getFavoriteEvents() {}

    @Override
    public void deleteFavoriteEvents() {}


    private void saveDataInDatabase(List<Event> apiEvents) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> localEvents = eventDAO.getAll();

            for (Event event : localEvents) {
                if (apiEvents.contains(event)) {
                    apiEvents.set(apiEvents.indexOf(event), event);
                }
            }
            List<Long> insertedNewsIds = eventDAO.insertEventsList(apiEvents);
            for (int i = 0; i < apiEvents.size(); i++) {
                apiEvents.get(i).setUid(Math.toIntExact(insertedNewsIds.get(i)));
            }
            eventDAO.updateEventList(apiEvents);//codice modificata

            responseCallback.onSuccess(apiEvents, System.currentTimeMillis());
        });
    }

    private void readDataFromDatabase(long lastUpdate) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(eventDAO.getAll(), lastUpdate);
        });
    }

}
