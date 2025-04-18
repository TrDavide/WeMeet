package com.temptationjavaisland.wemeet.model;

import android.media.Image;

import java.util.List;

public class Event {

    private String name;
    private String type;
    private String id;
    private boolean test;
    private String url;
    private String locale;
    private List<Image> images;
    private Dates dates;
    private List<Classification> classifications;
    private EventEmbedded _embedded;


    public Event() {}

    // Getter & Setter

/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId()) &&
                Objects.equals(getName(), event.getName()) &&
                Objects.equals(getType(), event.getType()) &&
                Objects.equals(isTest(), event.isTest()) &&
                Objects.equals(getUrl(), event.getUrl()) &&
                Objects.equals(getLocale(), event.getLocale()) &&
                Objects.equals(getImages(), event.getImages()) &&
                Objects.equals(getSales(), event.getSales()) &&
                Objects.equals(getDates(), event.getDates())&&
                Objects.equals(getClassifications(), event.getClassifications());
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Dates getDates() {
        return dates;
    }

    public void setDates(Dates dates) {
        this.dates = dates;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public EventEmbedded getEventEmbedded() {
        return _embedded;
    }

    public void setEventEmbedded(EventEmbedded eventEmbedded) {
        this._embedded = eventEmbedded;
    }
}
