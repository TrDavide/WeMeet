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

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            callback.onLocationResult(location.getLatitude(), location.getLongitude());
                        } else {
                            Log.w(TAG, "Posizione nulla, fallback Milano");
                            callback.onLocationResult(45.464098, 9.191926);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Errore posizione, fallback Milano", e);
                        callback.onLocationResult(45.464098, 9.191926);
                    });
        } else {
            Log.w(TAG, "Permessi non concessi, fallback Milano");
            callback.onLocationResult(45.464098, 9.191926);
        }
    }

    public static void getCityNameAsync(Context context, double lat, double lon, View rootView) {
        if (rootView == null) return;

        TextView cityTextView = rootView.findViewById(R.id.cityTextView);

        executor.execute(() -> {
            String city = "N/A";
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    city = address.getLocality();
                    if (city == null) city = address.getSubAdminArea();
                }
            } catch (Exception e) {
                Log.e(TAG, "Errore nel Geocoder", e);
            }

            String finalCity = city;
            mainHandler.post(() -> cityTextView.setText(finalCity));
        });
    }
}
