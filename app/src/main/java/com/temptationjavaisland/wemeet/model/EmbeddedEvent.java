package com.temptationjavaisland.wemeet.model;

import java.util.List;

public class EmbeddedEvent {
    private List<Venue> venues;
    private List<Attraction> attractions;

    // Getter e Setter
    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }
}
