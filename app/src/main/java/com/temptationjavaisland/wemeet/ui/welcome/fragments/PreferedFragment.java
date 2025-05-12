package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;

import java.io.IOException;
import java.util.List;

public class PreferedFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    public PreferedFragment() {}

    public static PreferedFragment newInstance(String param1, String param2) {
        PreferedFragment fragment = new PreferedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prefered, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPrefered);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        EventRecyclerAdapter adapter = new EventRecyclerAdapter(R.layout.event_card, List.of());
        recyclerView.setAdapter(adapter);

        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());

        try {
            // Caricamento dati da JSON
            EventAPIResponse response = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
            List<Event> eventList = response.getEmbedded().getEvents();

            // Mostra subito i dati del JSON
            adapter.updateData(eventList);

        } catch (IOException e) {
            e.printStackTrace();  // Log per debugging
        }

        // Carica i dati da Room solo se disponibili
        new Thread(() -> {
            EventRoomDatabase db = Room.databaseBuilder(requireContext(), EventRoomDatabase.class, "event_database")
                    .fallbackToDestructiveMigration()
                    .build();

            List<Event> eventListDb = db.eventsDao().getAll();
            if (eventListDb != null && !eventListDb.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    adapter.updateData(eventListDb);  // Sovrascrive se il DB ha dati
                });
            }
        }).start();

        return view;
    }

}
