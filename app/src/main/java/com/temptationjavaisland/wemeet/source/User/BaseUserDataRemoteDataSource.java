package com.temptationjavaisland.wemeet.source.User;



import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.User;
import com.temptationjavaisland.wemeet.repository.User.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);

    public abstract void getUserPreferedEvents(String idToken);

    public abstract void saveUserPreferedEvent(String idToken, Event event);

    public abstract void removeUserPreferedEvent(String idToken, String eventId);



}

