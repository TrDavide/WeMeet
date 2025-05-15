package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.search.SearchBar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {


    public HomeFragment() {}

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            EventAPIResponse response = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
            List<Event> eventList= response.getEmbedded().getEvents();
            EventRecyclerAdapter adapter = new EventRecyclerAdapter(R.layout.event_card, eventList, false);
            recyclerView.setAdapter(adapter);
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
    }
}