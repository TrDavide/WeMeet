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
        this.eventDAO = eventRoomDatabase.eventDao();
    }

    @Override
    public void getEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventCallback.onSuccessFromLocal(eventDAO.getAll());
        });
    }

    @Override
    public void getFavoriteEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> favoriteEvents = eventDAO.getSaved();
            eventCallback.onFavoriteStatusChanged(favoriteEvents);
        });
    }

    @Override
    public void updateEvent(Event event) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdated = eventDAO.updateEvent(event);
            if (rowUpdated == 1) {
                Event updatedEvent = eventDAO.getEvent(event.getUid());
                eventCallback.onFavoriteStatusChanged(updatedEvent, eventDAO.getSaved());
            } else {
                eventCallback.onFailureFromLocal(new Exception("Unexpected error during update."));
            }
        });
    }

    @Override
    public void deleteFavoriteEvents() {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> favoriteEvents = eventDAO.getSaved();
            for (Event event : favoriteEvents) {
                event.setSaved(false);
            }
            int updated = eventDAO.updateEventList(favoriteEvents);
            if (updated == favoriteEvents.size()) {
                eventCallback.onDeleteFavoriteSuccess(favoriteEvents);
            } else {
                eventCallback.onFailureFromLocal(new Exception("Error deleting favorite events."));
            }
        });
    }

    @Override
    public void insertEvents(List<Event> eventList) {
        EventRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Event> existingEvents = eventDAO.getAll();

            if (eventList != null) {
                for (Event event : existingEvents) {
                    if (eventList.contains(event)) {
                        eventList.set(eventList.indexOf(event), event);
                    }
                }
                List<Long> insertedIds = eventDAO.insertEventList(eventList);
                for (int i = 0; i < eventList.size(); i++) {
                    eventList.get(i).setUid(insertedIds.get(i).intValue());
                }
                eventCallback.onSuccessFromLocal(eventList);
            }
        });
    }
}
