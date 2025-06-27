package com.temptationjavaisland.wemeet.repository.User;

import androidx.lifecycle.MutableLiveData;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.model.User;

import java.util.Set;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserPreferedEvents(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferedEvent(String idToken, Event event);
    void removeUserPreferedEvent(String idToken, String eventId);
}
