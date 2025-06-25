package com.temptationjavaisland.wemeet.source;

import com.temptationjavaisland.wemeet.repository.EventResponseCallback;

/**

 Base class to get events from a remote source.*/
public abstract class BaseEventRemoteDataSource {
    protected EventResponseCallback eventResponseCallback;

    public void setEventCallback(EventResponseCallback eventResponseCallback) {
        this.eventResponseCallback = eventResponseCallback;
    }

    //public abstract void getEvents(String country, String city, String keyword, int page, long lastUpdate);

    public abstract void getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate);
}