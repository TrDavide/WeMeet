package com.temptationjavaisland.wemeet.repository;
import static com.temptationjavaisland.wemeet.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.source.EventCallback;
import com.temptationjavaisland.wemeet.source.BaseEventLocalDataSource;
import com.temptationjavaisland.wemeet.source.BaseEventRemoteDataSource;

import java.util.List;


/**
 * Repository class to get the news from local or from a remote source.
 */
public class EventRepository implements EventCallback {

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

    public MutableLiveData<Result> fetchEvents(String country, String city, String keyword, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            eventRemoteDataSource.getEvents(country, city, keyword, page, lastUpdate);
        } else {
            eventLocalDataSource.getEvents();
        }

        return allEventsMutableLiveData;
    }

    public MutableLiveData<Result> getFavoriteEvents() {
        eventLocalDataSource.getFavoriteEvents();
        return preferedEventsMutableLiveData;
    }

    public void updateEvent(Event Event) {
        eventLocalDataSource.updateEvent(Event);
    }

    public void deleteFavoriteEvents() {
        eventLocalDataSource.deleteFavoriteEvents();
    }

    public void onSuccessFromRemote(EventAPIResponse EventApiResponse, long lastUpdate) {
        eventLocalDataSource.insertEvents(EventApiResponse.getEmbedded().getEvents());
    }

    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(result);
    }

    public void onSuccessFromLocal(List<Event> EventList) {
        Result.Success result = new Result.Success(new EventAPIResponse(EventList));
        allEventsMutableLiveData.postValue(result);
    }

    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(resultError);
        preferedEventsMutableLiveData.postValue(resultError);
    }


    public void onFavoriteStatusChanged(Event event, List<Event> favoriteEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            EventAPIResponse data = ((Result.Success) allEventsResult).getData();
            if (data.getEmbedded() != null) {
                List<Event> oldAllEvents = data.getEmbedded().getEvents();
                if (oldAllEvents.contains(event)) {
                    oldAllEvents.set(oldAllEvents.indexOf(event), event);
                    allEventsMutableLiveData.postValue(allEventsResult);
                }
            }
        }

        preferedEventsMutableLiveData.postValue(new Result.Success(new EventAPIResponse(favoriteEvents)));
    }


    public void onFavoriteStatusChanged(List<Event> favoriteEvents) {
        preferedEventsMutableLiveData.postValue(new Result.Success(new EventAPIResponse(favoriteEvents)));
    }

    public void onDeleteFavoriteSuccess(List<Event> favoriteEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Event> oldAllEvents = ((Result.Success)allEventsResult).getData().getEmbedded().getEvents();
            for (Event event : favoriteEvents) {
                if (oldAllEvents.contains(event)) {
                    oldAllEvents.set(oldAllEvents.indexOf(event), event);
                }
            }
            allEventsMutableLiveData.postValue(allEventsResult);
        }

        if (preferedEventsMutableLiveData.getValue() != null &&
                preferedEventsMutableLiveData.getValue().isSuccess()) {
            favoriteEvents.clear();
            Result.Success result = new Result.Success(new EventAPIResponse(favoriteEvents));
            preferedEventsMutableLiveData.postValue(result);
        }
    }
}