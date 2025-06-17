package com.temptationjavaisland.wemeet.ui.welcome.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.repository.EventRepository;


public class EventViewModel extends ViewModel {

    private static final String TAG = EventViewModel.class.getSimpleName();

    private final EventRepository eventRepository;
    private final int page;
    private MutableLiveData<Result> eventsListLiveData;
    private MutableLiveData<Result> preferedEventsMutableLiveData;

    public EventViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.page = 1;
    }

    public MutableLiveData<Result> getEvents(String country, String city, String keyword, int page, long lastUpdate) {
        if (eventsListLiveData == null) {
            fetchEvents(country,city, keyword,page, lastUpdate);
        }
        return eventsListLiveData;
    }

    public MutableLiveData<Result> getFavoriteEventsLiveData() {
        if (preferedEventsMutableLiveData == null) {
            getFavoriteEvents();
        }
        return preferedEventsMutableLiveData;
    }


    public void updateEvent(Event event) {
        eventRepository.updateEvent(event);
    }

    private void fetchEvents(String country, String city, String keyword, int page, long lastUpdate) {
        eventsListLiveData = eventRepository.fetchEvents(country,city, keyword,page, lastUpdate);
    }

    private void getFavoriteEvents() {
        preferedEventsMutableLiveData = eventRepository.getFavoriteEvents();
    }

    public void removeFromFavorite(Event event) {
        eventRepository.updateEvent(event);
    }

    public void deleteAllFavoriteEvents() {
        eventRepository.deleteFavoriteEvents();
    }
}