package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

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

public class HomeFragment extends Fragment implements ResponseCallBack{

    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    public static final String TAG = LocationFragment.class.getName();
    private RecyclerView recyclerView;
    private EventRecyclerAdapter adapter;

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Inizializzo adapter con lista vuota e listener di click
        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList, new EventRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEventClick(Event event) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_data", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_eventPageFragment, bundle);
            }
        });
        recyclerView.setAdapter(adapter);

        // Mostra progress indicator e nascondi recyclerView in attesa dati
        recyclerView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);

        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            EventAPIResponse response = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
            List<Event> newEventList = response.getEmbedded().getEvents();

            // Aggiorno la lista dati dell'adapter
            eventList.clear();
            eventList.addAll(newEventList);
            adapter.notifyDataSetChanged();

            new android.os.Handler().postDelayed(() -> {
                circularProgressIndicator.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }, 1000); // 1 secondo per test visivo

        } catch (IOException e) {
            requireActivity().runOnUiThread(() ->
                    Snackbar.make(view, "Errore nel caricamento eventi", Snackbar.LENGTH_LONG).show()
            );
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchBar searchBar = view.findViewById(R.id.search_bar);
        RecyclerView recyclerViewHome = view.findViewById(R.id.recyclerViewHome);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onSuccess(List<Event> newEventsList, long lastUpdate) {
        eventList.clear();
        eventList.addAll(newEventsList);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
            }
        });
        Log.i(TAG, newEventsList.get(0).toString());
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}