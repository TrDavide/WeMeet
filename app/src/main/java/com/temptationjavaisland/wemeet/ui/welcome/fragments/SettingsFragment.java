package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;


public class SettingsFragment extends Fragment {

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Trova il bottone della freccia
        MaterialButton backButton = view.findViewById(R.id.arrowBackWelcome);
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        MaterialButton logoutButton = view.findViewById(R.id.bottone_logout);
        MaterialButton modificaButton = view.findViewById(R.id.modifica_profilo);
        MaterialButton eliminaPreferitiBtn = view.findViewById(R.id.elimina_preferiti);

        modificaButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_settingsFragment_to_modificaProfiloFragment);
        });

        logoutButton.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentContainerView, new WelcomeFragment());
            transaction.commit();
        });

        eliminaPreferitiBtn.setOnClickListener(v -> {
            EventRoomDatabase.databaseWriteExecutor.execute(() -> {
                EventRoomDatabase
                        .getDatabase(requireContext())
                        .eventsDao()
                        .deleteAllSavedEvents();
            });
        });

    }
}
