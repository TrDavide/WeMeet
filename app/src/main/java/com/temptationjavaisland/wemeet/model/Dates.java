package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.TypeConverters;

@Entity
public class Dates {
    @Embedded(prefix = "start_")
    @TypeConverters(Converters.class)
    private Start start;
    private String timezone;
    //@Embedded(prefix = "status_")
    //private Status status;
    private boolean spanMultipleDays;


    public Dates() {}

    // Getter e Setter
    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /*public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }*/

    public boolean isSpanMultipleDays() {
        return spanMultipleDays;
    }

    public void setSpanMultipleDays(boolean spanMultipleDays) {
        this.spanMultipleDays = spanMultipleDays;
    }


    public static class Start {
        private String localDate;
        private String localTime;
        private String dateTime;
        private boolean dateTBD;
        private boolean dateTBA;
        private boolean timeTBA;
        private boolean noSpecificTime;

        public Start() {}

        // Getter e Setter
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
/*
    public static class Status {
        private String code;


        public Status(String code) {
            this.code = code;
        }

        // Getter e Setter
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }*/
}
