package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.temptationjavaisland.wemeet.R;

public class HomePageFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    public HomePageFragment() {}

    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        }

        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_home).setIcon(R.drawable.home_active);

        bottomNavigationView.setItemActiveIndicatorEnabled(false);
        bottomNavigationView.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);
        //bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                resetIcons(); // Reimposta le icone allo stato normale
                if(item.getItemId() == R.id.nav_home){
                    item.setIcon(R.drawable.home_active);
                    Navigation.findNavController(view).navigate(R.id.homePageFragment);
                    return true;
                }else if(item.getItemId() == R.id.nav_search){
                    item.setIcon(R.drawable.place_active);
                    Navigation.findNavController(view).navigate(R.id.locationFragment);
                    return true;
                }else if(item.getItemId() == R.id.nav_save){
                    item.setIcon(R.drawable.save_active);
                    Navigation.findNavController(view).navigate(R.id.preferedFragment);
                    return true;
                }else if(item.getItemId() == R.id.nav_profile){
                    item.setIcon(R.drawable.profile_active);
                    Navigation.findNavController(view).navigate(R.id.profileFragment);
                    return true;
                }
                return false;
            }
        });

    }

    private void resetIcons() {
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setIcon(R.drawable.home);
        bottomNavigationView.getMenu().findItem(R.id.nav_search).setIcon(R.drawable.place);
        bottomNavigationView.getMenu().findItem(R.id.nav_save).setIcon(R.drawable.save);
        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setIcon(R.drawable.profile);
    }

}