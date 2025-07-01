package com.temptationjavaisland.wemeet.source.Event;

import com.temptationjavaisland.wemeet.repository.Event.EventResponseCallback;

public abstract class BaseEventRemoteDataSource {
    protected EventResponseCallback eventCallback;

    public void setEventCallback(EventResponseCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public abstract void getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate);

    public abstract void searchEvents(String keyword);
}