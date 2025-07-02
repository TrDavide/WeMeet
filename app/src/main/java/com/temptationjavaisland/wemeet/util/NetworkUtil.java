package com.temptationjavaisland.wemeet.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkUtil {

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            if (networkCapabilities == null) return false; // Capacità non disponibili

            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        }
        return false;
    }
}
