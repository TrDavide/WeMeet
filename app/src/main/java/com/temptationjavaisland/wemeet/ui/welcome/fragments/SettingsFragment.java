package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.content.Intent;
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
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.ui.welcome.LoginActivity;
import com.temptationjavaisland.wemeet.ui.welcome.WelcomeActivity;


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

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }


        // Trova il bottone della freccia
        Button arrowBackButton = view.findViewById(R.id.arrowBackSetting);

        arrowBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_profileFragment);
        });

        NavController navController = Navigation.findNavController(view);

        MaterialButton logoutButton = view.findViewById(R.id.bottone_logout);
        //MaterialButton modificaButton = view.findViewById(R.id.modifica_profilo);
        MaterialButton eliminaPreferitiBtn = view.findViewById(R.id.elimina_preferiti);

        /*modificaButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_settingsFragment_to_modificaProfiloFragment);
        });

         */

        logoutButton.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            // Torna alla WelcomeActivity e cancella lo stack
            Intent intent = new Intent(requireContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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
