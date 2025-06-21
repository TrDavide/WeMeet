package com.temptationjavaisland.wemeet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

public class Sales implements Parcelable {
    @Embedded(prefix = "_public_")
    private Public _public;

    public Sales(Public _public) {
        this._public = _public;
    }

    public Sales() {}

    protected Sales(Parcel in) {
        _public = in.readParcelable(Public.class.getClassLoader());
    }

    public static final Creator<Sales> CREATOR = new Creator<Sales>() {
        @Override
        public Sales createFromParcel(Parcel in) {
            return new Sales(in);
        }

        @Override
        public Sales[] newArray(int size) {
            return new Sales[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(_public, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getter e Setter
    public Public getPublic() {
        return _public;
    }

    public void setPublic(Public _public) {
        this._public = _public;
    }

    public static class Public implements Parcelable {
        private String startDateTime;
        private boolean startTBD;
        private boolean startTBA;
        private String endDateTime;

        public Public() {}

        protected Public(Parcel in) {
            startDateTime = in.readString();
            startTBD = in.readByte() != 0;
            startTBA = in.readByte() != 0;
            endDateTime = in.readString();
        }

        public static final Creator<Public> CREATOR = new Creator<Public>() {
            @Override
            public Public createFromParcel(Parcel in) {
                return new Public(in);
            }

            @Override
            public Public[] newArray(int size) {
                return new Public[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(startDateTime);
            dest.writeByte((byte) (startTBD ? 1 : 0));
            dest.writeByte((byte) (startTBA ? 1 : 0));
            dest.writeString(endDateTime);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getter e Setter
        public String getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(String startDateTime) {
            this.startDateTime = startDateTime;
        }

        public boolean isStartTBD() {
            return startTBD;
        }

        public void setStartTBD(boolean startTBD) {
            this.startTBD = startTBD;
        }

        public boolean isStartTBA() {
            return startTBA;
        }

        public void setStartTBA(boolean startTBA) {
            this.startTBA = startTBA;
        }

        public String getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(String endDateTime) {
            this.endDateTime = endDateTime;
        }
    }
}