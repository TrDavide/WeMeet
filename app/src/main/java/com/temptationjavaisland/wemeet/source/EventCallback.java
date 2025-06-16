package com.temptationjavaisland.wemeet.source;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;

import java.util.List;

public interface EventCallback {
    void onSuccessFromRemote(EventAPIResponse response, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Event> eventList);
    void onFailureFromLocal(Exception exception);
    void onFavoriteStatusChanged(Event event, List<Event> favoriteEvents);
    void onFavoriteStatusChanged(List<Event> events);
    void onDeleteFavoriteSuccess(List<Event> favoriteEvents);
}

