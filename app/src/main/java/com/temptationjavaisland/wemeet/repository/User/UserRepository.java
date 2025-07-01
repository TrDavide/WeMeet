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
import java.util.Set;

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

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferedEvents(String idToken) {
        userDataRemoteDataSource.getUserPreferedEvents(idToken);
        return userPreferedEventsMutableLiveData;
    }

    @Override
    public void saveUserPreferedEvent(String idToken, Event event) {
        userDataRemoteDataSource.saveUserPreferedEvent(idToken, event);
    }

    @Override
    public void removeUserPreferedEvent(String idToken, String eventId) {
        userDataRemoteDataSource.removeUserPreferedEvent(idToken, eventId);
    }



    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

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

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Event> articleList) {
        articleLocalDataSource.insertEvents(articleList);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onSuccessFromRemote(EventAPIResponse articleAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(List<Event> articlesList) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onFavoriteStatusChanged(Event article, List<Event> preferedArticles) {

    }

    @Override
    public void onFavoriteStatusChanged(List<Event> news) {

    }

    @Override
    public void onDeleteFavoriteSuccess(List<Event> favoriteNews) {

    }

    //@Override
    public void onSuccessFromCloudReading(List<Event> eventsList) {

    }

    //@Override
    public void onSuccessFromCloudWriting(Event event) {

    }

    //@Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessRemoveFavoriteEvent() {
        // Qui puoi notificare al LiveData o fare altre operazioni di successo
        Log.d(TAG, "Evento preferito rimosso con successo");
        // Esempio: userFavoriteNewsMutableLiveData.postValue(...);
    }

    @Override
    public void onFailureRemoveFavoriteEvent(String error) {
        Log.e(TAG, "Errore rimozione evento preferito: " + error);
        // Puoi notificare errore al LiveData o altro
    }



}