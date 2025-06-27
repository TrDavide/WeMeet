package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.temptationjavaisland.wemeet.R;

public class ProfileFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    private ImageView profileImageView;
    private TextView bioTextView;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bioTextView = view.findViewById(R.id.bio);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        Button btnSettings = view.findViewById(R.id.button_settings);
        btnSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_settingsFragment)
        );

        // Riferimento all'immagine profilo
        profileImageView = view.findViewById(R.id.profileImageView).findViewById(R.id.imageView);

        loadProfileImage();
        loadUserBio();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileImage();
        loadUserBio();
    }

    private void loadUserBio() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String userBio = preferences.getString("user_bio", getString(R.string.frase_bio));
        if (bioTextView != null) {
            bioTextView.setText(userBio);
        }
    }

    private void loadProfileImage() {
        if (profileImageView == null) return;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String imagePath = preferences.getString("profile_image_path", null);

        if (imagePath != null) {
            profileImageView.setImageURI(Uri.parse(imagePath));
        } else {
            profileImageView.setImageResource(R.mipmap.profile_default);
        }
    }
}
