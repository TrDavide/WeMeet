package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.EventAPIRepository;
import com.temptationjavaisland.wemeet.repository.EventRepository;
import com.temptationjavaisland.wemeet.repository.IEventRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.EventViewModelFactory;
import com.temptationjavaisland.wemeet.util.NetworkUtil;
import com.temptationjavaisland.wemeet.util.ResponseCallBack;
import com.temptationjavaisland.wemeet.util.ServiceLocator;
import com.temptationjavaisland.wemeet.model.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.location.Geocoder;
import android.widget.LinearLayout;


public class HomeFragment extends Fragment implements ResponseCallBack {

    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    public static final String TAG = HomeFragment.class.getName();
    private RecyclerView recyclerView;
    private EventRecyclerAdapter adapter;
    private IEventRepository eventRepository;
    private EventViewModel eventViewModel;
    private LinearLayout shimmerLinearLayout;
    private FrameLayout noInternetView;
    private static final int initialShimmerElements = 5;
    private String latlong;
    int radius = 300;
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

        shimmerLinearLayout = view.findViewById(R.id.shimmerLinearLayout);
        noInternetView = view.findViewById(R.id.noInternetMessage);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        eventList = new ArrayList<>();
        for (int i = 0; i < initialShimmerElements; i++) eventList.add(Event.getSampleEvent());
        //eventRepository = new EventAPIRepository(requireActivity().getApplication(), this);

        Bundle args = getArguments();
        if (args != null) {
            double lat = args.getDouble("lat", 0); //45.464003;
            double lon = args.getDouble("lon", 0);  //9.189664;

            getCityNameAsync(lat, lon, view);

            latlong = lat + "," + lon;

            //eventRepository.fetchEventsLocation(latlong, radius, System.currentTimeMillis());
        }

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList,
                new EventRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onEventClick(Event event) {
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

        if (!NetworkUtil.isInternetAvailable(getContext())) {
            noInternetView.setVisibility(View.VISIBLE);
            //Trick to avoid doing the API call
            lastUpdate = Long.valueOf(System.currentTimeMillis() + "");
        }


        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        eventViewModel.getEventsLocation(latlong, radius, "", "", lastUpdate)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Result.Success successResult = (Result.Success) result;
                        com.temptationjavaisland.wemeet.model.EventAPIResponse data = successResult.getData();
                        if (data != null && data.getEmbedded() != null && data.getEmbedded().getEvents() != null) {
                            int initialSize = this.eventList.size();
                            this.eventList.clear();
                            this.eventList.addAll(data.getEmbedded().getEvents());
                            adapter.notifyItemRangeInserted(initialSize, this.eventList.size());
                            recyclerView.setVisibility(View.VISIBLE);
                            shimmerLinearLayout.setVisibility(View.GONE);
                        } else {
                            Snackbar.make(requireView(), "Nessun evento trovato", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(requireView(),
                                getString(R.string.error_retrieving_events),
                                Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        SearchBar searchBar = view.findViewById(R.id.search_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onSuccess(List<Event> newEventsList, long lastUpdate) {
        eventList.clear();
        eventList.addAll(newEventsList);

        requireActivity().runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            circularProgressIndicator.setVisibility(View.GONE);
        });

        if (!newEventsList.isEmpty()) {
            Log.i(TAG, newEventsList.get(0).toString());
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        requireActivity().runOnUiThread(() ->
                Snackbar.make(requireView(), "Errore nel caricamento eventi: " + errorMessage, Snackbar.LENGTH_LONG).show()
        );
        circularProgressIndicator.setVisibility(View.GONE);
    }
}