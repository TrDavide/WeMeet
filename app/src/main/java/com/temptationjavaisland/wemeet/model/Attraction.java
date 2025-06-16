package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.List;
public class Attraction {
    private String name;
    private String type;
    private String id;
    private boolean test;
    private String url;
    private String locale;
    private ExternalLinks externalLinks;
    @Ignore
    private List<Image> images;
    @Ignore
    private List<Classification> classifications;
    //private Venue.UpcomingEvents upcomingEvents;
    private Links _links;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public ExternalLinks getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(ExternalLinks externalLinks) {
        this.externalLinks = externalLinks;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    /*public Venue.UpcomingEvents getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(Venue.UpcomingEvents upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }*/

    public Links getLinks() {
        return _links;
    }

    public void setLinks(Links _links) {
        this._links = _links;
    }



    public static class ExternalLinks {
        @Ignore
        private List<ExternalLink> youtube;
        @Ignore
        private List<ExternalLink> twitter;
        @Ignore
        private List<ExternalLink> itunes;
        @Ignore
        private List<ExternalLink> lastfm;
        @Ignore
        private List<ExternalLink> spotify;
        @Ignore
        private List<ExternalLink> wiki;
        @Ignore
        private List<ExternalLink> facebook;
        @Ignore
        private List<MusicBrainzLink> musicbrainz;
        @Ignore
        private List<ExternalLink> instagram;
        @Ignore
        private List<ExternalLink> homepage;
        @Ignore

        // Getter e Setter
        public List<ExternalLink> getYoutube() {
            return youtube;
        }

        public void setYoutube(List<ExternalLink> youtube) {
            this.youtube = youtube;
        }

        public List<ExternalLink> getTwitter() {
            return twitter;
        }

        public void setTwitter(List<ExternalLink> twitter) {
            this.twitter = twitter;
        }

        public List<ExternalLink> getItunes() {
            return itunes;
        }

        public void setItunes(List<ExternalLink> itunes) {
            this.itunes = itunes;
        }

        public List<ExternalLink> getLastfm() {
            return lastfm;
        }

        public void setLastfm(List<ExternalLink> lastfm) {
            this.lastfm = lastfm;
        }

        public List<ExternalLink> getSpotify() {
            return spotify;
        }

        public void setSpotify(List<ExternalLink> spotify) {
            this.spotify = spotify;
        }

        public List<ExternalLink> getWiki() {
            return wiki;
        }

        public void setWiki(List<ExternalLink> wiki) {
            this.wiki = wiki;
        }

        public List<ExternalLink> getFacebook() {
            return facebook;
        }

        public void setFacebook(List<ExternalLink> facebook) {
            this.facebook = facebook;
        }

        public List<MusicBrainzLink> getMusicbrainz() {
            return musicbrainz;
        }

        public void setMusicbrainz(List<MusicBrainzLink> musicbrainz) {
            this.musicbrainz = musicbrainz;
        }

        public List<ExternalLink> getInstagram() {
            return instagram;
        }

        public void setInstagram(List<ExternalLink> instagram) {
            this.instagram = instagram;
        }

        public List<ExternalLink> getHomepage() {
            return homepage;
        }

        public void setHomepage(List<ExternalLink> homepage) {
            this.homepage = homepage;
        }
    }



    public static class ExternalLink {
        private String url;

        // Getter e Setter
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }



    public static class MusicBrainzLink {
        private String id;
        private String url;

        // Getter e Setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
