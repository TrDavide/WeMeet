package com.temptationjavaisland.wemeet.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

@Dao
public interface EventDAO {
    @Query("SELECT * FROM Event")
    List<Event> getAll();

    @Query("SELECT * FROM Event WHERE uid = :eventId")
    Event getEvent(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Event... users);

    @Delete
    void delete(Event user);

    @Query("DELETE FROM Event")
    void deleteAll();

    @Query("SELECT * FROM Event WHERE saved = 1")
    List<Event> isSaved();

    @Update
    int updateEvent(Event event);

    @Query("DELETE FROM event WHERE uid = :eventId")
    void deleteById(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertEventsList(List<Event> newsList);

    @Query("SELECT * FROM event WHERE saved = 1")
    List<Event> getAllSavedEvents();

    @Query("DELETE FROM Event WHERE saved = 1") //Per elimina preferiti
    void deleteAllSavedEvents();
    @Update
    int updateEventList(List<Event> events);
}
