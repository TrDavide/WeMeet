package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Entity;

@Entity
public class Sales {
    @Embedded(prefix = "_public_")
    private Public _public;

    public Sales(Public _public) {
        this._public = _public;
    }

    // Getter e Setter
    public Public getPublic() {
        return _public;
    }

    public void setPublic(Public _public) {
        this._public = _public;
    }

    @Entity
    public static class Public {
        private String startDateTime;
        private boolean startTBD;
        private boolean startTBA;
        private String endDateTime;

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
