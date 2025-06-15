package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventApiResponseRetrofit;
import com.temptationjavaisland.wemeet.model.EventService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationFragment extends Fragment {

    private static final String API_KEY = "A5mU8sFCAGSybDD2Po2bLphN3AlazHoG";
    private EventRecyclerAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView imageView;
    private SearchView searchView;

    public LocationFragment() {}

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        imageView = view.findViewById(R.id.imageView);
        searchView = view.findViewById(R.id.search_bar);

        recyclerView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        fetchEvents();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchFilteredEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchFilteredEvents(newText);
                return true;
            }
        });

        searchView.setOnSearchClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        });

        searchView.setOnCloseListener(() -> {
            recyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            return false;
        });

        return view;
    }

    private void fetchEvents() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://app.ticketmaster.com/discovery/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EventService service = retrofit.create(EventService.class);

        service.getEvents(API_KEY).enqueue(new Callback<EventApiResponseRetrofit>() {
            @Override
            public void onResponse(Call<EventApiResponseRetrofit> call, Response<EventApiResponseRetrofit> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getEmbedded() != null) {
                    allEvents = response.body().getEmbedded().getEvents();

                    adapter = new EventRecyclerAdapter(R.layout.event_card, allEvents, event -> {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event_data", event);
                        Navigation.findNavController(requireView()).navigate(R.id.action_locationFragment_to_eventPageFragment, bundle);
                    });

                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<EventApiResponseRetrofit> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fetchFilteredEvents(String keyword) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://app.ticketmaster.com/discovery/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EventService service = retrofit.create(EventService.class);

        service.searchEvents(API_KEY, keyword).enqueue(new Callback<EventApiResponseRetrofit>() {
            @Override
            public void onResponse(Call<EventApiResponseRetrofit> call, Response<EventApiResponseRetrofit> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getEmbedded() != null) {
                    List<Event> filtered = response.body().getEmbedded().getEvents();
                    adapter.updateData(filtered);
                }
            }

            @Override
            public void onFailure(Call<EventApiResponseRetrofit> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
