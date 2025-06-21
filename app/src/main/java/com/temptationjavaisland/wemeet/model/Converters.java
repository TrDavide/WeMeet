package com.temptationjavaisland.wemeet.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromImageList(List<Image> images) {
        return gson.toJson(images);
    }

    @TypeConverter
    public static List<Image> toImageList(String json) {
        Type type = new TypeToken<List<Image>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromClassificationList(List<Classification> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Classification> toClassificationList(String json) {
        Type type = new TypeToken<List<Classification>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromVenueList(List<Venue> venues) {
        return gson.toJson(venues);
    }

    @TypeConverter
    public static List<Venue> toVenueList(String json) {
        Type type = new TypeToken<List<Venue>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromAttractionList(List<Attraction> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Attraction> toAttractionList(String json) {
        Type type = new TypeToken<List<Attraction>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromDates(Dates dates) {
        return gson.toJson(dates);
    }

    @TypeConverter
    public static Dates toDates(String json) {
        return gson.fromJson(json, Dates.class);
    }

    @TypeConverter
    public static String fromEmbeddedEvent(EmbeddedEvent embedded) {
        return gson.toJson(embedded);
    }

    @TypeConverter
    public static EmbeddedEvent toEmbeddedEvent(String json) {
        return gson.fromJson(json, EmbeddedEvent.class);
    }
}