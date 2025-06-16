package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;

@Entity
public class Venue implements Parcelable {

    private String name;
    private String type;
    private String id;
    private boolean test;
    private String locale;
    private String postalCode;

    @Embedded(prefix = "city_")
    private City city;

    @Embedded(prefix = "location_")
    private Location location;

    public Venue() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isTest() { return test; }
    public void setTest(boolean test) { this.test = test; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    protected Venue(Parcel in) {
        name = in.readString();
        type = in.readString();
        id = in.readString();
        test = in.readByte() != 0;
        locale = in.readString();
        postalCode = in.readString();
        city = in.readParcelable(City.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(id);
        dest.writeByte((byte) (test ? 1 : 0));
        dest.writeString(locale);
        dest.writeString(postalCode);
        dest.writeParcelable(city, flags);
        dest.writeParcelable(location, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    public static class City implements Parcelable {
        private String name;

        public City() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        protected City(Parcel in) {
            name = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<City> CREATOR = new Creator<City>() {
            @Override
            public City createFromParcel(Parcel in) {
                return new City(in);
            }

            @Override
            public City[] newArray(int size) {
                return new City[size];
            }
        };
    }

    public static class Location implements Parcelable {
        private String latitude;
        private String longitude;

        public Location() {}

        public String getLatitude() { return latitude; }
        public void setLatitude(String latitude) { this.latitude = latitude; }

        public String getLongitude() { return longitude; }
        public void setLongitude(String longitude) { this.longitude = longitude; }

        protected Location(Parcel in) {
            latitude = in.readString();
            longitude = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(latitude);
            dest.writeString(longitude);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Location> CREATOR = new Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel in) {
                return new Location(in);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };
    }
}
