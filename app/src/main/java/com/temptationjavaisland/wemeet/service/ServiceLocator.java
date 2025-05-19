package com.temptationjavaisland.wemeet.service;

import android.app.Application;

import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    public ServiceLocator() {
    }

    public static ServiceLocator getInstance(){
        if(INSTANCE == null){
            synchronized (ServiceLocator.class){
                if(INSTANCE == null){
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                return chain.proceed(request);
            })
            .build();

    public EventAPIService getEventAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TICKETMASTER_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build(); //nel caso controllare Gradle
        return retrofit.create(EventAPIService.class);
    }

    public EventRoomDatabase getEventsDB(Application application) {
        return EventRoomDatabase.getDatabase(application);
    }


}
