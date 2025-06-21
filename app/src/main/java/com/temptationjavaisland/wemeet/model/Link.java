package com.temptationjavaisland.wemeet.model;

import androidx.room.DatabaseView;
import androidx.room.Entity;

public class Link {
    private String href;

    // Getter e Setter
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}