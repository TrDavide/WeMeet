package com.temptationjavaisland.wemeet.model;
import java.util.List;

public class EventAPIResponse {

    private List<Event> event;

    public EventAPIResponse(){}


    public List<Event> getEvent() {
        return event;
    }
    public void setArticles(List<Event> event) {
        this.event = event;
    }
}