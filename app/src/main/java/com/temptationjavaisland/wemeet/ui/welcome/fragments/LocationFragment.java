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
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
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
    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inizializzazione dei repository tramite ServiceLocator
        EventRepository eventRepository =
                ServiceLocator.getInstance().getEventRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        //viewModel per gestire eventi
        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(eventRepository)).get(EventViewModel.class);

        //viewModel per gestire utente e preferenze
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.search_bar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        //adapter per mostrare gli eventi e gestire click e salvataggi
        adapter = new EventRecyclerAdapter(R.layout.event_card, new ArrayList<>(), new EventRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                //collegamento alla pagina evento
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_data", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_locationFragment_to_eventPageFragment, bundle);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                //aggiunge o rimuove l'evento dai preferiti sia in locale che remoto
                Event event = eventList.get(position);
                boolean newSavedState = !event.isSaved();
                event.setSaved(newSavedState);

                if (newSavedState) {
                    eventViewModel.insertEvent(event); //salva in locale
                    userViewModel.saveUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), event);
                } else {
                    eventViewModel.unsetFavorite(event.getId()); //rimuove da DB locale
                    eventViewModel.removeFromFavorite(event);    //rimuove anche dalla lista in memoria
                    userViewModel.removeUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), event.getId());
                }

                adapter.notifyItemChanged(position);
            }
        });

        //osserva i preferiti per aggiornare lo stato saved degli eventi mostrati
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

        setupSearchView(); //configura la barra di ricerca

        return view;
    }

    private void setupSearchView() {
        //mostra il recycler quando si attiva la ricerca
        searchView.setOnSearchClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        });

        //ripristina lo stato iniziale alla chiusura
        searchView.setOnCloseListener(() -> {
            adapter.updateData(new ArrayList<>());
            recyclerView.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            return false;
        });

        //gestione ricerca testo
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
        //esegue la ricerca degli eventi via API in base alla keyword
        eventViewModel.searchEvents(keyword)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        EventAPIResponse response = ((Result.EventSuccess) result).getData();
                        if (response.getEmbedded() != null && response.getEmbedded().getEvents() != null) {
                            List<Event> events = response.getEmbedded().getEvents();
                            eventList.clear();
                            eventList.addAll(events);

                            //controlla se gli eventi sono già salvati
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
                            //nessun risultato trovato
                            eventList.clear();
                            adapter.updateData(eventList);
                            recyclerView.setVisibility(View.GONE);
                            layoutEmptyState.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //errore nella chiamata API
                        eventList.clear();
                        adapter.updateData(eventList);
                        recyclerView.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                });
    }
}
