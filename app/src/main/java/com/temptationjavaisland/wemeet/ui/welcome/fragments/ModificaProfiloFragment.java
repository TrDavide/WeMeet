package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.temptationjavaisland.wemeet.R;

public class ModificaProfiloFragment extends Fragment {

    public ModificaProfiloFragment() {}

    public static ModificaProfiloFragment newInstance() {
        return new ModificaProfiloFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modifica_profilo, container, false);
        return view;
    }
}
