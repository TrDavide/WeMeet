package com.temptationjavaisland.wemeet.ui.welcome;

import static java.security.AccessController.getContext;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
//import com.temptationjavaisland.wemeet.Manifest;
import android.Manifest;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;


public class HomePageActivity extends AppCompatActivity {

    private NavController navController;
    BottomNavigationView bottomNav;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragmentContainerView);

        navController = navHostFragment.getNavController();

        bottomNav = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.placeFragment, R.id.preferedFragment, R.id.profileFragment
        ).build();
        bottomNav.setItemActiveIndicatorEnabled(false);
        bottomNav.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);

        NavigationUI.setupWithNavController(bottomNav, navController);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permesso NON ancora concesso, lo richiediamo
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            // Permesso già concesso, ottieni la posizione
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return; // Ulteriore controllo
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Toast.makeText(HomePageActivity.this,
                                    "Latitudine: " + latitude + "\nLongitudine: " + longitude,
                                    Toast.LENGTH_LONG).show();

                            // Passa latitudine e longitudine al fragment
                            passLocationToHomeFragment(latitude, longitude);

                        } else {
                            Toast.makeText(HomePageActivity.this,
                                    "Posizione non disponibile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void passLocationToHomeFragment(double latitude, double longitude) {
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", latitude);
        bundle.putDouble("lon", longitude);

        // Ottieni il NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        if (navHostFragment != null) {
            // Ottieni il fragment attualmente visibile (homeFragment o altro)
            // Assumendo che HomeFragment sia quello caricato, ottieni la navController e navBackStackEntry
            navController.navigate(R.id.homeFragment, bundle);
            // In alternativa, puoi usare questo se vuoi sostituire il fragment con gli argomenti:
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation(); // Permesso concesso → ottieni posizione
            } else {
                Toast.makeText(this, "Permesso posizione negato", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
