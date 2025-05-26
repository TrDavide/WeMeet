package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;

public class EventPageFragment extends Fragment {

    MapView mapView;

    public EventPageFragment() {}

    public static EventPageFragment newInstance(String param1, String param2) {
        EventPageFragment fragment = new EventPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_page, container, false);
        mapView = view.findViewById(R.id.mapsView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            LatLng milano = new LatLng(45.4642, 9.1900);
            googleMap.addMarker(new MarkerOptions().position(milano).title("Milano"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(milano, 12));
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton backButton = view.findViewById(R.id.arrowBack); // Sostituisci con l'ID esatto del tuo bottone

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

    }
}