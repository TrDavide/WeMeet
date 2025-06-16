package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Classification implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int eventId;

    private boolean primary;
    @Embedded(prefix = "segment_")
    private Segment segment;
    @Embedded(prefix = "genre_")
    private Genre genre;
    @Embedded(prefix = "subGenre_")
    private SubGenre subGenre;
    @Embedded(prefix = "type_")
    private Type type;
    @Embedded(prefix = "subType_")
    private SubType subType;
    private boolean family;

    public Classification() {}

    protected Classification(Parcel in) {
        id = in.readInt();
        eventId = in.readInt();
        primary = in.readByte() != 0;
        segment = in.readParcelable(Segment.class.getClassLoader());
        genre = in.readParcelable(Genre.class.getClassLoader());
        subGenre = in.readParcelable(SubGenre.class.getClassLoader());
        type = in.readParcelable(Type.class.getClassLoader());
        subType = in.readParcelable(SubType.class.getClassLoader());
        family = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(eventId);
        dest.writeByte((byte) (primary ? 1 : 0));
        dest.writeParcelable(segment, flags);
        dest.writeParcelable(genre, flags);
        dest.writeParcelable(subGenre, flags);
        dest.writeParcelable(type, flags);
        dest.writeParcelable(subType, flags);
        dest.writeByte((byte) (family ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Classification> CREATOR = new Creator<Classification>() {
        @Override
        public Classification createFromParcel(Parcel in) {
            return new Classification(in);
        }

        @Override
        public Classification[] newArray(int size) {
            return new Classification[size];
        }
    };

    // Getter e Setter
    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public SubGenre getSubGenre() {
        return subGenre;
    }

    public void setSubGenre(SubGenre subGenre) {
        this.subGenre = subGenre;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SubType getSubType() {
        return subType;
    }

    public void setSubType(SubType subType) {
        this.subType = subType;
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }

    // 🔽 Classi statiche Parcelable 🔽

    public static class Segment implements Parcelable {
        private String id;
        private String name;

        public Segment() {}

        protected Segment(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        public static final Creator<Segment> CREATOR = new Creator<Segment>() {
            @Override
            public Segment createFromParcel(Parcel in) {
                return new Segment(in);
            }

            @Override
            public Segment[] newArray(int size) {
                return new Segment[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getter e Setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Genre implements Parcelable {
        private String id;
        private String name;

        public Genre() {}

        protected Genre(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        public static final Creator<Genre> CREATOR = new Creator<Genre>() {
            @Override
            public Genre createFromParcel(Parcel in) {
                return new Genre(in);
            }

            @Override
            public Genre[] newArray(int size) {
                return new Genre[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }
    }

    public static class SubGenre implements Parcelable {
        private String id;
        private String name;

        public SubGenre() {}

        protected SubGenre(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        public static final Creator<SubGenre> CREATOR = new Creator<SubGenre>() {
            @Override
            public SubGenre createFromParcel(Parcel in) {
                return new SubGenre(in);
            }

            @Override
            public SubGenre[] newArray(int size) {
                return new SubGenre[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }
    }

    public static class Type implements Parcelable {
        private String id;
        private String name;

        public Type() {}

        protected Type(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        public static final Creator<Type> CREATOR = new Creator<Type>() {
            @Override
            public Type createFromParcel(Parcel in) {
                return new Type(in);
            }

            @Override
            public Type[] newArray(int size) {
                return new Type[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }
    }

    public static class SubType implements Parcelable {
        private String id;
        private String name;

        public SubType() {}

        protected SubType(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        public static final Creator<SubType> CREATOR = new Creator<SubType>() {
            @Override
            public SubType createFromParcel(Parcel in) {
                return new SubType(in);
            }

            @Override
            public SubType[] newArray(int size) {
                return new SubType[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }
    }
}
