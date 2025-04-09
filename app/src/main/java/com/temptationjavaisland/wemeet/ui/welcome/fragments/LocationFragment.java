package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.temptationjavaisland.wemeet.R;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.search.SearchBar;

public class LocationFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

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
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchBar searchBar = view.findViewById(R.id.search_bar);

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

    }


}