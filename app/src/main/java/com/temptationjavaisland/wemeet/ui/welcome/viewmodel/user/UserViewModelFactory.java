package com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.temptationjavaisland.wemeet.repository.User.IUserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final IUserRepository userRepository;

    public UserViewModelFactory(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserViewModel(userRepository);
    }
}