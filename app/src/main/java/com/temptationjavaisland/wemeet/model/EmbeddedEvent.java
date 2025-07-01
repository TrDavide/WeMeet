package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
@TypeConverters(Converters.class)
public class EmbeddedEvent implements Parcelable {

    private List<Venue> venues;
    private List<Attraction> attractions;

    public EmbeddedEvent() {}

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }

    protected EmbeddedEvent(Parcel in) {
        venues = in.createTypedArrayList(Venue.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(venues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EmbeddedEvent> CREATOR = new Creator<EmbeddedEvent>() {
        @Override
        public EmbeddedEvent createFromParcel(Parcel in) {
            return new EmbeddedEvent(in);
        }

        @Override
        public EmbeddedEvent[] newArray(int size) {
            return new EmbeddedEvent[size];
        }
    };
}