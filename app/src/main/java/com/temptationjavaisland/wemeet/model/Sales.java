package com.temptationjavaisland.wemeet.model;

public class Sales {
    private Public _public;

    // Getter e Setter
    public Public getPublic() {
        return _public;
    }

    public void setPublic(Public _public) {
        this._public = _public;
    }

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
