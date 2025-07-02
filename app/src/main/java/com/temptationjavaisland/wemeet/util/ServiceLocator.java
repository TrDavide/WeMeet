package com.temptationjavaisland.wemeet.util;

import android.app.Application;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.repository.User.UserRepository;
import com.temptationjavaisland.wemeet.service.EventAPIService;
import com.temptationjavaisland.wemeet.source.Event.BaseEventLocalDataSource;
import com.temptationjavaisland.wemeet.source.Event.BaseEventRemoteDataSource;
import com.temptationjavaisland.wemeet.source.Event.EventLocalDataSource;
import com.temptationjavaisland.wemeet.source.Event.EventMockDataSource;
import com.temptationjavaisland.wemeet.source.Event.EventRemoteDataSource;
import com.temptationjavaisland.wemeet.source.User.BaseUserAuthenticationRemoteDataSource;
import com.temptationjavaisland.wemeet.source.User.BaseUserDataRemoteDataSource;
import com.temptationjavaisland.wemeet.source.User.UserAuthenticationFirebaseDataSource;
import com.temptationjavaisland.wemeet.source.User.UserFirebaseDataSource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    //OkHttpClient personalizzato con interceptor per aggiungere header User-Agent alle richieste
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Android) WeMeetApp")
                        .build();
                return chain.proceed(request);
            })
            .build();

    public EventAPIService getEventAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TICKETMASTER_API_BASE_URL)  // base url da definire in Constants
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // per conversione JSON
                .build();
        return retrofit.create(EventAPIService.class);
    }

    public EventRoomDatabase getEventDao(Application application) {
        return EventRoomDatabase.getDatabase(application);
    }

    public EventRepository getEventRepository(Application application, boolean debugMode) {
        BaseEventRemoteDataSource eventRemoteDataSource;
        BaseEventLocalDataSource eventLocalDataSource;

        if (debugMode) {
            JSONParserUtils jsonParserUtils = new JSONParserUtils(application);
            eventRemoteDataSource = new EventMockDataSource(jsonParserUtils);
        } else {
            //in produzione usa l'API remota con la API key da risorse
            String apiKey = application.getString(R.string.ticketmaster_key);
            eventRemoteDataSource = new EventRemoteDataSource(apiKey);
        }

        //data source locale per la cache e persistenza dati
        eventLocalDataSource = new EventLocalDataSource(getEventDao(application));

        //crea il repository combinando remote e local data source
        return new EventRepository(eventRemoteDataSource, eventLocalDataSource);
    }

    public IUserRepository getUserRepository(Application application) {

        //remote data source per autenticazione via Firebase
        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();

        //remote data source per dati utente via Firebase
        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserFirebaseDataSource();

        //data source locale eventi per cache
        BaseEventLocalDataSource eventsLocalDataSource =
                new EventLocalDataSource(getEventDao(application));

        //combina le sorgenti dati e restituisce UserRepository
        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, eventsLocalDataSource);
    }

}
