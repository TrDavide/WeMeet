package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.temptationjavaisland.wemeet.util.GeoUtils;
import com.temptationjavaisland.wemeet.util.NetworkUtil;
import com.temptationjavaisland.wemeet.util.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private int radius = 20; //raggio eventi in km
    private String latlong; //coordinate utente
    private Long lastUpdate;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inizializzazione dei ViewModel tramite ServiceLocator
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //inizializzazione componenti grafici
        noInternetView = view.findViewById(R.id.noInternetMessage);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //adapter con callback per click evento e pulsante preferito
        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList, new EventRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEventItemClick(Event event) {
                //collegamento alla pagina evento
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_data", event);
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.eventPageFragment, bundle);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                //salvataggio o rimozione evento preferito
                Event event = eventList.get(position);
                boolean isCurrentlySaved = event.isSaved();
                event.setSaved(!isCurrentlySaved);
                eventViewModel.updateEvent(event);

                if (!isCurrentlySaved) {
                    userViewModel.saveUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), event);
                } else {
                    userViewModel.removeUserPreferedEvent(userViewModel.getLoggedUser().getIdToken(), event.getId());
                }
            }
        });

        //vede gli eventi preferiti e aggiorna la UI
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

        //se manca internet mostra eventi salvati localmente
        if (!NetworkUtil.isInternetAvailable(getContext())) {
            List<Event> eventiLocali = eventViewModel.getAll();
            if (!eventiLocali.isEmpty()) {
                eventList.clear();
                eventList.addAll(eventiLocali);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                noInternetView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
                noInternetView.setVisibility(View.VISIBLE);
            }
            circularProgressIndicator.setVisibility(View.GONE);
            return view;
        }

        //controllo permessi e fetch posizione/eventi
        checkLocationPermissionAndFetch();
        return view;
    }

    private void checkLocationPermissionAndFetch() {
        //richiesta permesso posizione se non già concesso
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndEvents();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchLocationAndEvents() {
        //ottiene la posizione e aggiorna eventi in base alla posizione
        GeoUtils.getLastKnownLocation(requireContext(), fusedLocationClient, (lat, lon) -> {
            latlong = lat + "," + lon;
            GeoUtils.getCityNameAsync(requireContext(), lat, lon, getView());
            fetchEvents();
        });
    }

    private void fetchEvents() {
        //recupera eventi da remoto e aggiorna la UI
        lastUpdate = 0L;
        Gson gson = new Gson();
        eventViewModel.getEventsLocation(latlong, radius, "km", "it-it", lastUpdate)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        List<Event> events = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                        for (Event event : events) {
                            Log.d(TAG, "Evento completo JSON: " + gson.toJson(event));
                        }
                        eventList.clear();
                        eventList.addAll(events);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(TAG, "Errore fetch eventi: " + ((Result.Error) result));
                    }
                    circularProgressIndicator.setVisibility(View.GONE);
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //assicura che la bottom navigation sia visibile
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //gestione risultato richiesta permessi
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndEvents();
            } else {
                double lat = 45.464098; //usa coordinate predefinite se il permesso è negato
                double lon = 9.191926;
                latlong = lat + "," + lon;
                GeoUtils.getCityNameAsync(requireContext(), lat, lon, getView());
                fetchEvents();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
