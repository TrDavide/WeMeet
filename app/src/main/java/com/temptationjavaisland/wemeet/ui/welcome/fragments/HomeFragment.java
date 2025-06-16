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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.EventAPIRepository;
import com.temptationjavaisland.wemeet.repository.IEventRepository;
import com.temptationjavaisland.wemeet.util.ResponseCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.location.Geocoder;

public class HomeFragment extends Fragment implements ResponseCallBack {

    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    public static final String TAG = HomeFragment.class.getName();
    private RecyclerView recyclerView;
    private EventRecyclerAdapter adapter;
    private IEventRepository eventRepository;

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHome);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        eventRepository = new EventAPIRepository(requireActivity().getApplication(), this);

        Bundle args = getArguments();
        if (args != null) {
            double lat = args.getDouble("lat", 0); //45.464003;
            double lon = args.getDouble("lon", 0);  //9.189664;

            getCityNameAsync(lat, lon, view);

            String latlong = lat + "," + lon;
            int radius = 300;
            eventRepository.fetchEventsByLocation(latlong, radius, System.currentTimeMillis());
        }

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList, event -> {
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
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);

        return view;
    }

    private void getCityNameAsync(double lat, double lon, View rootView) {
        TextView cityTextView = rootView.findViewById(R.id.cityTextView);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String city = address.getLocality();
                        if (city == null) {
                            city = address.getSubAdminArea();
                        }
                        return city;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "N/A";
            }

            @Override
            protected void onPostExecute(String city) {
                cityTextView.setText(city);
            }
        }.execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
