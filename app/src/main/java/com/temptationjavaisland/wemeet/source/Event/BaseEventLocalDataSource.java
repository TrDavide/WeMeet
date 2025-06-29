package com.temptationjavaisland.wemeet.source.Event;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.Event.EventResponseCallback;

import java.util.List;

public abstract class BaseEventLocalDataSource {
    protected EventResponseCallback eventCallback;

    public void setEventCallback(EventResponseCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public abstract void getEvents();

    public abstract void getPreferedEvents();

    public abstract void updateEvent(Event event);

    public abstract void deletePreferedEvents();

    public abstract void insertEvents(List<Event> eventList);
}
