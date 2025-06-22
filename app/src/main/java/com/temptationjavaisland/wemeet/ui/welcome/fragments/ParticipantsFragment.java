package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.model.Event;

public class ParticipantsFragment extends Fragment {

    private Event event;

    public ParticipantsFragment() {}

    public static ParticipantsFragment newInstance() {
        ParticipantsFragment fragment = new ParticipantsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partecipants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button arrowBackParticipant = view.findViewById(R.id.arrowBackParticipants);

        arrowBackParticipant.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_data", event); // ripassa l'evento

            // Naviga verso EventPageFragment con bundle
            Navigation.findNavController(v).navigate(R.id.eventPageFragment, bundle);

            // Subito dopo, rimuovi ParticipantsFragment dallo stack
            Navigation.findNavController(v).popBackStack(R.id.participantsFragment, true);
        });
    }
}