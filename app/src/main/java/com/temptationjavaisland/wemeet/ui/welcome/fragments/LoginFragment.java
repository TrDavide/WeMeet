package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.temptationjavaisland.wemeet.HomePageActivity;
import com.temptationjavaisland.wemeet.R;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            if(isEmailOk(editTextEmail.getText().toString()) && editTextEmail.getText() != null/*editTextEmail.getText() != null && isEmailOk(editTextEmail.getText().toString())*/){
                if(editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())){
                    /*Intent intent = new Intent(getActivity(), HomePageActivity.class);
                    try {
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){

                    }*/
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homePageActivity); //cambio pagina con navigation
                } else{
                    //editTextPassword.setError("The password must have at least 8 chars");
                    Snackbar.make(view, "Inserisci una password corretta", Snackbar.LENGTH_SHORT)
                            .show();
                }

            } else{

                //editTextEmail.setError("Check your email");
                Snackbar.make(view, "Inserisci una mail corretta", Snackbar.LENGTH_SHORT)
                        .show(); // content: restituisce il primo elemento del layout, quindi in questo caso LinearLayout (gli viene assegnato un id)
            }
        });
    }

    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }

}
