package com.temptationjavaisland.wemeet.model;
import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity
public class EventAPIResponse {

    @androidx.room.Embedded(prefix = "_embedded_")
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