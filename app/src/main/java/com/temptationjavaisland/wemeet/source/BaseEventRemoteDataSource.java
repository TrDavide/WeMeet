package com.temptationjavaisland.wemeet.source;

/**
 * Base class to get events from a remote source.
 */
public abstract class BaseEventRemoteDataSource {
    protected EventCallback eventCallback;

    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public abstract void getEvents(String country, String city, String keyword, int page, long lastUpdate);
}
