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

import com.temptationjavaisland.wemeet.R;

public class WelcomeFragment extends Fragment {

    public WelcomeFragment() {
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //controlla se l'utente è già autenticato in Firebase
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            //se sì, naviga direttamente alla home page e esci da questo metodo
            Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_homePageActivity);
            return;
        }

        Button loginButton = view.findViewById(R.id.accediButton);
        Button signUpButton = view.findViewById(R.id.RegistratiButton);

        //listener per il bottone "Accedi"
        loginButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_welcomeFragment_to_loginFragment);
        });

        //listener per il bottone "Registrati"
        signUpButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_welcomeFragment_to_signUpFragment);
        });
    }
}
