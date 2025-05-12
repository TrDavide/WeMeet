package com.temptationjavaisland.wemeet.model;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    @TypeConverter
    public String fromClassificationList(List<Classification> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public List<Classification> toClassificationList(String json) {
        Type listType = new TypeToken<List<Classification>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String fromImageList(List<Image> images) {
        return new Gson().toJson(images);
    }

    @TypeConverter
    public static List<Image> toImageList(String json) {
        Type type = new TypeToken<List<Image>>() {}.getType();
        return new Gson().fromJson(json, type);
    }


}


