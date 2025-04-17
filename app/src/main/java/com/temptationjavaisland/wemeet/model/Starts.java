package com.temptationjavaisland.wemeet.model;

public class Starts {

    private String localDate;
    private String localTime;
    private String dateTime;
    private boolean dateTBD;
    private boolean dateTBA;
    private boolean timeTBA;
    private boolean noSpecificTime;

    public Starts (){}


    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isDateTBD() {
        return dateTBD;
    }

    public void setDateTBD(boolean dateTBD) {
        this.dateTBD = dateTBD;
    }

    public boolean isDateTBA() {
        return dateTBA;
    }

    public void setDateTBA(boolean dateTBA) {
        this.dateTBA = dateTBA;
    }

    public boolean isTimeTBA() {
        return timeTBA;
    }

    public void setTimeTBA(boolean timeTBA) {
        this.timeTBA = timeTBA;
    }

    public boolean isNoSpecificTime() {
        return noSpecificTime;
    }

    public void setNoSpecificTime(boolean noSpecificTime) {
        this.noSpecificTime = noSpecificTime;
    }
}
