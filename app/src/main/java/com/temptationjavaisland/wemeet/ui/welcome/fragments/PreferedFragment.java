package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class PreferedFragment extends Fragment {

    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private List<Event> eventList;
    private EventRecyclerAdapter adapter;
    private EventViewModel eventViewModel;
    public PreferedFragment() {}

    public static PreferedFragment newInstance() {
        return new PreferedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventRepository articleRepository =
                ServiceLocator.getInstance().getEventRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );


        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(articleRepository)).get(EventViewModel.class);

        eventList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prefered, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPrefered);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList,
                new EventRecyclerAdapter.OnItemClickListener() {
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
                        eventList.get(position).setSaved(!eventList.get(position).isSaved());
                        eventViewModel.updateEvent(eventList.get(position));
                    }
        });
        recyclerView.setAdapter(adapter);
        loadSavedEvents();

        return view;
    }

    private void loadSavedEvents() {
        new Thread(() -> {
            List<Event> saved = EventRoomDatabase
                    .getDatabase(requireContext())
                    .eventsDao()
                    .getSaved();

            Log.d("DB_TEST", "Eventi salvati trovati: " + saved.size());

            requireActivity().runOnUiThread(() -> {
                eventList.clear();
                eventList.addAll(saved);
                adapter.notifyDataSetChanged();

                recyclerView.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

    }
}
