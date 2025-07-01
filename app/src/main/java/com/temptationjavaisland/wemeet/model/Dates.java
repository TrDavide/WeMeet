package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.google.android.gms.common.api.Status;

@Entity
public class Dates implements Parcelable {

    @Embedded(prefix = "start_")
    @TypeConverters(Converters.class)
    private Start start;
    private String timezone;
    @Embedded(prefix = "status_")
    private Status status;
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

    protected Dates(Parcel in) {
        start = in.readParcelable(Start.class.getClassLoader());
        timezone = in.readString();
        spanMultipleDays = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(start, flags);
        dest.writeString(timezone);
        dest.writeByte((byte) (spanMultipleDays ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Dates> CREATOR = new Creator<Dates>() {
        @Override
        public Dates createFromParcel(Parcel in) {
            return new Dates(in);
        }

        @Override
        public Dates[] newArray(int size) {
            return new Dates[size];
        }
    };

    public static class Start implements Parcelable {
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

        protected Start(Parcel in) {
            localDate = in.readString();
            localTime = in.readString();
            dateTime = in.readString();
            dateTBD = in.readByte() != 0;
            dateTBA = in.readByte() != 0;
            timeTBA = in.readByte() != 0;
            noSpecificTime = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(localDate);
            dest.writeString(localTime);
            dest.writeString(dateTime);
            dest.writeByte((byte) (dateTBD ? 1 : 0));
            dest.writeByte((byte) (dateTBA ? 1 : 0));
            dest.writeByte((byte) (timeTBA ? 1 : 0));
            dest.writeByte((byte) (noSpecificTime ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Start> CREATOR = new Creator<Start>() {
            @Override
            public Start createFromParcel(Parcel in) {
                return new Start(in);
            }

            @Override
            public Start[] newArray(int size) {
                return new Start[size];
            }
        };
    }


    public static class Status {
        private String code;

        public Status(String code) {
            this.code = code;
        }

        public Status() {
        }

        // Getter e Setter
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

}
