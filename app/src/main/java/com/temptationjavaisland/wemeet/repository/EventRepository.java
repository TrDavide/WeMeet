package com.temptationjavaisland.wemeet.repository;

import static com.temptationjavaisland.wemeet.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.source.BaseEventLocalDataSource;
import com.temptationjavaisland.wemeet.source.BaseEventRemoteDataSource;

import java.util.List;

/**
 * Repository class to get the news from local or from a remote source.
 */
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
        this.eventRemoteDataSource.setEventCallback(this);
        this.eventLocalDataSource.setEventCallback(this);
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

    public MutableLiveData<Result> searchEvents(String keyword) {
        MutableLiveData<Result> searchResult = new MutableLiveData<>();

        // Qui NON puoi passare un callback anonimo, perché la firma del metodo non lo prevede.
        // Perciò devi usare il callback già settato in eventRemoteDataSource (eventResponseCallback)

        // Setta un callback temporaneo per intercettare la risposta della ricerca
        EventResponseCallback tempCallback = new EventResponseCallback() {
            @Override
            public void onSuccessFromRemote(EventAPIResponse response, long lastUpdate) {
                searchResult.postValue(new Result.EventSuccess(response));
            }

            @Override
            public void onFailureFromRemote(Exception exception) {
                searchResult.postValue(new Result.Error(exception.getMessage()));
            }

            // Implementazioni vuote per il resto
            @Override public void onSuccessFromLocal(List<Event> eventList) {}
            @Override public void onFailureFromLocal(Exception exception) {}
            @Override public void onFavoriteStatusChanged(Event event, List<Event> favoriteEvents) {}
            @Override public void onFavoriteStatusChanged(List<Event> favoriteEvents) {}
            @Override public void onDeleteFavoriteSuccess(List<Event> favoriteEvents) {}
        };

        // Temporaneamente imposta questo callback sul remote data source
        eventRemoteDataSource.setEventCallback(tempCallback);

        // Avvia la ricerca
        eventRemoteDataSource.searchEvents(keyword);

        return searchResult;
    }

    public MutableLiveData<Result> getPreferedEvents() {
        eventLocalDataSource.getPreferedEvents();
        return preferedEventsMutableLiveData;
    }

    public void updateEvent(Event Event) {
        eventLocalDataSource.updateEvent(Event);
    }

    public void deleteFavoriteEvents() {
        eventLocalDataSource.deletePreferedEvents();
    }

    public void onSuccessFromRemote(EventAPIResponse eventAPIResponse, long lastUpdate) {
        eventLocalDataSource.insertEvents(eventAPIResponse.getEmbedded().getEvents());
    }

    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(result);
    }

    public void onSuccessFromLocal(List<Event> eventList) {
        Result.EventSuccess result = new Result.EventSuccess(buildResponseFromEvents(eventList));
        allEventsMutableLiveData.postValue(result);
    }

    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(resultError);
        preferedEventsMutableLiveData.postValue(resultError);
    }

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

    public void onFavoriteStatusChanged(List<Event> preferedEvents) {
        preferedEventsMutableLiveData.postValue(new Result.EventSuccess(buildResponseFromEvents(preferedEvents)));
    }

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

    private EventAPIResponse buildResponseFromEvents(List<Event> events) {
        EventAPIResponse response = new EventAPIResponse();
        EventAPIResponse.Embedded embedded = new EventAPIResponse.Embedded();
        embedded.setEvents(events);
        response.setEmbedded(embedded);
        return response;
    }
}
