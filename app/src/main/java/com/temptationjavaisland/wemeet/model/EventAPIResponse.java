package com.temptationjavaisland.wemeet.model;
import java.util.List;

public class EventAPIResponse {

    private List<Event> events;

    public EventAPIResponse(){}


    public List<Event> getEvents() {
        return events;
    }
    public void setArticles(List<Event> events) {
        this.events = events;
    }

    public String getTotalResults() {
        return "CIAOOOOOOO";
    }

}