package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.List;

public class Links implements Parcelable {
    @Embedded(prefix = "self_")
    private Link self;

    @Ignore
    private List<Link> attractions;

    @Ignore
    private List<Link> venues;

    public Links() {}

    protected Links(Parcel in) {
        self = in.readParcelable(Link.class.getClassLoader());
        attractions = in.createTypedArrayList(Link.CREATOR);
        venues = in.createTypedArrayList(Link.CREATOR);
    }

    public static final Creator<Links> CREATOR = new Creator<Links>() {
        @Override
        public Links createFromParcel(Parcel in) {
            return new Links(in);
        }

        @Override
        public Links[] newArray(int size) {
            return new Links[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(self, flags);
        dest.writeTypedList(attractions);
        dest.writeTypedList(venues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getter e Setter
    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }

    public List<Link> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Link> attractions) {
        this.attractions = attractions;
    }

    public List<Link> getVenues() {
        return venues;
    }

    public void setVenues(List<Link> venues) {
        this.venues = venues;
    }

    // Classe Link interna (oppure in un file separato se già esistente)
    public static class Link implements Parcelable {
        private String href;

        public Link() {}

        protected Link(Parcel in) {
            href = in.readString();
        }

        public static final Creator<Link> CREATOR = new Creator<Link>() {
            @Override
            public Link createFromParcel(Parcel in) {
                return new Link(in);
            }

            @Override
            public Link[] newArray(int size) {
                return new Link[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(href);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getter e Setter
        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }
}
