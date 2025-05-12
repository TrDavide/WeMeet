package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.List;
@Entity
public class Links {
    @Embedded(prefix = "self_")
    private Link self;
    @Ignore
    private List<Link> attractions;
    @Ignore
    private List<Link> venues;

    public Links() {}

    // Getter e Setter
    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }

    public List<Link> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Link> attractions) {
        this.attractions = attractions;
    }

    public List<Link> getVenues() {
        return venues;
    }

    public void setVenues(List<Link> venues) {
        this.venues = venues;
    }
}
