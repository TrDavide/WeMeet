package com.temptationjavaisland.wemeet.ui.welcome.fragments;

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
import com.temptationjavaisland.wemeet.R;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.outlinedTextField);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            if(isEmailOk(editTextEmail.getText().toString())){
                if(isPasswordOk(editTextPassword.getText().toString())){
                    //Intent intent = new Intent(this, CasaPaginaActivity.class); Usi queste due righe di codice senza il navigation
                    //startActivity(intent);

                    Navigation.findNavController(v).navigate(R.id.action_welcomeFragment_to_loginFragment); //cambio pagina con navigation
                }
                else{
                    editTextPassword.setError("The password must have at least 8 chars");
                    Snackbar.make(v.findViewById(android.R.id.content), "Check your password", Snackbar.LENGTH_SHORT)
                            .show();
                }
                //Log.d(TAG, "Launch new activity");
            }
            else{
                //Log.d(TAG, "Error");
                editTextEmail.setError("Check your email");
                Snackbar.make(v.findViewById(android.R.id.content), "Insert a correct email", Snackbar.LENGTH_SHORT)
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