package com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;

import java.util.List;


public class EventViewModel extends ViewModel {

    private static final String TAG = EventViewModel.class.getSimpleName();

    private final EventRepository eventRepository;
    private final int page;
    private MutableLiveData<Result> eventsListLiveData;
    private MutableLiveData<Result> preferedEventsListLiveData;
    private final LiveData<List<Event>> savedEventsLiveData;

    public EventViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.savedEventsLiveData = eventRepository.getSavedEventsLiveData();
        this.page = 1;
    }
    public void clearLocalEvents() {
        eventRepository.clearLocalEvents();
    }

    public LiveData<List<Event>> getSavedEventsLiveData() {
        return savedEventsLiveData;
    }

    public void insertEvents(List<Event> events) {
        eventRepository.insertEvents(events);
    }

    public List<Event> ottineiEventiSalvatiLocal() {
        return eventRepository.ottineiEventiSalvatiLocal();
    }

    public List<Event> getAll() {
        return eventRepository.getAll();
    }

    public void insertEvent(Event event) {
        eventRepository.insertEvent(event);
    }

    public void unsetFavorite(String eventId) {
        eventRepository.unsetFavorite(eventId);
    }

    public MutableLiveData<Result> getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        if (eventsListLiveData == null) {
            fetchEventsLocation(latlong,radius, unit,locale, lastUpdate);
        }
        return eventsListLiveData;
    }

    public LiveData<Result> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword);
    }

    public MutableLiveData<Result> getPreferedEventsLiveData() {
        preferedEventsListLiveData = eventRepository.getPreferedEvents();
        return preferedEventsListLiveData;
    }

    public void updateEvent2() {
        eventRepository.getPreferedEvents();
    }

    public void updateEvent(Event event) {
        eventRepository.updateEvent(event);
    }

    private void fetchEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        eventsListLiveData = eventRepository.fetchEventsLocation(latlong,radius, unit,locale, lastUpdate);
    }

    private void getPreferedEvents() {
        preferedEventsListLiveData = eventRepository.getPreferedEvents();
    }

    public void removeFromFavorite(Event event) {
        eventRepository.updateEvent(event);
    }

    public void deleteAllFavoriteEvents() {
        eventRepository.deleteFavoriteEvents();
    }

    public void refreshPreferedEvents() {
        eventRepository.refreshPreferedEvents();
    }



}