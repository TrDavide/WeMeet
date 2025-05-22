package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity
public class Venue implements Parcelable {
    private String name;
    private String type;
    private String id;
    private boolean test;
    private String locale;
    private String postalCode;

    /*@Embedded(prefix = "city_")
    private City city;
    @Embedded(prefix = "country_")
    private Country country;
    @Embedded(prefix = "location_")
    private Location location;
    @Embedded(prefix = "upcomingEvents_")
    private UpcomingEvents upcomingEvents;
    @Embedded(prefix = "_links_")
    private Links _links;*/

    public Venue() {}

    // Getter e Setter
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

    /*
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public UpcomingEvents getUpcomingEvents() { return upcomingEvents; }
    public void setUpcomingEvents(UpcomingEvents upcomingEvents) { this.upcomingEvents = upcomingEvents; }

    public Links getLinks() { return _links; }
    public void setLinks(Links _links) { this._links = _links; }
    */

    // Parcelable implementation

    protected Venue(Parcel in) {
        name = in.readString();
        type = in.readString();
        id = in.readString();
        test = in.readByte() != 0;
        locale = in.readString();
        postalCode = in.readString();

        // Per ora non si leggono i campi embedded/commentati
        // city = in.readParcelable(City.class.getClassLoader());
        // country = in.readParcelable(Country.class.getClassLoader());
        // location = in.readParcelable(Location.class.getClassLoader());
        // upcomingEvents = in.readParcelable(UpcomingEvents.class.getClassLoader());
        // _links = in.readParcelable(Links.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(id);
        dest.writeByte((byte) (test ? 1 : 0));
        dest.writeString(locale);
        dest.writeString(postalCode);

        // Per ora non si scrivono i campi embedded/commentati
        // dest.writeParcelable(city, flags);
        // dest.writeParcelable(country, flags);
        // dest.writeParcelable(location, flags);
        // dest.writeParcelable(upcomingEvents, flags);
        // dest.writeParcelable(_links, flags);
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


    /*
    // Esempio di classi embedded (per implementare anche loro Parcelable, se necessario)
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
    */
}
