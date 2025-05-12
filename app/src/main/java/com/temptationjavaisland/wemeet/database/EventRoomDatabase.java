package com.temptationjavaisland.wemeet.database;

import static com.temptationjavaisland.wemeet.util.Constants.DATABASE_VERSION;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.temptationjavaisland.wemeet.model.Converters;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.util.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@TypeConverters(Converters.class)
@Database(entities = {Event.class}, version = DATABASE_VERSION, exportSchema = true)
public abstract class EventRoomDatabase extends RoomDatabase {


    public abstract EventDAO eventsDao();

    private static volatile EventRoomDatabase INSTANCE;

    public static EventRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    EventRoomDatabase.class, Constants.SAVED_ARTICLES_DATABASE)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}