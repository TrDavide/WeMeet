package com.temptationjavaisland.wemeet.model;
import java.util.List;

public class EventAPIResponse {
    private Embedded _embedded;

    // Getter e Setter
    public Embedded getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(Embedded _embedded) {
        this._embedded = _embedded;
    }



    public static class Embedded {
        private List<Event> events;

        // Getter e Setter
        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }

    }
}