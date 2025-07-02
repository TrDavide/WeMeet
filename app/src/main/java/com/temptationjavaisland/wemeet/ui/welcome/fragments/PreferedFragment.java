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
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.database.EventRoomDatabase;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.repository.Event.EventRepository;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment che mostra gli eventi preferiti dell'utente.
 */
public class PreferedFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Event> eventList;
    private EventRecyclerAdapter adapter;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;


    public PreferedFragment() {}

    public static PreferedFragment newInstance() {
        return new PreferedFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ottiene il repository degli eventi tramite ServiceLocator
        EventRepository eventRepository = ServiceLocator.getInstance().getEventRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        //inizializza l'EventViewModel con la factory appropriata
        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new EventViewModelFactory(eventRepository)).get(EventViewModel.class);

        //ottiene il repository dell'utente e inizializza il relativo ViewModel
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        //inizializza la lista eventi vuota
        eventList = new ArrayList<>();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //rende visibile la bottom navigation se nascosta
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);

        //osserva gli eventi preferiti locali e aggiorna la UI
        eventViewModel.getPreferedEventsLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.EventSuccess) {
                List<Event> localEvents = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                eventList.clear();
                eventList.addAll(localEvents);
                adapter.notifyDataSetChanged();  // Notifica l'adapter per aggiornare la RecyclerView
            }
        });

        //osserva gli eventi preferiti da remoto, salva nel DB locale e aggiorna la UI
        userViewModel.getUserPreferedEventsMutableLiveData(userViewModel.getLoggedUser().getIdToken())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.EventSuccess) {
                        List<Event> remoteEvents = ((Result.EventSuccess) result).getData().getEmbedded().getEvents();
                        eventViewModel.insertEvents(remoteEvents); // salva nel DB

                        eventList.clear(); //aggiorna UI con lista remota
                        eventList.addAll(remoteEvents);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prefered, container, false);

        //configura la RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewPrefered);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //accesso al database (opzionale, sembra non usato direttamente)
        EventRoomDatabase.getDatabase(recyclerView.getContext())
                .eventsDao()
                .getSaved(); // chiamata non collegata a LiveData: potenzialmente da rimuovere o aggiornare

        //inizializza l'adapter e il click listener
        adapter = new EventRecyclerAdapter(R.layout.event_card, eventList,
                new EventRecyclerAdapter.OnItemClickListener() {

                    //quando si clicca su un evento → naviga alla pagina evento
                    @Override
                    public void onEventItemClick(Event event) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event_data", event);

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
                        navController.navigate(R.id.eventPageFragment, bundle);
                    }

                    //quando si preme il bottone preferiti → aggiorna stato e ViewModel
                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        Event event = eventList.get(position);
                        event.setSaved(!event.isSaved()); //inverte stato salvato
                        eventViewModel.updateEvent(event); //aggiorna nel DB

                        //rimuove dai preferiti remoti
                        userViewModel.removeUserPreferedEvent(
                                userViewModel.getLoggedUser().getIdToken(),
                                eventList.get(position).getId()
                        );
                    }
                });

        //imposta l'adapter nella RecyclerView
        recyclerView.setAdapter(adapter);

        return view;
    }
}
