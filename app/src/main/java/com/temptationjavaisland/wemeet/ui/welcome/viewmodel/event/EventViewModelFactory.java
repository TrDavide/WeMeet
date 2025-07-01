package com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;

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