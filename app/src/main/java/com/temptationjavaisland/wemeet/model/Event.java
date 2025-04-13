package com.temptationjavaisland.wemeet.model;

import java.util.Objects;

public class Event {


    private String id;
    private String name;
    private String date;
    private String partecipant;
    private String url;
    private String startTime;
    private String endTime;
    private String imageUrl;
    private String location;
    private Long startTimeMillis;
    private boolean liked;

    public Event() {}

    // Getter & Setter

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getPartecipant() { return partecipant; }

    public void setPartecipant(String partecipant) { this.partecipant = partecipant; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getStartTime() { return startTime; }

    /*public void setStartTime(String startTime) {
        this.startTime = startTime;
        this.startTimeMillis = DateTimeUtil.getDateMillis(startTime);
    }*/

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }

    public void setLocation(String venue) { this.location = location; }

    public Long getStartTimeMillis() { return startTimeMillis; }

    public void setStartTimeMillis(Long startTimeMillis) {}

    public boolean getLiked() { return liked; }

    public void setLiked(boolean liked) { this.liked = liked; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(name, event.name) &&
                Objects.equals(date, event.date) &&
                Objects.equals(partecipant, event.partecipant) &&
                Objects.equals(url, event.url) &&
                Objects.equals(startTime, event.startTime) &&
                Objects.equals(endTime, event.endTime) &&
                Objects.equals(imageUrl, event.imageUrl) &&
                Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, partecipant, url, startTime, endTime, imageUrl, location);
    }
}
