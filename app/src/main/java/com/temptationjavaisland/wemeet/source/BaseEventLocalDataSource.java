package com.temptationjavaisland.wemeet.source;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.EventResponseCallback;

import java.util.List;

public abstract class BaseEventLocalDataSource {
    protected EventResponseCallback eventResponseCallback;

    public void setEventCallback(EventResponseCallback eventResponseCallback) {
        this.eventResponseCallback = eventResponseCallback;
    }

    public abstract void getEvents();

    public abstract void getFavoriteEvents();

    public abstract void updateEvent(Event event);

    public abstract void deleteFavoriteEvents();

    public abstract void insertEvents(List<Event> eventList);
}
