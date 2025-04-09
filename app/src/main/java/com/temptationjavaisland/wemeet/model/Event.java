package com.temptationjavaisland.wemeet.model;

import java.util.Objects;

public class Event {


    private String id;
    private String name;
    private String description;
    private String url;
    private String startTime;
    private String endTime;
    private String imageUrl;
    private String venue;
    private Long startTimeMillis;
    private boolean liked;

    public Event() {}

    // Getter & Setter

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

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

    public String getVenue() { return venue; }

    public void setVenue(String venue) { this.venue = venue; }

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
                Objects.equals(description, event.description) &&
                Objects.equals(url, event.url) &&
                Objects.equals(startTime, event.startTime) &&
                Objects.equals(endTime, event.endTime) &&
                Objects.equals(imageUrl, event.imageUrl) &&
                Objects.equals(venue, event.venue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url, startTime, endTime, imageUrl, venue);
    }
}
