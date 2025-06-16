package com.temptationjavaisland.wemeet.repository;
package com.temptationjavaisland.wemeet.repository;

import androidx.lifecycle.MutableLiveData;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventApiResponseRetrofit;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.source.BaseEventLocalDataSource;
import com.temptationjavaisland.wemeet.source.BaseEventRemoteDataSource;
import com.temptationjavaisland.wemeet.source.EventCallback;

import java.util.List;

public class EventRepository implements EventCallback {

    private final MutableLiveData<Result> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Result> favoriteEventsLiveData = new MutableLiveData<>();

    private final BaseEventRemoteDataSource remoteDataSource;
    private final BaseEventLocalDataSource localDataSource;

    public EventRepository(BaseEventRemoteDataSource remoteDataSource,
                           BaseEventLocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;

        this.remoteDataSource.setEventCallback(this);
        this.localDataSource.setEventCallback(this);
    }

    public MutableLiveData<Result> fetchEvents(String location, long lastUpdate, long freshTimeout) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > freshTimeout) {
            remoteDataSource.getEvents(location);
        } else {
            localDataSource.getEvents();
        }
        return allEventsLiveData;
    }

    public MutableLiveData<Result> getFavoriteEvents() {
        localDataSource.getFavoriteEvents();
        return favoriteEventsLiveData;
    }

    public void updateEvent(Event event) {
        localDataSource.updateEvent(event);
    }

    public void deleteFavoriteEvents() {
        localDataSource.deleteFavoriteEvents();
    }

    @Override
    public void onSuccessFromRemote(EventApiResponseRetrofit response, long lastUpdate) {
        localDataSource.insertEvents(response.getEmbedded().getEvents());
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        allEventsLiveData.postValue(new Result.Error(exception.getMessage()));
    }

    @Override
    public void onSuccessFromLocal(List<Event> eventList) {
        allEventsLiveData.postValue(new Result.Success(eventList));
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error error = new Result.Error(exception.getMessage());
        allEventsLiveData.postValue(error);
        favoriteEventsLiveData.postValue(error);
    }

    @Override
    public void onFavoriteStatusChanged(Event event, List<Event> favoriteEvents) {
        Result allEventsResult = allEventsLiveData.getValue();
        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Event> oldEvents = ((Result.Success<List<Event>>) allEventsResult).getData();
            if (oldEvents.contains(event)) {
                oldEvents.set(oldEvents.indexOf(event), event);
                allEventsLiveData.postValue(allEventsResult);
            }
        }
        favoriteEventsLiveData.postValue(new Result.Success(favoriteEvents));
    }

    @Override
    public void onFavoriteStatusChanged(List<Event> events) {
        favoriteEventsLiveData.postValue(new Result.Success(events));
    }

    @Override
    public void onDeleteFavoriteSuccess(List<Event> favoriteEvents) {
        Result allEventsResult = allEventsLiveData.getValue();
        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Event> oldEvents = ((Result.Success<List<Event>>) allEventsResult).getData();
            for (Event event : favoriteEvents) {
                if (oldEvents.contains(event)) {
                    oldEvents.set(oldEvents.indexOf(event), event);
                }
            }
            allEventsLiveData.postValue(allEventsResult);
        }
        if (favoriteEventsLiveData.getValue() != null && favoriteEventsLiveData.getValue().isSuccess()) {
            favoriteEvents.clear();
            favoriteEventsLiveData.postValue(new Result.Success(favoriteEvents));
        }
    }
}
