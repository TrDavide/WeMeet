package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.temptationjavaisland.wemeet.R;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchBar;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;

import java.io.IOException;
import java.util.List;

public class LocationFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    public static final String TAG = LocationFragment.class.getName();

    public LocationFragment() {}

    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            EventAPIResponse response = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
            Log.i(TAG, response.getTotalResults() + "sono io");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchBar searchBar = view.findViewById(R.id.search_bar);
        RecyclerView recyclerViewHome = view.findViewById(R.id.recyclerViewHome);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Gestione click sul menu (tre puntini)
        searchBar.setOnMenuItemClickListener(new SearchBar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_filter) {
                    Toast.makeText(getContext(), "Apertura filtri...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        /*
        try {
            EventAPIResponse response = jsonParserUtils.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);
            List<Event> eventList = response.getEvent();
            EventRecyclerAdapter adapter = new EventRecyclerAdapter(R.layout.fragment_location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

    }


}