package com.temptationjavaisland.wemeet.repository.User;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.temptationjavaisland.wemeet.repository.Event.EventResponseCallback;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.model.User;
import com.temptationjavaisland.wemeet.source.Event.BaseEventLocalDataSource;
import com.temptationjavaisland.wemeet.source.User.BaseUserAuthenticationRemoteDataSource;
import com.temptationjavaisland.wemeet.source.User.BaseUserDataRemoteDataSource;

import java.util.List;

public class UserRepository implements IUserRepository, UserResponseCallback, EventResponseCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseEventLocalDataSource articleLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userPreferedEventsMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseEventLocalDataSource eventsLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.articleLocalDataSource = eventsLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferedEventsMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.articleLocalDataSource.setEventCallback(this);
    }

    //metodo per login o registrazione utente, ritorna LiveData per la UI
    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    //login utente tramite token Google
    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    //recupera eventi preferiti dell'utente
    @Override
    public MutableLiveData<Result> getUserPreferedEvents(String idToken) {
        userDataRemoteDataSource.getUserPreferedEvents(idToken);
        return userPreferedEventsMutableLiveData;
    }

    //salva evento preferito nel backend remoto
    @Override
    public void saveUserPreferedEvent(String idToken, Event event) {
        userDataRemoteDataSource.saveUserPreferedEvent(idToken, event);
    }

    //rimuove evento preferito nel backend remoto
    @Override
    public void removeUserPreferedEvent(String idToken, String eventId) {
        userDataRemoteDataSource.removeUserPreferedEvent(idToken, eventId);
    }

    //restituisce utente attualmente loggato
    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    //effettua logout utente e notifica UI tramite LiveData
    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    //metodi per delegare autenticazione al data source remoto
    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    //callback invocato al successo dell'autenticazione: salva dati utente remoti
    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    //callback invocato al fallimento dell'autenticazione: notifica errore alla UI
    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    //callback invocato al successo del recupero dati utente dal database remoto
    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    //callback invocato al successo del recupero lista eventi dal database remoto
    @Override
    public void onSuccessFromRemoteDatabase(List<Event> articleList) {
        articleLocalDataSource.insertEvents(articleList);
    }

    //callback invocato al fallimento del recupero dati dal database remoto
    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    // Metodo chiamato al successo logout (vuoto per ora)
    @Override
    public void onSuccessLogout() { }

    //metodi vuoti delle callback per eventi (da implementare se necessario)
    @Override
    public void onSuccessFromRemote(EventAPIResponse articleAPIResponse, long lastUpdate) { }

    @Override
    public void onFailureFromRemote(Exception exception) { }

    @Override
    public void onSuccessFromLocal(List<Event> articlesList) { }

    @Override
    public void onFailureFromLocal(Exception exception) { }

    @Override
    public void onFavoriteStatusChanged(Event article, List<Event> preferedArticles) { }

    @Override
    public void onFavoriteStatusChanged(List<Event> news) { }

    @Override
    public void onDeleteFavoriteSuccess(List<Event> favoriteNews) { }

    //callback invocato al successo rimozione evento preferito
    @Override
    public void onSuccessRemoveFavoriteEvent() {
        Log.d(TAG, "Evento preferito rimosso con successo");
        // Possibile notifica LiveData qui
    }

    //callback invocato al fallimento rimozione evento preferito
    @Override
    public void onFailureRemoveFavoriteEvent(String error) {
        Log.e(TAG, "Errore rimozione evento preferito: " + error);
        // Possibile notifica LiveData qui
    }
}
