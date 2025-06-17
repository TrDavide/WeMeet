package com.temptationjavaisland.wemeet.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.temptationjavaisland.wemeet.repository.EventRepository;

/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the NewsViewModel class.
 */
public class EventViewModelFactory implements ViewModelProvider.Factory {

    private final EventRepository eventRepository;

    public EventViewModelFactory(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EventViewModel(eventRepository);
    }
}