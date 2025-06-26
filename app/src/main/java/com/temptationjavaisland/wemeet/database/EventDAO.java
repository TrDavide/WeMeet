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

    @Query("SELECT * FROM event WHERE saved = 1")
    List<Event> getSaved();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Event> events);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertEventsList(List<Event> newsList);

    @Query("SELECT * FROM Event WHERE id = :remoteId LIMIT 1")
    Event findByRemoteId(String remoteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEvent(Event event);

    @Delete
    void delete(Event user);

    @Query("DELETE FROM Event")
    void deleteAll();

    @Query("DELETE FROM event WHERE uid = :eventId")
    void deleteById(int eventId);

    @Query("DELETE FROM Event WHERE saved = 1") //Per elimina preferiti
    void deleteAllSavedEvents();

    @Update
    int updateEvent(Event event);

    @Update
    int updateListPreferedEvent(List<Event> events);

    @Query("SELECT * FROM Event WHERE saved = 1")
    List<Event> isSaved();


}
