package com.temptationjavaisland.wemeet.util;

public class Constants{

    public static final String SAMPLE_JSON_FILENAME = "sample_api_response.json";
    public static final int DATABASE_VERSION = 4;
    public static final String SAVED_ARTICLES_DATABASE= "saved db";

    public static final String TICKETMASTER_API_BASE_URL = "https://app.ticketmaster.com/discovery/v2/";
    public static final String TOP_HEADLINES_ENDPOINT = "events.json";
    public static final String TOP_HEADLINES_COUNTRY_PARAMETER = "countryCode";
    public static final String TOP_HEADLINES_CITY_PARAMETER = "city";
    public static final String TOP_HEADLINES_KEYWORD_PARAMETER = "keyword";
    public static final String TOP_HEADLINES_PAGE_SIZE_PARAMETER = "pageSize";
    public static final String TOP_HEADLINES_APIKEY = "apiKey";
    public static final int TOP_HEADLINES_PAGE_SIZE_VALUE = 100;
    public static final int FRESH_TIMEOUT = 1000 * 60; // 1 minute in milliseconds
    public static final String TOP_HEADLINES_LATLONG_PARAMETER = "latlong";
    public static final String TOP_HEADLINES_RADIUS_PARAMETER = "radius";
    public static final String TOP_HEADLINES_UNIT_PARAMETER = "unit";
    public static final String TOP_HEADLINES_LOCALE_PARAMETER = "locale";
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";
    public static final String FIREBASE_REALTIME_DATABASE = "https://wemeet-5fc51-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_FAVORITE_EVENTS_COLLECTION = "prefered_events";




}
