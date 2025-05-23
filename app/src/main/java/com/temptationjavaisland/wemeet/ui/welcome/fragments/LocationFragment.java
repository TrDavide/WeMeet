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
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends Fragment {

    private EventRecyclerAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();

    public LocationFragment() {}

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView imageView = view.findViewById(R.id.imageView);
        SearchView searchView = view.findViewById(R.id.search_bar);

        recyclerView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        // Caricamento eventi dal JSON
        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            EventAPIResponse response = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
            allEvents = response.getEmbedded().getEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter = new EventRecyclerAdapter(R.layout.event_card, allEvents, event -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_data", event); // Event deve implementare Parcelable
            Navigation.findNavController(requireView()).navigate(R.id.action_locationFragment_to_eventPageFragment, bundle);
        });

        recyclerView.setAdapter(adapter);

        // Filtraggio in tempo reale
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                filterEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                filterEvents(newText);
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

    private void filterEvents(String query) {
        List<Event> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(allEvents);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Event event : allEvents) {
                if (event.getName() != null && event.getName().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(event);
                }
            }
        }
        adapter.updateData(filteredList);
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLocation);
            ImageView imageView = view.findViewById(R.id.imageView);

            recyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}