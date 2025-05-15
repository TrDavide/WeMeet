package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.Map;

@Entity
public class Venue {
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

    // Getter e Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /*public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UpcomingEvents getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(UpcomingEvents upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public Links getLinks() {
        return _links;
    }

    public void setLinks(Links _links) {
        this._links = _links;
    }*/


    /*public static class City {
        private String name;

        // Getter e Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }*/


    /*public static class Country {
        private String name;
        private String countryCode;

        // Getter e Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }*/


    /*public static class Location {
        private String longitude;
        private String latitude;

        // Getter e Setter
        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
    }*/


    /*public static class UpcomingEvents {
        @Ignore
        private Map<String, Integer> events;
        private int _total;
        private int _filtered;

        // Getter e Setter
        public Map<String, Integer> getEvents() {
            return events;
        }

        public void setEvents(Map<String, Integer> events) {
            this.events = events;
        }

        public int getTotal() {
            return _total;
        }

        public void setTotal(int _total) {
            this._total = _total;
        }

        public int getFiltered() {
            return _filtered;
        }

        public void setFiltered(int _filtered) {
            this._filtered = _filtered;
        }
    }*/
}
