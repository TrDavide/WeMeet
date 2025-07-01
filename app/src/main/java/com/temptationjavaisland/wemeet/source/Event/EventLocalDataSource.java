package com.temptationjavaisland.wemeet.source.Event;

import androidx.lifecycle.LiveData;

import com.temptationjavaisland.wemeet.database.EventDAO;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

public class EventLocalDataSource extends BaseEventLocalDataSource {

    private final EventDAO eventDAO;

    public EventLocalDataSource(EventRoomDatabase eventRoomDatabase) {
        this.eventDAO = eventRoomDatabase.eventsDao();
    }

    //elimina tutti gli eventi salvati nel database
    public void clearAllEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDAO.deleteAll();
        });
    }

    //restituisce una LiveData con la lista degli eventi salvati permettendo di osservare i cambiamenti in tempo reale.
    public LiveData<List<Event>> getSavedEventsLiveData() {
        return eventDAO.getSavedEventsLiveData();
    }


    //inserisce un nuovo evento o aggiorna uno esistente.
    public void insertOrUpdateEvent(Event event) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (event.getUid() == 0) { //se l'evento non ha UID, viene inserito e l'UID aggiornato.
                long newId = eventDAO.insertEventsList(List.of(event)).get(0);
                event.setUid((int) newId);
            } else {
                eventDAO.updateEvent(event); //altrimenti viene fatto un update
            }
        });
    }


    //restituisce tutti gli eventi presenti nel database locale
    public List<Event> getAll() {
        return eventDAO.getAll();
    }


    //segna un evento come non preferito rimuovendolo dai preferiti
    public void unsetFavorite(String eventId) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDAO.unsetFavorite(eventId);
        });
    }


    //recupera tutti gli eventi dal database e notifica tramite callback
    @Override
    public void getEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventCallback.onSuccessFromLocal(eventDAO.getAll());
        });
    }

    //recupera gli eventi preferiti dal database e notifica tramite callback
    @Override
    public void getPreferedEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> preferedEvents = eventDAO.getSaved();
            eventCallback.onFavoriteStatusChanged(preferedEvents);
        });
    }


    //aggiorna un evento nel database e notifica
    @Override
    public void updateEvent(Event event) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter  = eventDAO.updateEvent(event);
            if (rowUpdatedCounter == 1) {  //se l'aggiornamento va a buon fine, ritorna l'evento aggiornato e la lista preferiti.
                Event updatedEvent = eventDAO.getEvent(event.getUid());
                eventCallback.onFavoriteStatusChanged(updatedEvent, eventDAO.getSaved());
            } else {
                eventCallback.onFailureFromLocal(new Exception("Unexpected error during update.")); //Altrimenti segnala errore
            }
        });
    }


    //rimuove tutti gli eventi preferiti resettando lo stato saved aggiorna il database e notifica successo o errore tramite callback
    @Override
    public void deletePreferedEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> favoriteEvents = eventDAO.getSaved();
            for (Event event : favoriteEvents) {
                event.setSaved(false);
            }
            int updatedRowsNumber  = eventDAO.updateListPreferedEvent(favoriteEvents);
            if (updatedRowsNumber  == favoriteEvents.size()) {
                eventCallback.onDeleteFavoriteSuccess(favoriteEvents);
            } else {
                eventCallback.onFailureFromLocal(new Exception("Error deleting favorite events."));
            }
        });
    }


    //inserisce o aggiorna una lista di eventi
    @Override
    public void insertEvents(List<Event> eventList) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (eventList != null) {
                for (int i = 0; i < eventList.size(); i++) {
                    Event newEvent = eventList.get(i);

                    // Cerca evento esistente con lo stesso ID remoto
                    Event existingEvent = eventDAO.findByRemoteId(newEvent.getId());

                    if (existingEvent != null) {
                        // Evento già presente, aggiorna con stesso UID
                        newEvent.setUid(existingEvent.getUid());
                        eventDAO.updateEvent(newEvent);
                        eventList.set(i, newEvent);
                    } else {
                        // Evento nuovo, inserisce e assegna UID
                        long insertedId = eventDAO.insertEvent(newEvent);
                        newEvent.setUid((int) insertedId);
                        eventList.set(i, newEvent);
                    }
                }

                // Notifica la lista aggiornata o inserita tramite callback
                eventCallback.onSuccessFromLocal(eventList);
            }
        });
    }
}
