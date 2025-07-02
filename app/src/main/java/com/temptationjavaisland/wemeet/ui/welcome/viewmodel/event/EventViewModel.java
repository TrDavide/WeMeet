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

    //metodo per pulire eventi salvati localmente
    public void clearLocalEvents() {
        eventRepository.clearLocalEvents();
    }

    //restituisce eventi salvati come LiveData osservabile
    public LiveData<List<Event>> getSavedEventsLiveData() {
        return savedEventsLiveData;
    }

    //inserisce una lista di eventi nel repository localmente
    public void insertEvents(List<Event> events) {
        eventRepository.insertEvents(events);
    }

    //recupera tutti gli eventi salvati localmente
    public List<Event> getAll() {
        return eventRepository.getAll();
    }

    //inserisce un singolo evento nel repository localmente
    public void insertEvent(Event event) {
        eventRepository.insertEvent(event);
    }

    //togli l'evento dalla lista preferiti
    public void unsetFavorite(String eventId) {
        eventRepository.unsetFavorite(eventId);
    }

    //ottiene eventi basandosi su posizione, raggio, unità e ultimo aggiornamento
    public MutableLiveData<Result> getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        if (eventsListLiveData == null) {
            fetchEventsLocation(latlong, radius, unit, locale, lastUpdate);
        }
        return eventsListLiveData;
    }

    //cerca eventi per keyword
    public LiveData<Result> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword);
    }

    //liveData per ottenere eventi preferiti
    public MutableLiveData<Result> getPreferedEventsLiveData() {
        // Aggiorna la LiveData da repository ogni volta che viene chiamato
        preferedEventsListLiveData = eventRepository.getPreferedEvents();
        return preferedEventsListLiveData;
    }

    public void updateEvent2() {
        eventRepository.getPreferedEvents();
    }

    //aggiorna un evento nel repository
    public void updateEvent(Event event) {
        eventRepository.updateEvent(event);
    }

    private void fetchEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        eventsListLiveData = eventRepository.fetchEventsLocation(latlong, radius, unit, locale, lastUpdate);
    }

    private void getPreferedEvents() {
        preferedEventsListLiveData = eventRepository.getPreferedEvents();
    }

    //rimuove un evento dai preferiti
    public void removeFromFavorite(Event event) {
        eventRepository.updateEvent(event);
    }

    //elimina tutti gli eventi preferiti
    public void deleteAllFavoriteEvents() {
        eventRepository.deleteFavoriteEvents();
    }

    //aggiorna la lista di eventi preferiti
    public void refreshPreferedEvents() {
        eventRepository.refreshPreferedEvents();
    }
}
