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

    /*public MutableLiveData<Result> fetchEvents(String latlong, int radius, String unit, String locale, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            eventRemoteDataSource.getEvents(latlong, radius, unit,locale, lastUpdate);
        } else {
            eventLocalDataSource.getEvents();
        }

        return allEventsMutableLiveData;
    }*/

    public MutableLiveData<Result> fetchEventsLocation(String latlong, int radius, String unit, String locale, int pageSize, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            //da controllare i parametri
            eventRemoteDataSource.getEventsLocation(latlong, radius, unit,locale, pageSize, lastUpdate);
        } else {
            eventLocalDataSource.getEvents();
        }

        return allEventsMutableLiveData;
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
        Result.EventSuccess result = new Result.EventSuccess(new EventAPIResponse(eventList));
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
            List<Event> oldAllEvents  = ((Result.EventSuccess) allEventsResult).getData().getEmbedded().getEvents();
            if (oldAllEvents.contains(event)) {
                oldAllEvents.set(oldAllEvents.indexOf(event), event);
                allEventsMutableLiveData.postValue(allEventsResult);
            }
        }
        preferedEventsMutableLiveData.postValue(new Result.EventSuccess(new EventAPIResponse(preferedEvents)));
    }


    public void onFavoriteStatusChanged(List<Event> preferedEvents) {
        preferedEventsMutableLiveData.postValue(new Result.EventSuccess(new EventAPIResponse(preferedEvents)));
    }

    public void onDeleteFavoriteSuccess(List<Event> preferedEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Event> oldAllEvents = ((Result.EventSuccess)allEventsResult).getData().getEmbedded().getEvents();
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
            Result.EventSuccess result = new Result.EventSuccess(new EventAPIResponse(preferedEvents));
            preferedEventsMutableLiveData.postValue(result);
        }
    }
}