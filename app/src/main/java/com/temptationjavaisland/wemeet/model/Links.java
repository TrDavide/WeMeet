package com.temptationjavaisland.wemeet.model;

import java.util.List;

public class Links {
    private Link self;
    private List<Link> attractions;
    private List<Link> venues;

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
