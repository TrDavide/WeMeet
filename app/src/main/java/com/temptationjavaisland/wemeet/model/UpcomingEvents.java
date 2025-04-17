package com.temptationjavaisland.wemeet.model;

public class UpcomingEvents {

    private int mfxIt;
    private int total;
    private int filtered;

    public UpcomingEvents (){}


    public int getMfxIt() {
        return mfxIt;
    }

    public void setMfxIt(int mfxIt) {
        this.mfxIt = mfxIt;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFiltered() {
        return filtered;
    }

    public void setFiltered(int filtered) {
        this.filtered = filtered;
    }
}
