package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import static com.temptationjavaisland.wemeet.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.temptationjavaisland.wemeet.util.Constants.INVALID_USER_ERROR;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.preference.PreferenceManager;
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
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ottieni il repository e il ViewModel dell'utente
        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)
        ).get(UserViewModel.class);

        //inizializza il client per One Tap Google Sign-in
        oneTapClient = Identity.getSignInClient(requireActivity());

        //costruisci la richiesta di login (email/password + Google)
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                                .setSupported(true)
                                .build()
                )
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                )
                .setAutoSelectEnabled(true)
                .build();

        //launcher per gestire il risultato dell'intent Google SignIn
        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();
        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();

                    if (idToken != null) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                        //effettua il login con Firebase
                        auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity(), task -> {
                                    if (task.isSuccessful()) {
                                        //login Google riuscito
                                        FirebaseUser firebaseUser = auth.getCurrentUser();

                                        //salva la password nel SharedPreferences
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
                                        prefs.edit().putString("user_password", editTextPassword.getText().toString().trim()).apply();

                                        Log.i(TAG, "Google sign-in success: " + firebaseUser.getEmail());

                                        //naviga alla homepage
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

    //restituisce il messaggio d'errore a seconda del tipo di errore
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //se l'utente è già loggato va direttamente alla homepage
        if (userViewModel.getLoggedUser() != null) {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homePageActivity);
        }

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        //limita la lunghezza massima dell'input
        editTextEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
        editTextPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

        Button arrowBackButton = view.findViewById(R.id.arrowBackLogin);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button loginGoogleButton = view.findViewById(R.id.loginGoogleButton);

        //pulsante "indietro" che torna al welcome
        arrowBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_welcomeFragment);
        });

        //login tradizionale email e password
        loginButton.setOnClickListener(v -> {
            if (editTextEmail.getText() != null && isEmailOk(editTextEmail.getText().toString())) {
                if (editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            editTextEmail.getText().toString().trim(),
                            editTextPassword.getText().toString().trim()
                    ).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //login email e password riuscito
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homePageActivity);
                            }
                        } else {
                            Snackbar.make(view, "Credenziali errate o utente non registrato", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Snackbar.make(view, "Inserisci una password corretta", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(view, "Inserisci una mail corretta", Snackbar.LENGTH_SHORT).show();
            }
        });

        //login con Google
        loginGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        //se la richiesta ha successo lancia l’intent per la selezione account
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //nessun account salvato trovato o altro errore
                        Log.d(TAG, e.getLocalizedMessage());
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));
    }

    //valida email con Apache Commons Validator
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    //la password deve essere lunga almeno 8 caratteri
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }

}
