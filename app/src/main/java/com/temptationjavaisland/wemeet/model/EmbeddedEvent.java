package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class EmbeddedEvent implements Parcelable {

    @Ignore
    @Embedded(prefix = "venues_")
    @TypeConverters(Converters.class)
    private List<Venue> venues;

    @Ignore
    private List<Attraction> attractions;

    public EmbeddedEvent() {}

    // Getter e Setter
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
        // attractions = in.createTypedArrayList(Attraction.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(venues);
        // dest.writeTypedList(attractions);
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
