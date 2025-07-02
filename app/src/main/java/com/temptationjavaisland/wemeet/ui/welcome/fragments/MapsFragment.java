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

/**
 * Fragment che mostra una mappa utilizzando MapView.
 */
public class MapsFragment extends Fragment {

    MapView mapView;

    public MapsFragment() {}


    //metodo statico per creare una nuova istanza del Fragment.
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }


    //metodo chiamato alla creazione del Fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //metodo che crea e ritorna la vista associata al Fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        return view;
    }

    //metodo chiamato quando il Fragment diventa visibile e attivo.
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    //metodo chiamato quando il Fragment non è più attivo ma è ancora visibile.
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }


    //metodo chiamato quando il Fragment viene distrutto.
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }


    //metodo chiamato in caso di memoria insufficiente.
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
