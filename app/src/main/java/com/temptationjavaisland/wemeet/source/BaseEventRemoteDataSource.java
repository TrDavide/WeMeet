package com.temptationjavaisland.wemeet.source;

import com.temptationjavaisland.wemeet.repository.EventResponseCallback;

/**

 Base class to get events from a remote source.*/
public abstract class BaseEventRemoteDataSource {
    protected EventResponseCallback eventCallback;

    public void setEventCallback(EventResponseCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    //public abstract void getEvents(String country, String city, String keyword, int page, long lastUpdate);

    public abstract void getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate);
}