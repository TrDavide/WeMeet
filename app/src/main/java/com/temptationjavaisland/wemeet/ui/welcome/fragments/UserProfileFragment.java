package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.search.SearchBar;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.adapter.EventRecyclerAdapter;
import com.temptationjavaisland.wemeet.model.Event;
import com.temptationjavaisland.wemeet.model.EventAPIResponse;
import com.temptationjavaisland.wemeet.repository.EventRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.EventViewModelFactory;
import com.temptationjavaisland.wemeet.util.Constants;
import com.temptationjavaisland.wemeet.util.JSONParserUtils;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {
    private EventViewModel eventViewModel;
    private List<Event> eventList;
    public UserProfileFragment() {}

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewUserProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            EventAPIResponse response = jsonParserUtils.parserJSONFileWithGsson(Constants.SAMPLE_JSON_FILENAME);
            List<Event> eventList= response.getEmbedded().getEvents();
            EventRecyclerAdapter adapter = new EventRecyclerAdapter(R.layout.event_card, eventList,
                    new EventRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onEventClick(Event event) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("event_data", event); // Assicurati che Event implementi Parcelable
                            Navigation.findNavController(requireView()).navigate(R.id.action_userProfileFragment_to_eventPageFragment, bundle);
                        }
                        @Override
                        public void onFavoriteButtonPressed(int position) {
                            eventList.get(position).setSaved(!eventList.get(position).isSaved());
                            eventViewModel.updateEvent(eventList.get(position));
                        }
            });
            recyclerView.setAdapter(adapter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchBar searchBar = view.findViewById(R.id.search_bar);
        RecyclerView recyclerViewUserProfile = view.findViewById(R.id.recyclerViewUserProfile);
        recyclerViewUserProfile.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }
}
