package com.temptationjavaisland.wemeet.source;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.source.EventCallback;

import java.util.List;

public abstract class BaseEventLocalDataSource {
    protected EventCallback eventCallback;

    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public abstract void getEvents();

    public abstract void getFavoriteEvents();

    public abstract void updateEvent(Event event);

    public abstract void deleteFavoriteEvents();

    public abstract void insertEvents(List<Event> eventList);
}
