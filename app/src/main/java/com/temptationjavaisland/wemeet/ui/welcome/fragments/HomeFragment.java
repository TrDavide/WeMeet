package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.repository.EventRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.EventViewModelFactory;
import com.temptationjavaisland.wemeet.util.NetworkUtil;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getName();

    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    private RecyclerView recyclerView;
    private EventRecyclerAdapter adapter;
    private EventViewModel eventViewModel;
    private FrameLayout noInternetView;
    private static final int radius = 1;
    private String latlong;
    private Long lastUpdate;

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventRepository eventRepository =
                ServiceLocator.getInstance().getEventRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(eventRepository)).get(EventViewModel.class);

        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetView = view.findViewById(R.id.noInternetMessage);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList,
                new EventRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onEventItemClick(Event event) {
                        EventPageFragment eventPageFragment = new EventPageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event_data", event);
                        eventPageFragment.setArguments(bundle);

                        FragmentTransaction transaction = requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();
                        transaction.replace(R.id.fragmentContainerView, eventPageFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        eventList.get(position).setSaved(!eventList.get(position).isSaved());
                        eventViewModel.updateEvent(eventList.get(position));
                    }
                });

        lastUpdate = System.currentTimeMillis();

        Bundle args = getArguments();
        if (args != null) {
            double lat = 45.4642; //args.getDouble("lat", 45.4642);
            double lon = 9.1900; //args.getDouble("lon", 9.1900);
            getCityNameAsync(lat, lon, view);
            latlong = lat + "," + lon;
        } /*else {
            latlong = "0,0"; // fallback
        }*/

        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);
        noInternetView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);

        if (!NetworkUtil.isInternetAvailable(getContext())) {
            noInternetView.setVisibility(View.VISIBLE);
            circularProgressIndicator.setVisibility(View.GONE);
            lastUpdate = System.currentTimeMillis() + 100; // evita chiamata API
        }

        Gson gson = new Gson();

        eventViewModel.getEventsLocation(latlong, 20, "km", "it-it", 0L)
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
                    }
                });

        return view;
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private void getCityNameAsync(double lat, double lon, View rootView) {
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
            mainHandler.post(() -> cityTextView.setText(finalCity));
        });
    }
}