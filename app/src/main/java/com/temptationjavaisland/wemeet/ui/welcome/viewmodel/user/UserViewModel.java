package com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.model.User;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;

import java.util.Set;


public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userPreferedEventsMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    //restituisce userLiveData
    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    //restituisce userLiveData da login con token Google
    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    //liveData degli eventi preferiti dell'utente
    public MutableLiveData<Result> getUserPreferedEventsMutableLiveData(String idToken) {
        if (userPreferedEventsMutableLiveData == null) {
            getUserPreferedEvents(idToken);
        }
        return userPreferedEventsMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    //logout aggiorna userMutableLiveData con esito logout
    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        return userMutableLiveData;
    }

    //recupera eventi preferiti dell'utente
    private void getUserPreferedEvents(String idToken) {
        userPreferedEventsMutableLiveData = userRepository.getUserPreferedEvents(idToken);
    }

    //salva un evento preferito
    public void saveUserPreferedEvent(String idToken, Event event) {
        userRepository.saveUserPreferedEvent(idToken, event);
    }

    //rimuove un evento preferito e aggiorna la lista
    public void removeUserPreferedEvent(String idToken, String eventId) {
        userRepository.removeUserPreferedEvent(idToken, eventId);
        getUserPreferedEvents(idToken);
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    //flag per errore autenticazione
    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    //chiamate interne per inizializzare userMutableLiveData
    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }
}
