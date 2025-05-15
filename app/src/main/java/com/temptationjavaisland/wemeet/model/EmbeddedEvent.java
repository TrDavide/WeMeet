package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class EmbeddedEvent {
    @Ignore
    @Embedded(prefix = "venues_")
    @TypeConverters(Converters.class)
    private List<Venue> venues;
    //@Ignore
    //private List<Attraction> attractions;

    public EmbeddedEvent() {}

    // Getter e Setter
    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    /*public List<Attraction> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }*/
}
