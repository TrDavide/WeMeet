package com.temptationjavaisland.wemeet.source.Event;

import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;
import com.temptationjavaisland.wemeet.util.Constants;

import java.io.IOException;

public class EventMockDataSource extends BaseEventRemoteDataSource {

    private final JSONParserUtils jsonParserUtils;

    public EventMockDataSource(JSONParserUtils jsonParserUtils) {
        this.jsonParserUtils = jsonParserUtils;
    }

    @Override
    public void getEventsLocation(String latlong, int radius, String unit, String locale, long lastUpdate) {
        EventAPIResponse eventApiResponse = null;

        try {
            eventApiResponse = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (eventApiResponse != null) {
            eventCallback.onSuccessFromRemote(eventApiResponse, System.currentTimeMillis());
        } else {
            eventCallback.onFailureFromRemote(new Exception("Errore nel parsing del file JSON"));
        }
    }

    @Override
    public void searchEvents(String keyword) {
    }

}