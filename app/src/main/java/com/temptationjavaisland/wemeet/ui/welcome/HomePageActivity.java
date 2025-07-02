package com.temptationjavaisland.wemeet.ui.welcome;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.Manifest;
import com.temptationjavaisland.wemeet.R;

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
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        //ottieni il NavController per controllare la navigazione tra fragment
        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.placeFragment, R.id.preferedFragment, R.id.profileFragment
        ).build();

        bottomNav.setItemActiveIndicatorEnabled(false);
        bottomNav.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);

        //collega la BottomNavigationView con il NavController per sincronizzare la navigazione
        NavigationUI.setupWithNavController(bottomNav, navController);

        //inizializza il client per la posizione tramite API di Google Play Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //controlla se il permesso di posizione è concesso e agisci di conseguenza
        checkLocationPermission();
    }


    //controlla se il permesso ACCESS_FINE_LOCATION è stato concesso se no, lo richiede all'utente.
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            getUserLocation();
        }
    }


    //Ottiene l'ultima posizione conosciuta dell'utente usando FusedLocationProviderClient
    //Se la posizione è disponibile, la passa al fragment Home
    private void getUserLocation() {
        // Ulteriore verifica permesso per sicurezza
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //richiedi l'ultima posizione nota dell'utente
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //ottieni latitudine e longitudine
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            //mostra latitudine e longitudine tramite Toast (utile per debug)
                            Toast.makeText(HomePageActivity.this,
                                    "Latitudine: " + latitude + "\nLongitudine: " + longitude,
                                    Toast.LENGTH_LONG).show();

                            //passa i dati al fragment Home per usarli nella UI o logica
                            passLocationToHomeFragment(latitude, longitude);

                        } else {
                            //posizione non disponibile mostra messaggio
                            Toast.makeText(HomePageActivity.this,
                                    "Posizione non disponibile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //passa latitudine e longitudine come argomenti al fragment Home
    //usando il NavController per navigare con i dati nel bundle
    private void passLocationToHomeFragment(double latitude, double longitude) {
        //crea un bundle per passare dati
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", latitude);
        bundle.putDouble("lon", longitude);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        if (navHostFragment != null) {
            //naviga verso homeFragment passando il bundle con latitudine e longitudine
            navController.navigate(R.id.homeFragment, bundle);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //verifica che il codice di richiesta sia quello per il permesso posizione
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            //controlla se l'utente ha concesso il permesso
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permesso concesso ottieni posizione
                getUserLocation();
            } else {
                //permesso negato mostra messaggio all'utente
                Toast.makeText(this, "Permesso posizione negato", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
