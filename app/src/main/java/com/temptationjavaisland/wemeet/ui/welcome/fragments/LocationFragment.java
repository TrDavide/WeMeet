package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationFragment extends Fragment {

    private EventRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private LinearLayout layoutEmptyState;
    private List<Event> eventList;
    private EventViewModel eventViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventRepository eventRepository =
                ServiceLocator.getInstance().getEventRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(eventRepository)).get(EventViewModel.class);

        eventList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.search_bar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        adapter = new EventRecyclerAdapter(R.layout.event_card, new ArrayList<>(), new EventRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_data", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_locationFragment_to_eventPageFragment, bundle);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                Event event = eventList.get(position);
                event.setSaved(!event.isSaved());

                if (event.getUid() == 0) {
                    EventRoomDatabase.databaseWriteExecutor.execute(() -> {
                        long newId = EventRoomDatabase.getDatabase(recyclerView.getContext())
                                .eventsDao()
                                .insertEventsList(List.of(event))
                                .get(0);
                        event.setUid((int) newId);

                        EventRoomDatabase.getDatabase(recyclerView.getContext())
                                .eventsDao()
                                .updateEvent(event);
                    });
                } else {
                    eventViewModel.updateEvent(event);
                }
            }
        });

        eventViewModel.getPreferedEventsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.EventSuccess) {
                List<Event> savedEvents = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                Set<String> savedIds = new HashSet<>();
                for (Event e : savedEvents) {
                    savedIds.add(e.getId());
                }

                for (Event event : eventList) {
                    event.setSaved(savedIds.contains(event.getId()));
                }

                adapter.notifyDataSetChanged();
            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);

        setupSearchView();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnSearchClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        });

        searchView.setOnCloseListener(() -> {
            adapter.updateData(new ArrayList<>());
            recyclerView.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEvents(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapter.updateData(new ArrayList<>());
                    recyclerView.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                } else {
                    searchEvents(newText.trim());
                }
                return true;
            }
        });
    }

    private void searchEvents(String keyword) {
        eventViewModel.searchEvents(keyword)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        EventAPIResponse response = ((Result.EventSuccess) result).getData();
                        if (response.getEmbedded() != null && response.getEmbedded().getEvents() != null) {
                            List<Event> events = response.getEmbedded().getEvents();
                            eventList.clear();
                            eventList.addAll(events);

                            // ⬇️ Verifica quali eventi sono salvati confrontando gli ID
                            Result preferedResult = eventViewModel.getPreferedEventsLiveData().getValue();
                            if (preferedResult instanceof Result.EventSuccess) {
                                List<Event> savedEvents = ((Result.EventSuccess) preferedResult).getData().getEmbedded().getEvents();
                                Set<String> savedIds = new HashSet<>();
                                for (Event e : savedEvents) {
                                    savedIds.add(e.getId());
                                }

                                for (Event e : eventList) {
                                    e.setSaved(savedIds.contains(e.getId()));
                                }
                            }

                            adapter.updateData(eventList);
                            recyclerView.setVisibility(View.VISIBLE);
                            layoutEmptyState.setVisibility(View.GONE);
                        } else {
                            eventList.clear();
                            adapter.updateData(eventList);
                            recyclerView.setVisibility(View.GONE);
                            layoutEmptyState.setVisibility(View.VISIBLE);
                        }
                    } else {
                        eventList.clear();
                        adapter.updateData(eventList);
                        recyclerView.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                });
    }

}