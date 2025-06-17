package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class Event implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String name;
    //private String type;
    //private String id;
    //private boolean test;
    //private String url;
   //private String locale;
    private boolean saved;

    @TypeConverters(Converters.class)
    private List<Image> images;

    /*@Embedded(prefix = "sales_")
    private Sales sales;*/

    @Embedded(prefix = "dates_")
    @TypeConverters(Converters.class)
    private Dates dates;

    //@TypeConverters(Converters.class)
    //private List<Classification> classifications;

    //@Embedded(prefix = "_links_")
    //private Links _links;

    @Embedded(prefix = "_embedded_")
    @TypeConverters(Converters.class)
    private EmbeddedEvent _embedded;

    public Event() {}

    // Getter e Setter
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
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
    }*/

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    /*public Sales getSales() {
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
    /*
     * Used to fill the shimmer list
     */
    public static Event getSampleArticle() {
        Event sample = new Event();
        sample.setName("Not so long title sample");
        return sample;
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        uid = in.readInt();
        name = in.readString();
        //type = in.readString();
        //id = in.readString();
        //test = in.readByte() != 0;
        //url = in.readString();
        //locale = in.readString();
        saved = in.readByte() != 0;
        images = in.createTypedArrayList(Image.CREATOR);
        //sales = in.readParcelable(Sales.class.getClassLoader());
        dates = in.readParcelable(Dates.class.getClassLoader());
        //classifications = in.createTypedArrayList(Classification.CREATOR);
        //_links = in.readParcelable(Links.class.getClassLoader());
        _embedded = in.readParcelable(EmbeddedEvent.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(name);
        //dest.writeString(type);
        //dest.writeString(id);
        //dest.writeByte((byte) (test ? 1 : 0));
        //dest.writeString(url);
        //dest.writeString(locale);
        dest.writeByte((byte) (saved ? 1 : 0));
        dest.writeTypedList(images);
        //dest.writeParcelable(sales, flags);
        dest.writeParcelable(dates, flags);
        //dest.writeTypedList(classifications);
        //dest.writeParcelable(_links, flags);
        dest.writeParcelable(_embedded, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
