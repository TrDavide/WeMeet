package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.List;

@Entity
public class EventEmbedded {
    @Ignore
    private List<Venue> venues;
    @Ignore
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
