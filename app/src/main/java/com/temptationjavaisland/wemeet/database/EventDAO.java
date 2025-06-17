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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Event... users);

    @Delete
    void delete(Event user);

    @Query("DELETE from Event")
    void deleteAll();

    @Query("SELECT * FROM Event WHERE saved = 1")
    List<Event> isSaved();

    @Update
    void updateEvent(Event event);

    @Query("DELETE FROM event WHERE uid = :eventId")
    void deleteById(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertEventsList(List<Event> newsList);

    @Query("SELECT * FROM event WHERE saved = 1")
    List<Event> getAllSavedEvents();

    @Query("DELETE FROM Event WHERE saved = 1") //Per elimina preferiti
    void deleteAllSavedEvents();

    //@Query("SELECT EXISTS(SELECT 1 FROM Event WHERE uid = :eventId AND saved = 1)")
    //boolean isEventSaved(int eventId);


}
