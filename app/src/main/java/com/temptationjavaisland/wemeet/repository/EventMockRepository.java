package com.temptationjavaisland.wemeet.repository;

import com.temptationjavaisland.wemeet.model.Event;

public class EventMockRepository implements IEventRepository{


    @Override
    public void fetchEvents(String country, String city, String keyword, int page, long lastUpdate) {

    }

    @Override
    public void fetchEventsByLocation(String latlong, int radius, long lastUpdate) {

    }

    @Override
    public void updateEvents(Event event) {

    }

    @Override
    public void getFavoriteEvents() {

    }

    @Override
    public void deleteFavoriteEvents() {

    }
}
