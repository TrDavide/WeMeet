package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventApiResponseRetrofit;
import com.temptationjavaisland.wemeet.model.EventService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationFragment extends Fragment {

    private static final String API_KEY = "A5mU8sFCAGSybDD2Po2bLphN3AlazHoG";

    private EventRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private LinearLayout layoutEmptyState;

    // Retrofit e service per le chiamate API
    private Retrofit retrofit;
    private EventService eventService;

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

        // Adapter con lista vuota e listener per click evento
        adapter = new EventRecyclerAdapter(R.layout.event_card, new ArrayList<>(), event -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_data", event);
            Navigation.findNavController(requireView()).navigate(R.id.action_locationFragment_to_eventPageFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        // All'avvio mostra layoutEmptyState (immagine + testo) e nascondi lista
        recyclerView.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);

        // Inizializza Retrofit e EventService
        retrofit = new Retrofit.Builder()
                .baseUrl("https://app.ticketmaster.com/discovery/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        eventService = retrofit.create(EventService.class);

        setupSearchView();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnSearchClickListener(v -> {
            // Quando apri la ricerca mostra recyclerView e nascondi layoutEmptyState
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        });

        searchView.setOnCloseListener(() -> {
            // Quando chiudi la ricerca nascondi lista e mostra layoutEmptyState
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
        if (keyword.isEmpty()) {
            adapter.updateData(new ArrayList<>());
            recyclerView.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        eventService.searchEvents(API_KEY, keyword).enqueue(new Callback<EventApiResponseRetrofit>() {
            @Override
            public void onResponse(Call<EventApiResponseRetrofit> call, Response<EventApiResponseRetrofit> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getEmbedded() != null) {
                    List<Event> events = response.body().getEmbedded().getEvents();
                    Log.d("LocationFragment", "Events found: " + events.size());
                    adapter.updateData(events);

                    if (!events.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("LocationFragment", "Nessun evento trovato o errore nella risposta");
                    adapter.updateData(new ArrayList<>());
                    recyclerView.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<EventApiResponseRetrofit> call, Throwable t) {
                Log.e("LocationFragment", "Errore nella chiamata API", t);
                adapter.updateData(new ArrayList<>());
                recyclerView.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }
}
