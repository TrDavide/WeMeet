package com.temptationjavaisland.wemeet.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

@Dao
public interface EventDAO {
    @Query("SELECT * FROM Event")
    List<Event> getAll();

    @Insert
    void insertAll(Event... users);

    @Delete
    void delete(Event user);

    @Query("DELETE from Event")
    void deleteAll();

    @Update
    void updateArticle(Event event);


}
