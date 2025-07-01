package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
import com.temptationjavaisland.wemeet.util.NetworkUtil;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    private RecyclerView recyclerView;
    private EventRecyclerAdapter adapter;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;
    private FrameLayout noInternetView;
    private int radius = 20;
    private String latlong;
    private Long lastUpdate;

    private FusedLocationProviderClient fusedLocationClient;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventRepository eventRepository = ServiceLocator.getInstance().getEventRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(eventRepository)
        ).get(EventViewModel.class);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        eventList = new ArrayList<>();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetView = view.findViewById(R.id.noInternetMessage);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList, new EventRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                EventPageFragment eventPageFragment = new EventPageFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_data", event);
                eventPageFragment.setArguments(bundle);

                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                navController.navigate(R.id.eventPageFragment, bundle);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                Event event = eventList.get(position);
                boolean isCurrentlySaved = event.isSaved();

                event.setSaved(!isCurrentlySaved);
                eventViewModel.updateEvent(event); // aggiorna localmente

                if (!isCurrentlySaved) {
                    // Se non era salvato prima lo salvo (ora è diventato salvato)
                    userViewModel.saveUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), event);
                } else {
                    // Se era già salvato lo rimuovo da Firebase
                    userViewModel.removeUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), event.getId());
                }


            }
        });

        eventViewModel.getPreferedEventsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.EventSuccess) {
                Set<String> savedIds = ((Result.EventSuccess) result)
                        .getData()
                        .getEmbedded()
                        .getEvents()
                        .stream()
                        .map(Event::getId)
                        .collect(Collectors.toSet());

                for (Event event : eventList) {
                    event.setSaved(savedIds.contains(event.getId()));
                }
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);
        noInternetView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);

        if (!NetworkUtil.isInternetAvailable(getContext())) {
            Log.d(TAG, "Sono dentro NetworkUtil");
            List<Event> eventiLocali = eventViewModel.getAll();
            if (!eventiLocali.isEmpty()) {
                Log.d(TAG, "Sono dentro l'IF NetworkUtil");
                eventList.clear();
                eventList.addAll(eventiLocali);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                noInternetView.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Sono dentro l'ELSE NetworkUtil");
                recyclerView.setVisibility(View.GONE);
                noInternetView.setVisibility(View.VISIBLE);
            }
            circularProgressIndicator.setVisibility(View.GONE);
            return view;
        }

        //Check permessi posizione e poi ottieni location
        checkLocationPermissionAndFetch();

        return view;
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndEvents();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchLocationAndEvents() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            Log.d(TAG, "Posizione GPS: " + lat + ", " + lon);
                            latlong = lat + "," + lon;
                            getCityNameAsync(lat, lon, getView());
                            fetchEvents();
                        } else {
                            Log.w(TAG, "Posizione GPS nulla, uso Milano di default");
                            double lat = 45.464098;
                            double lon = 9.191926;
                            latlong = lat + "," + lon;
                            getCityNameAsync(lat, lon, getView());
                            fetchEvents();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Errore nel prendere posizione GPS", e);
                        double lat = 45.464098;
                        double lon = 9.191926;
                        latlong = lat + "," + lon;
                        getCityNameAsync(lat, lon, getView());
                        fetchEvents();
                    });
        } else {
            Log.w(TAG, "Permesso posizione non concesso, uso coordinate fisse");
            double lat = 45.464098;
            double lon = 9.191926;
            latlong = lat + "," + lon;
            getCityNameAsync(lat, lon, getView());
            fetchEvents();
        }
    }


    private void fetchEvents() {
        lastUpdate = 0L; // Forza chiamata rete

        Gson gson = new Gson();
        eventViewModel.getEventsLocation(latlong, radius, "km", "it-it", lastUpdate)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        List<Event> events = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                        for (Event event : events) {
                            String eventJson = gson.toJson(event);
                            Log.d(TAG, "Evento completo JSON: " + eventJson);
                        }
                        eventList.clear();
                        eventList.addAll(events);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        circularProgressIndicator.setVisibility(View.GONE);
                    } else {
                        Log.e(TAG, "Errore fetch eventi: " + ((Result.Error) result));
                        circularProgressIndicator.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndEvents();
            } else {
                Log.w(TAG, "Permesso posizione negato, uso coordinate fisse");
                // fallback coordinate fisse
                double lat = 45.464098;
                double lon = 9.191926;
                latlong = lat + "," + lon;
                getCityNameAsync(lat, lon, getView());
                fetchEvents();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getCityNameAsync(double lat, double lon, View rootView) {
        if (rootView == null) return; // safety check

        TextView cityTextView = rootView.findViewById(R.id.cityTextView);

        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            String city = "N/A";
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    city = address.getLocality();
                    if (city == null) {
                        city = address.getSubAdminArea();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String finalCity = city;
            Log.d(TAG, "Città: " + finalCity);
            mainHandler.post(() -> cityTextView.setText(finalCity));
        });
    }
}
