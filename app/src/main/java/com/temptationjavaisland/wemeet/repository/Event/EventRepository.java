package com.temptationjavaisland.wemeet.repository.Event;

import static com.temptationjavaisland.wemeet.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.source.Event.BaseEventLocalDataSource;
import com.temptationjavaisland.wemeet.source.Event.BaseEventRemoteDataSource;

import java.util.List;

public class EventRepository implements EventResponseCallback {

    private static final String TAG = EventRepository.class.getSimpleName();

    private final MutableLiveData<Result> allEventsMutableLiveData;
    private final MutableLiveData<Result> preferedEventsMutableLiveData;

    private final BaseEventRemoteDataSource eventRemoteDataSource;
    private final BaseEventLocalDataSource eventLocalDataSource;

    public EventRepository(BaseEventRemoteDataSource eventRemoteDataSource,
                           BaseEventLocalDataSource eventLocalDataSource) {

        allEventsMutableLiveData = new MutableLiveData<>();
        preferedEventsMutableLiveData = new MutableLiveData<>();
        this.eventRemoteDataSource = eventRemoteDataSource;
        this.eventLocalDataSource = eventLocalDataSource;

        //registra questo repository come callback per entrambi i data source
        this.eventRemoteDataSource.setEventCallback(this);
        this.eventLocalDataSource.setEventCallback(this);
    }


    //elimina tutti gli eventi in locale
    public void clearLocalEvents() {
        eventLocalDataSource.clearAllEvents();
    }


    //rimuove un evento dai preferiti tramite l'id
    public void unsetFavorite(String eventId) {
        eventLocalDataSource.unsetFavorite(eventId);
    }


    //restituisce tutti gli eventi da locale
    public List<Event> getAll() {
        return eventLocalDataSource.getAll();
    }


    //inserisce o aggiorna un evento in locale
    public void insertEvent(Event event) {
        eventLocalDataSource.insertOrUpdateEvent(event);
    }

    public MutableLiveData<Result> fetchEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            eventRemoteDataSource.getEventsLocation(latlong, radius, unit, locale, lastUpdate);
        } else {
            eventLocalDataSource.getEvents();
        }

        return allEventsMutableLiveData;
    }

    //ricerca eventi in remoto per parola chiave
    public MutableLiveData<Result> searchEvents(String keyword) {
        MutableLiveData<Result> searchResult = new MutableLiveData<>();

        //callback temporaneo per ricevere solo il risultato della ricerca
        EventResponseCallback tempCallback = new EventResponseCallback() {
            @Override
            public void onSuccessFromRemote(EventAPIResponse response, long lastUpdate) {
                searchResult.postValue(new Result.EventSuccess(response));
            }

            @Override
            public void onFailureFromRemote(Exception exception) {
                searchResult.postValue(new Result.Error(exception.getMessage()));
            }

            @Override public void onSuccessFromLocal(List<Event> eventList) {}
            @Override public void onFailureFromLocal(Exception exception) {}
            @Override public void onFavoriteStatusChanged(Event event, List<Event> favoriteEvents) {}
            @Override public void onFavoriteStatusChanged(List<Event> favoriteEvents) {}
            @Override public void onDeleteFavoriteSuccess(List<Event> favoriteEvents) {}
        };

        eventRemoteDataSource.setEventCallback(tempCallback);
        eventRemoteDataSource.searchEvents(keyword);

        return searchResult;
    }


    //restituisce eventi preferiti dalla sorgente locale
    public MutableLiveData<Result> getPreferedEvents() {
        eventLocalDataSource.getPreferedEvents();
        return preferedEventsMutableLiveData;
    }


    //aggiorna un evento nel database locale.
    public void updateEvent(Event Event) {
        eventLocalDataSource.updateEvent(Event);
    }


    //Elimina tutti gli eventi preferiti.
    public void deleteFavoriteEvents() {
        eventLocalDataSource.deletePreferedEvents();
    }


    //callback di successo da remoto: salva eventi nel DB locale
    public void onSuccessFromRemote(EventAPIResponse eventAPIResponse, long lastUpdate) {
        eventLocalDataSource.insertEvents(eventAPIResponse.getEmbedded().getEvents());
    }


    //callback di errore da remoto: notifica l'errore al LiveData
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(result);
    }

    //callback di successo da locale: restituisce eventi tramite LiveData
    public void onSuccessFromLocal(List<Event> eventList) {
        Result.EventSuccess result = new Result.EventSuccess(buildResponseFromEvents(eventList));
        allEventsMutableLiveData.postValue(result);
    }


    //Callback di errore da locale e notifica l'errore su entrambi i LiveData
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(resultError);
        preferedEventsMutableLiveData.postValue(resultError);
    }

    //Callback chiamato quando cambia lo stato di un preferito (aggiunta/rimozione).
    public void onFavoriteStatusChanged(Event event, List<Event> preferedEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Event> oldAllEvents = ((Result.EventSuccess) allEventsResult).getData().getEmbedded().getEvents();
            if (oldAllEvents.contains(event)) {
                oldAllEvents.set(oldAllEvents.indexOf(event), event);
                allEventsMutableLiveData.postValue(allEventsResult);
            }
        }

        preferedEventsMutableLiveData.postValue(new Result.EventSuccess(buildResponseFromEvents(preferedEvents)));
    }


    //Callback per aggiornamento completo della lista di preferiti.
    public void onFavoriteStatusChanged(List<Event> preferedEvents) {
        preferedEventsMutableLiveData.postValue(new Result.EventSuccess(buildResponseFromEvents(preferedEvents)));
    }


    //Ritorna i preferiti come LiveData osservabile.
    public LiveData<List<Event>> getSavedEventsLiveData() {
        return eventLocalDataSource.getSavedEventsLiveData();
    }

    //Callback chiamato dopo aver cancellato i preferiti con successo e aggiorna i LiveData di conseguenza.
    public void onDeleteFavoriteSuccess(List<Event> preferedEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Event> oldAllEvents = ((Result.EventSuccess) allEventsResult).getData().getEmbedded().getEvents();
            for (Event event : preferedEvents) {
                if (oldAllEvents.contains(event)) {
                    oldAllEvents.set(oldAllEvents.indexOf(event), event);
                }
            }
            allEventsMutableLiveData.postValue(allEventsResult);
        }

        if (preferedEventsMutableLiveData.getValue() != null &&
                preferedEventsMutableLiveData.getValue().isSuccess()) {
            preferedEvents.clear();
            Result.EventSuccess result = new Result.EventSuccess(buildResponseFromEvents(preferedEvents));
            preferedEventsMutableLiveData.postValue(result);
        }
    }

    //Costruisce una struttura EventAPIResponse a partire da una lista di eventi.
    private EventAPIResponse buildResponseFromEvents(List<Event> events) {
        EventAPIResponse response = new EventAPIResponse();
        EventAPIResponse.Embedded embedded = new EventAPIResponse.Embedded();
        embedded.setEvents(events);
        response.setEmbedded(embedded);
        return response;
    }


    //Ricarica i preferiti dalla sorgente locale.
    public void refreshPreferedEvents() {
        eventLocalDataSource.getPreferedEvents();
    }

    //Inserisce una lista di eventi nel database locale.
    public void insertEvents(List<Event> events) {
        eventLocalDataSource.insertEvents(events);
    }
}
