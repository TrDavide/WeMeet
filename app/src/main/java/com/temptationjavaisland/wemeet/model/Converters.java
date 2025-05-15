package com.temptationjavaisland.wemeet.model;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public String fromClassificationList(List<Classification> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public List<Classification> toClassificationList(String json) {
        Type listType = new TypeToken<List<Classification>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    @TypeConverter
    public static String fromImageList(List<Image> images) {
        return new Gson().toJson(images);
    }

    @TypeConverter
    public static List<Image> toImageList(String json) {
        Type type = new TypeToken<List<Image>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static Dates fromStringToDates(String value) {
        return gson.fromJson(value, Dates.class);
    }

    @TypeConverter
    public static String fromDatesToString(Dates dates) {
        return gson.toJson(dates);
    }

    @TypeConverter
    public static EmbeddedEvent fromStringToEmbedded(String value) {
        return gson.fromJson(value, EmbeddedEvent.class);
    }

    @TypeConverter
    public static String fromEmbeddedToString(EmbeddedEvent embedded) {
        return gson.toJson(embedded);
    }

    @TypeConverter
    public static String fromStart(Dates.Start start) {
        return gson.toJson(start);
    }

    @TypeConverter
    public static Dates.Start toStart(String data) {
        return gson.fromJson(data, Dates.Start.class);
    }

    @TypeConverter
    public static String fromVenueList(List<Venue> venues) {
        return gson.toJson(venues);
    }

    @TypeConverter
    public static List<Venue> toVenueList(String data) {
        Type listType = new TypeToken<List<Venue>>() {}.getType();
        return gson.fromJson(data, listType);
    }

}


