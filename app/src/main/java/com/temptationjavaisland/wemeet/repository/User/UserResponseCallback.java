package com.temptationjavaisland.wemeet.repository.User;

import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.User;

import java.util.List;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<Event> eventsList);
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
