package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.model.Event;

public class EventPageFragment extends Fragment {

    private MapView mapView;
    private ImageView eventImageView;
    private TextView titleTextView, dateTextView, locationTextView, descriptionTextView, positionTextView;
    private Event event;

    public EventPageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_page, container, false);

        //recupera l'evento passato tramite bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            event = bundle.getParcelable("event_data");
        }

        //collega i widget del layout alle variabili
        eventImageView = view.findViewById(R.id.shapeableImageView);
        titleTextView = view.findViewById(R.id.eventTitleTextView);
        dateTextView = view.findViewById(R.id.eventDateTextView);
        locationTextView = view.findViewById(R.id.eventLocationTextView);
        descriptionTextView = view.findViewById(R.id.textSubtitle);
        positionTextView = view.findViewById(R.id.textPersone);
        mapView = view.findViewById(R.id.mapsView);

        if (event != null) {
            titleTextView.setText(event.getName());

            //controlla la presenza della data e la mostra
            if (event.getDates() != null && event.getDates().getStart() != null) {
                dateTextView.setText(event.getDates().getStart().getLocalTime());
            } else {
                dateTextView.setText("Data non disponibile");
            }

            //controlla se il luogo è disponibile e lo setta
            if (event.getEmbedded() != null &&
                    event.getEmbedded().getVenues() != null &&
                    !event.getEmbedded().getVenues().isEmpty() &&
                    event.getEmbedded().getVenues().get(0) != null) {

                locationTextView.setText(event.getEmbedded().getVenues().get(0).getName());

                //controlla la città e la mostra
                if (event.getEmbedded().getVenues().get(0).getCity() != null &&
                        event.getEmbedded().getVenues().get(0).getCity().getName() != null) {
                    positionTextView.setText(event.getEmbedded().getVenues().get(0).getCity().getName());
                } else {
                    positionTextView.setText("Posizione non disponibile");
                }
            } else {
                locationTextView.setText("Luogo non disponibile");
                positionTextView.setText("Posizione non disponibile");
            }

            //mostra la descrizione
            descriptionTextView.setText(event.getName());

            //carica immagine evento con Glide se disponibile, altrimenti immagine di default
            if (event.getImages() != null && !event.getImages().isEmpty() && eventImageView != null) {
                String imageUrl = event.getImages().get(0).getUrl();
                Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.event_background)
                        .into(eventImageView);
            } else if (eventImageView != null) {
                eventImageView.setImageResource(R.drawable.event_background);
            }
        }

        //inizializza la MapView e aggiunge un marker sulla posizione evento
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            if (event != null && event.getEmbedded() != null && !event.getEmbedded().getVenues().isEmpty()) {
                double lat = Double.parseDouble(event.getEmbedded().getVenues().get(0).getLocation().getLatitude());
                double lng = Double.parseDouble(event.getEmbedded().getVenues().get(0).getLocation().getLongitude());

                Log.d("MAP_DEBUG", "Latitudine: " + lat + ", Longitudine: " + lng);
                LatLng location = new LatLng(lat, lng);

                //aggiunge marker sulla mappa e posiziona la camera su di esso
                googleMap.addMarker(new MarkerOptions().position(location).title(event.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
            } else {
                Log.e("MAP_DEBUG", "Evento o venues mancanti");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //nasconde la BottomNavigationView per questa pagina
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }

        //bottone per tornare indietro nella navigazione
        Button backButton = view.findViewById(R.id.arrowBack);
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        //bottone per aprire l'URL dell'evento nel browser
        MaterialButton buttonAcquista = view.findViewById(R.id.buttonAcquista);
        buttonAcquista.setOnClickListener(v -> {
            if (event != null && event.getUrl() != null && !event.getUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrl()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(requireContext(), "URL non disponibile per questo evento", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
