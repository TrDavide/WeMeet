package com.temptationjavaisland.wemeet.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.temptationjavaisland.wemeet.R;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeoUtils {

    private static final String TAG = "PositionUtils";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());


    public interface LocationCallback {
        void onLocationResult(double lat, double lon);
    }


    public static void getLastKnownLocation(Context context, FusedLocationProviderClient fusedLocationClient, LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            //permessi concessi richiedi ultima posizione nota
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            //posizione trovata restituisci latitudine e longitudine
                            callback.onLocationResult(location.getLatitude(), location.getLongitude());
                        } else {
                            //posizione nulla logga warning e usa fallback Milano
                            Log.w(TAG, "Posizione nulla, fallback Milano");
                            callback.onLocationResult(45.464098, 9.191926); // Milano
                        }
                    })
                    .addOnFailureListener(e -> {
                        //errore nella richiesta posizione logga errore e usa fallback Milano
                        Log.e(TAG, "Errore posizione, fallback Milano", e);
                        callback.onLocationResult(45.464098, 9.191926);
                    });
        } else {
            //permessi non concessi logga warning e usa fallback Milano
            Log.w(TAG, "Permessi non concessi, fallback Milano");
            callback.onLocationResult(45.464098, 9.191926);
        }
    }


    public static void getCityNameAsync(Context context, double lat, double lon, View rootView) {
        if (rootView == null) return;

        TextView cityTextView = rootView.findViewById(R.id.cityTextView);
        executor.execute(() -> {
            String city = "N/A"; //valore di default

            try {
                //crea un Geocoder con la locale di default
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                //ottieni gli indirizzi a partire da lat e lon
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    //per ottenere la località
                    city = address.getLocality();

                    //se null prova con subAdminArea
                    if (city == null) city = address.getSubAdminArea();
                }
            } catch (Exception e) {
                //logga eventuali errori durante la geocodifica
                Log.e(TAG, "Errore nel Geocoder", e);
            }

            //salva il risultato finale in una variabile finale per uso interno della lambda
            String finalCity = city;

            //torna al thread principale per aggiornare l'interfaccia UI
            mainHandler.post(() -> cityTextView.setText(finalCity));
        });
    }
}
