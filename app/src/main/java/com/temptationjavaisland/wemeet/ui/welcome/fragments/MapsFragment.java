package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temptationjavaisland.wemeet.R;

public class MapsFragment extends Fragment {

    MapView mapView;

    public MapsFragment() {

    }

    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        /*
        mapView = view.findViewById(R.id.mappaView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            // Coordinate di Milano
            LatLng milano = new LatLng(45.4642, 9.1900);
            // Aggiungi un marker
            googleMap.addMarker(new MarkerOptions().position(milano).title("Milano"));
            // Sposta la camera sulla posizione del marker con un certo zoom
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(milano, 12));
        });*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}