package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PreferedFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Event> eventList;
    private EventRecyclerAdapter adapter;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;

    public PreferedFragment() {}

    public static PreferedFragment newInstance() {
        return new PreferedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventRepository eventRepository = ServiceLocator.getInstance().getEventRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(eventRepository)).get(EventViewModel.class);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        eventList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);

        // 1. Mostra subito i preferiti locali
        eventViewModel.getPreferedEventsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.EventSuccess) {
                List<Event> localEvents = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                eventList.clear();
                eventList.addAll(localEvents);
                adapter.notifyDataSetChanged();
            }
        });

        // 2. Quando arrivano i preferiti remoti → sincronizza e aggiorna
        userViewModel.getUserPreferedEventsMutableLiveData(userViewModel.getLoggedUser().getIdToken())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.EventSuccess) {
                        List<Event> remoteEvents = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                        // salva nel locale
                        eventViewModel.insertEvents(remoteEvents);

                        // aggiorna lista visualizzata (solo se vuoi aggiornare subito)
                        eventList.clear();
                        eventList.addAll(remoteEvents);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prefered, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPrefered);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        EventRoomDatabase.getDatabase(recyclerView.getContext())
                .eventsDao()
                .getSaved();

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList,
                new EventRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onEventItemClick(Event event) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event_data", event);

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                        navController.navigate(R.id.eventPageFragment, bundle);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        Event event = eventList.get(position);
                        event.setSaved(!event.isSaved());
                        eventViewModel.updateEvent(event);
                        userViewModel.removeUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), eventList.get(position).getId());
                    }
                });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
