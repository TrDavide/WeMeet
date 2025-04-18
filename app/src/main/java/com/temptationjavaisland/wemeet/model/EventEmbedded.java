package com.temptationjavaisland.wemeet.model;

import java.util.List;

public class EventEmbedded {

    private List<Venue> venues;
    private List<Attraction> attractions;

    public EventEmbedded (){}

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenue(List<Venue> venues) {
        this.venues = venues;
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }
}
