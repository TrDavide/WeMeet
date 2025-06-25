package com.temptationjavaisland.wemeet.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventAPIResponse {

    @SerializedName("_embedded")
    @androidx.room.Embedded(prefix = "_embedded_")
    private Embedded _embedded;

    public EventAPIResponse() {}

    public Embedded getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(Embedded _embedded) {
        this._embedded = _embedded;
    }

    public static class Embedded {

        @SerializedName("events")
        private List<Event> events;

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }
    }
}
