package com.temptationjavaisland.wemeet.source;
import com.temptationjavaisland.wemeet.database.EventDAO;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import java.util.List;

/**
 * Class to manage local data source for events using Room.
 */
public class EventLocalDataSource extends BaseEventLocalDataSource {

    private final EventDAO eventDAO;

    public EventLocalDataSource(EventRoomDatabase eventRoomDatabase) {
        this.eventDAO = eventRoomDatabase.eventsDao();
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
            List<Event> allEvents = eventDAO.getAll();

            if (eventList != null) {
                for (Event event : allEvents) {
                    if (eventList.contains(event)) {
                        eventList.set(eventList.indexOf(event), event);
                    }
                }
                List<Long> insertedEventsIds = eventDAO.insertEventsList(eventList);
                for (int i = 0; i < eventList.size(); i++) {
                    eventList.get(i).setUid(insertedEventsIds.get(i).intValue());
                }
                eventCallback.onSuccessFromLocal(eventList);
            }
        });
    }
}
