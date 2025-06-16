package com.temptationjavaisland.wemeet.util;

import com.temptationjavaisland.wemeet.model.Event;

import java.util.List;

public interface ResponseCallBack {

    void onSuccess(List<Event> eventsList, long lastUpdate);
    void onFailure(String errorMessage);
}