package com.temptationjavaisland.wemeet.repository;

import com.temptationjavaisland.wemeet.model.Event;

public interface IEventRepository {

    void fetchEvents(String country, String city, String keyword, int page, long lastUpdate);

    void fetchEventsByLocation(String latlong, int radius, long lastUpdate);

    void updateEvents(Event event);

    void getFavoriteEvents();

    void deleteFavoriteEvents();

}
