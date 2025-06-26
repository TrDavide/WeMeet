package com.temptationjavaisland.wemeet.ui.welcome.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.repository.EventRepository;

import java.util.List;


public class EventViewModel extends ViewModel {

    private static final String TAG = EventViewModel.class.getSimpleName();

    private final EventRepository eventRepository;
    private final int page;
    private MutableLiveData<Result> eventsListLiveData;
    private MutableLiveData<Result> preferedEventsListLiveData;

    public EventViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.page = 1;
    }


    /*public MutableLiveData<Result> getEvents(String country, String city, String keyword, int page, long lastUpdate) {
        if (eventsListLiveData == null) {
            fetchEvents(country,city, keyword,page, lastUpdate);
        }
        return eventsListLiveData;
    }*/

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



    public void updateEvent(Event event) {
        eventRepository.updateEvent(event);
    }

    private void fetchEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        eventsListLiveData = eventRepository.fetchEventsLocation(latlong,radius, unit,locale, lastUpdate);
    }

    /*private void fetchEvents(String country, String city, String keyword, int page, long lastUpdate) {
        eventsListLiveData = eventRepository.fetchEvents(country,city, keyword,page, lastUpdate);
    }*/

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