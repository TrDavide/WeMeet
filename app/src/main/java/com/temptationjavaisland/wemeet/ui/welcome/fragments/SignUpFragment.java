package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import static com.temptationjavaisland.wemeet.util.Constants.USER_COLLISION_ERROR;
import static com.temptationjavaisland.wemeet.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.model.Result;
import com.temptationjavaisland.wemeet.model.User;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

import org.apache.commons.validator.routines.EmailValidator;

public class SignUpFragment extends Fragment {


    public static final String TAG = SignUpFragment.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextConfermaPassword, editTextPassword;
    private BeginSignInRequest signInRequest;
    private UserViewModel userViewModel;

    public SignUpFragment() {}

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //recupero dell'implementazione concreta del repository utente tramite ServiceLocator
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        //inizializzo il ViewModel utente con la factory specifica
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        //inizialmente non ci sono errori di autenticazione
        userViewModel.setAuthenticationError(false);

        //configuro la richiesta OneTap per supportare password e token Google
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        editTextEmail = view.findViewById(R.id.inputTextEmail);
        editTextPassword = view.findViewById(R.id.inputTextPassword);
        editTextConfermaPassword = view.findViewById(R.id.inputTextConfermaPassword);

        //imposto limiti di lunghezza sugli input per evitare dati troppo lunghi
        editTextEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
        editTextPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        editTextConfermaPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

        Button arrowBackButton = view.findViewById(R.id.arrowBackWelcome);
        Button registerButton = view.findViewById(R.id.registerButton);

        //listener per il bottone "torna indietro": naviga al fragment di benvenuto
        arrowBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_welcomeFragment);
        });

        //listener per il bottone "registrati"
        registerButton.setOnClickListener(v -> {
            //recupero i dati inseriti dall'utente, con trim per rimuovere spazi
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confermaPassword = editTextConfermaPassword.getText().toString().trim();

            if(isEmailOk(editTextEmail.getText().toString()) && editTextEmail.getText() != null){
                if(editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())){
                    if(checkPasswords()){
                        if (!userViewModel.isAuthenticationError()) {
                            //osservo il LiveData per il risultato della registrazione
                            userViewModel.getUserMutableLiveData(email, password, false).observe(
                                    getViewLifecycleOwner(), result -> {
                                        if (result.isSuccess()) {
                                            //registrazione avvenuta con successo, recupero l'utente
                                            User user = ((Result.UserSuccess) result).getData();
                                            userViewModel.setAuthenticationError(false);

                                            //navigo alla home page dell'app
                                            Navigation.findNavController(view).navigate(
                                                    R.id.action_signUpFragment_to_homePageActivity);
                                        } else {
                                            //in caso di errore, setto errore di autenticazione e mostro messaggio all'utente
                                            userViewModel.setAuthenticationError(true);
                                            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                    getErrorMessage(((Result.Error) result).getMessage()),
                                                    Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            userViewModel.getUser(email, password, false);
                        }

                        Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_homePageActivity);
                    }else{
                        editTextConfermaPassword.setError("Le password non corrispondono");
                        userViewModel.setAuthenticationError(true);
                        Snackbar.make(view, "Reinserisci la password", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    //password non valida troppo corta
                    userViewModel.setAuthenticationError(true);
                    Snackbar.make(view, "Inserisci una password corretta", Snackbar.LENGTH_SHORT).show();
                }
            }else{
                //email non valida
                userViewModel.setAuthenticationError(true);
                Snackbar.make(view, "Inserisci una mail corretta", Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //restituisce un messaggio di errore leggibile per l'utente, a partire dal codice errore interno.
    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_collision_user);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }


    //controlla se l'email inserita è formalmente valida.
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }


    //controlla che la password abbia almeno 8 caratteri.
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }


    //controlla che la password e la password di conferma coincidano.
    private boolean checkPasswords() {
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfermaPassword.getText().toString().trim();

        return password.equals(confirmPassword);
    }

}
