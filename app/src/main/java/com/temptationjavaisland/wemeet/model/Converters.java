package com.temptationjavaisland.wemeet.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Classe di converter personalizzati per Room.
 * Converte oggetti complessi in JSON (String) e viceversa,
 * per permetterne il salvataggio nel database.
 */
public class Converters {

    private static final Gson gson = new Gson();

    //Converte una lista di oggetti Image in una stringa JSON.
    @TypeConverter
    public static String fromImageList(List<Image> images) {
        return gson.toJson(images);
    }

    //Converte una stringa JSON in una lista di oggetti Image.
    @TypeConverter
    public static List<Image> toImageList(String json) {
        Type type = new TypeToken<List<Image>>() {}.getType();
        return gson.fromJson(json, type);
    }

    //Converte una lista di Classification in formato JSON.
    @TypeConverter
    public static String fromClassificationList(List<Classification> list) {
        return gson.toJson(list);
    }

    //Converte una stringa JSON in una lista di Classification.
    @TypeConverter
    public static List<Classification> toClassificationList(String json) {
        Type type = new TypeToken<List<Classification>>() {}.getType();
        return gson.fromJson(json, type);
    }

    //Converte una lista di Venue in formato JSON.
    @TypeConverter
    public static String fromVenueList(List<Venue> venues) {
        return gson.toJson(venues);
    }

    //Converte una stringa JSON in una lista di Venue.
    @TypeConverter
    public static List<Venue> toVenueList(String json) {
        Type type = new TypeToken<List<Venue>>() {}.getType();
        return gson.fromJson(json, type);
    }

    //Converte una lista di Attraction in formato JSON.
    @TypeConverter
    public static String fromAttractionList(List<Attraction> list) {
        return gson.toJson(list);
    }

    //Converte una stringa JSON in una lista di Attraction.
    @TypeConverter
    public static List<Attraction> toAttractionList(String json) {
        Type type = new TypeToken<List<Attraction>>() {}.getType();
        return gson.fromJson(json, type);
    }

    //Converte l'oggetto Dates in JSON.
    @TypeConverter
    public static String fromDates(Dates dates) {
        return gson.toJson(dates);
    }

    //Converte il JSON in un oggetto Dates.
    @TypeConverter
    public static Dates toDates(String json) {
        return gson.fromJson(json, Dates.class);
    }

    //Converte il JSON in un oggetto EmbeddedEvent.
    @TypeConverter
    public static EmbeddedEvent toEmbeddedEvent(String json) {
        return gson.fromJson(json, EmbeddedEvent.class);
    }
}
