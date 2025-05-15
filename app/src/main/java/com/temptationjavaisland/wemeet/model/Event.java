package com.temptationjavaisland.wemeet.model;
import com.temptationjavaisland.wemeet.model.Image;
import java.util.List;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    private String name;
    //private String type;
    //private String id;
    //private boolean test;
    //private String url;
    //private String locale;
    private boolean saved;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }


    //@Relation(parentColumn = "uid", entityColumn = "eventid")
    //private transient List<Image> images;

    //@Embedded(prefix = "sales_")
    //private Sales sales;

    @Embedded(prefix = "dates_")
    @TypeConverters(Converters.class)
    private Dates dates;

    //@Relation(parentColumn = "uid", entityColumn = "eventId")
    //private List<Classification> classifications;

    //@Embedded(prefix = "_links_")
    //private Links _links;

    @Embedded(prefix = "_embedded_")
    @TypeConverters(Converters.class)
    private EmbeddedEvent _embedded;

    public Event() {}

    // Getter e Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String getType() {
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }*/

    public Dates getDates() {
        return dates;
    }

    public void setDates(Dates dates) {
        this.dates = dates;
    }

    /*public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public Links getLinks() {
        return _links;
    }

    public void setLinks(Links _links) {
        this._links = _links;
    }*/

    public EmbeddedEvent getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(EmbeddedEvent _embedded) {
        this._embedded = _embedded;
    }
}
