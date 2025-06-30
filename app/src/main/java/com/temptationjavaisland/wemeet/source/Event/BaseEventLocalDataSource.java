package com.temptationjavaisland.wemeet.source.Event;

import androidx.lifecycle.LiveData;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.Event.EventResponseCallback;

import java.util.List;

public abstract class BaseEventLocalDataSource {
    protected EventResponseCallback eventCallback;

    public void setEventCallback(EventResponseCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public abstract void getEvents();

    public abstract void clearAllEvents();

    public abstract LiveData<List<Event>> getSavedEventsLiveData();

    public abstract void insertOrUpdateEvent(Event event);

    public abstract void unsetFavorite(String eventId);

    public abstract List<Event> getAll();

    public abstract List<Event> ottieniEventisalvati();

    public abstract void getPreferedEvents();

    public abstract void updateEvent(Event event);

    public abstract void deletePreferedEvents();

    public abstract void insertEvents(List<Event> eventList);
}
