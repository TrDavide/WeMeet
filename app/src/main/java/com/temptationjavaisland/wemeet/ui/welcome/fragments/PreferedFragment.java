package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.repository.EventAPIRepository;
import com.temptationjavaisland.wemeet.repository.EventMockRepository;
import com.temptationjavaisland.wemeet.repository.IEventRepository;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;
import com.temptationjavaisland.wemeet.util.ResponseCallBack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreferedFragment extends Fragment implements ResponseCallBack {

    BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private IEventRepository eventRepository;
    private List<Event> eventList;
    private EventRecyclerAdapter adapter;
    private static final String TAG = EventAPIRepository.class.getSimpleName();

    public PreferedFragment() {}

    public static PreferedFragment newInstance() {
        PreferedFragment fragment = new PreferedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventList = new ArrayList<>();

        if(requireActivity().getResources().getBoolean(R.bool.debug_mode)){
            eventRepository = new EventMockRepository();
        }else{
            eventRepository = new EventAPIRepository(requireActivity().getApplication(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prefered, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPrefered);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList);
        recyclerView.setAdapter(adapter);
        eventRepository.fetchEvents("it", "Milano", "Blanco" , 10, 1000);

        //List<Event> eventList = EventRoomDatabase.getDatabase(getContext()).eventsDao().isSaved();
        Log.d("DB_TEST", "Eventi salvati: " + eventList.size());

        return view;
    }

    @Override
    public void onSuccess(List<Event> newEventsList, long lastUpdate) {
        eventList.clear();
        eventList.addAll(newEventsList);
        Log.d("PREFERED_FRAGMENT", "onSuccess chiamato con " + newEventsList.size() + " eventi");
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG).show();
    }
}
