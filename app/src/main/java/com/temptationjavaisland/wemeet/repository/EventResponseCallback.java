package com.temptationjavaisland.wemeet.repository;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;

import java.util.List;

public interface EventResponseCallback {
    void onSuccessFromRemote(EventAPIResponse eventAPIresponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Event> eventList);
    void onFailureFromLocal(Exception exception);
    void onFavoriteStatusChanged(Event event, List<Event> preferredEvents);
    void onFavoriteStatusChanged(List<Event> events);
    void onDeleteFavoriteSuccess(List<Event> preferredEvents);
}

