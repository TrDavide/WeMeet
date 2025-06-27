package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import static com.temptationjavaisland.wemeet.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.temptationjavaisland.wemeet.util.Constants.INVALID_USER_ERROR;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.model.User;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private TextInputEditText editTextEmail, editTextPassword;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

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

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                        auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity(), task -> {
                                    if (task.isSuccessful()) {
                                        // Login avvenuto con successo, utente registrato in Firebase
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        Log.i(TAG, "Google sign-in success: " + firebaseUser.getEmail());

                                        // Qui puoi eventualmente creare un tuo User e salvarlo nel database
                                        // oppure navigare alla Home direttamente:
                                        Navigation.findNavController(requireView())
                                                .navigate(R.id.action_loginFragment_to_homePageActivity);

                                    } else {
                                        Log.w(TAG, "Google sign-in failed", task.getException());
                                        Snackbar.make(requireView(), "Login con Google fallito", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
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

        if (userViewModel.getLoggedUser() != null) {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homePageActivity);
        }

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
        Button loginGoogleButton = view.findViewById(R.id.loginGoogleButton);
        loginGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));

    }

    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }

}
