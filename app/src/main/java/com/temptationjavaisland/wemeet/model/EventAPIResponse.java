package com.temptationjavaisland.wemeet.model;
import java.util.List;

public class EventAPIResponse {
    private String status;
    private int totalResults;
    private List<Event> event;

    public EventAPIResponse(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Event> getEvent() {
        return event;
    }
    public void setArticles(List<Event> event) {
        this.event = event;
    }
}