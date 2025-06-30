package com.temptationjavaisland.wemeet.source.Event;
import com.temptationjavaisland.wemeet.database.EventDAO;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;

import org.apache.commons.digester.annotations.rules.SetTop;

import java.util.List;

/**
 * Class to manage local data source for events using Room.
 */
public class EventLocalDataSource extends BaseEventLocalDataSource {

    private final EventDAO eventDAO;

    public EventLocalDataSource(EventRoomDatabase eventRoomDatabase) {
        this.eventDAO = eventRoomDatabase.eventsDao();
    }

    public void clearAllEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDAO.deleteAll();
        });
    }

    public List<Event> ottieniEventisalvati() {
        /*EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDAO.getSaved();
        });*/
        return eventDAO.getSaved();
    }

    public void insertOrUpdateEvent(Event event) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (event.getUid() == 0) {
                long newId = eventDAO.insertEventsList(List.of(event)).get(0);
                event.setUid((int) newId);
            } else {
                eventDAO.updateEvent(event);
            }
        });
    }

    public void unsetFavorite(String eventId) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDAO.unsetFavorite(eventId);
        });
    }

    @Override
    public void getEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventCallback.onSuccessFromLocal(eventDAO.getAll());
        });
    }

    @Override
    public void getPreferedEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> preferedEvents = eventDAO.getSaved();
            eventCallback.onFavoriteStatusChanged(preferedEvents);
        });
    }

    @Override
    public void updateEvent(Event event) { 
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter  = eventDAO.updateEvent(event);
            if (rowUpdatedCounter == 1) {
                Event updatedEvent = eventDAO.getEvent(event.getUid());
                eventCallback.onFavoriteStatusChanged(updatedEvent, eventDAO.getSaved());
            } else {
                eventCallback.onFailureFromLocal(new Exception("Unexpected error during update."));
            }
        });
    }

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

    @Override
    public void insertEvents(List<Event> eventList) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (eventList != null) {
                for (int i = 0; i < eventList.size(); i++) {
                    Event newEvent = eventList.get(i);

                    // Cerca se un evento con stesso ID remoto esiste già
                    Event existingEvent = eventDAO.findByRemoteId(newEvent.getId());

                    if (existingEvent != null) {
                        // Esiste già → aggiorna usando lo stesso UID
                        newEvent.setUid(existingEvent.getUid());
                        eventDAO.updateEvent(newEvent);
                        eventList.set(i, newEvent);
                    } else {
                        // Non esiste → inserisce
                        long insertedId = eventDAO.insertEvent(newEvent);
                        newEvent.setUid((int) insertedId);
                        eventList.set(i, newEvent);
                    }
                }

                eventCallback.onSuccessFromLocal(eventList);
            }
        });
    }
}
