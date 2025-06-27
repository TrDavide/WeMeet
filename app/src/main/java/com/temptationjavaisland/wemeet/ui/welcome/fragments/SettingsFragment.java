package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.ui.welcome.WelcomeActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import android.widget.EditText;

public class SettingsFragment extends Fragment {

    private ImageView imageProfile;
    private SharedPreferences preferences;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launcher per scegliere immagine
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imageProfile.setImageURI(imageUri);
                        preferences.edit().putString("profile_image_path", imageUri.toString()).apply();
                    }
                });

        // Launcher per richiedere permesso
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(requireContext(), "Permesso negato, impossibile aprire la galleria", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) bottomNav.setVisibility(View.GONE);

        Button arrowBackButton = view.findViewById(R.id.arrowBackSetting);
        arrowBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_profileFragment));

        NavController navController = Navigation.findNavController(view);

        MaterialButton logoutButton = view.findViewById(R.id.bottone_logout);
        logoutButton.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            // Torna alla WelcomeActivity e cancella lo stack
            Intent intent = new Intent(requireContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        MaterialButton temaButton = view.findViewById(R.id.bottone_tema);
        boolean isNightMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) ||
                ((AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        && (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES);

        temaButton.setText(isNightMode ? "Tema: Notte" : "Tema: Giorno");

        temaButton.setOnClickListener(v -> {
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            requireActivity().recreate();
        });


        imageProfile = view.findViewById(R.id.image_profile);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        String imagePath = preferences.getString("profile_image_path", null);
        if (imagePath != null) {
            imageProfile.setImageURI(Uri.parse(imagePath));
        }

        imageProfile.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), imageProfile);
            popupMenu.getMenuInflater().inflate(R.menu.profile_image_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.remove_photo) {
                    imageProfile.setImageResource(R.mipmap.profile_default);
                    preferences.edit().remove("profile_image_path").apply();
                    return true;

                } else if (item.getItemId() == R.id.change_photo) {
                    requestImagePermission();
                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

        EditText editBio = view.findViewById(R.id.edit_bio);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Carica la biografia salvata, se esiste
        String savedBio = preferences.getString("user_bio", "");
        editBio.setText(savedBio);

        // Salva la biografia ogni volta che viene modificata
        editBio.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newBio = editBio.getText().toString().trim();
                preferences.edit().putString("user_bio", newBio).apply();
            }
        });
    }

    private void requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}
