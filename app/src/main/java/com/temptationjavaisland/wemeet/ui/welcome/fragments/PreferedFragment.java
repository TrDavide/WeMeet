package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;

import java.util.ArrayList;
import java.util.List;

public class PreferedFragment extends Fragment {

    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    private EventRecyclerAdapter adapter;

    public PreferedFragment() {}

    public static PreferedFragment newInstance() {
        return new PreferedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prefered, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPrefered);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList, new EventRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEventClick(Event event) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_data", event); // Assicurati che Event implementi Parcelable
                Navigation.findNavController(requireView()).navigate(R.id.action_preferedFragment_to_eventPageFragment, bundle);
            }
        });
        recyclerView.setAdapter(adapter);

        loadSavedEvents();

        return view;
    }

    private void loadSavedEvents() {
        new Thread(() -> {
            List<Event> saved = EventRoomDatabase
                    .getDatabase(requireContext())
                    .eventsDao()
                    .getAllSavedEvents();

            Log.d("DB_TEST", "Eventi salvati trovati: " + saved.size());

            requireActivity().runOnUiThread(() -> {
                eventList.clear();
                eventList.addAll(saved);
                adapter.notifyDataSetChanged();

                recyclerView.setVisibility(View.VISIBLE);
            });
        }).start();
    }
}
