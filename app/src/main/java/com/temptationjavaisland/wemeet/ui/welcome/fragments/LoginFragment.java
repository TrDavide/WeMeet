package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.temptationjavaisland.wemeet.R;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private TextInputEditText editTextEmail, editTextPassword;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
    }

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

        editTextEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
        editTextPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

        Button arrowBackButton = view.findViewById(R.id.arrowBackLogin);
        Button loginButton = view.findViewById(R.id.loginButton);

        arrowBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_welcomeFragment);
        });

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
