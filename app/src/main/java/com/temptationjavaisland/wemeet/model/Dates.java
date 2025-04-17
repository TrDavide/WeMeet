package com.temptationjavaisland.wemeet.model;

public class Dates {

    private Starts start;
    private String timezone;
    private Status status;
    private boolean spanMultipleDays;

    public Dates (){}

    public Starts getStart() {
        return start;
    }

    public void setStart(Starts start) {
        this.start = start;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isSpanMultipleDays() {
        return spanMultipleDays;
    }

    public void setSpanMultipleDays(boolean spanMultipleDays) {
        this.spanMultipleDays = spanMultipleDays;
    }
}
