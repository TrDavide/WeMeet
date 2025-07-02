package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import static com.temptationjavaisland.wemeet.util.Constants.FIREBASE_REALTIME_DATABASE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.temptationjavaisland.wemeet.R;
import com.temptationjavaisland.wemeet.repository.User.IUserRepository;
import com.temptationjavaisland.wemeet.source.User.UserFirebaseDataSource;
import com.temptationjavaisland.wemeet.ui.welcome.WelcomeActivity;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModel;
import com.temptationjavaisland.wemeet.ui.welcome.viewmodel.user.UserViewModelFactory;
import com.temptationjavaisland.wemeet.util.ServiceLocator;

public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    BottomNavigationView bottomNavigationView;
    private UserViewModel userViewModel;
    private EventViewModel eventViewModel;
    private static final int RC_GOOGLE_REAUTH = 123; //codice per Google reauth
    private String passwordForReauth = null;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    //inizializzazione ViewModel
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //recupera il repository utente tramite ServiceLocator
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository))
                .get(UserViewModel.class);

        //inizializzazione EventViewModel
        eventViewModel = new ViewModelProvider(
                requireActivity(),
                new com.temptationjavaisland.wemeet.ui.welcome.viewmodel.event.EventViewModelFactory(
                        ServiceLocator.getInstance().getEventRepository(
                                requireActivity().getApplication(),
                                requireActivity().getResources().getBoolean(R.bool.debug_mode)
                        )
                )
        ).get(EventViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton logoutButton = view.findViewById(R.id.bottone_logout);
        MaterialButton temaButton = view.findViewById(R.id.bottone_tema);
        MaterialButton deleteProfileButton = view.findViewById(R.id.bottone_elimina_profilo);
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //mostra l’email dell’utente loggato
        if (currentUser != null && currentUser.getEmail() != null) {
            String userEmail = currentUser.getEmail();
            emailTextView.setText("Sei loggato con:\n " + userEmail);
        }

        //rende visibile la barra di navigazione inferiore
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        //logout utente
        logoutButton.setOnClickListener(v -> {
            if (eventViewModel != null) eventViewModel.clearLocalEvents();
            FirebaseAuth.getInstance().signOut();
            startWelcomeActivity();
        });

        //imposta il testo del pulsante in base al tema corrente
        boolean isNightMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) ||
                ((AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        && (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES);

        temaButton.setText(isNightMode ? "Tema: Notte" : "Tema: Giorno");

        //toggle tra tema giorno e notte
        temaButton.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
            requireActivity().recreate();
        });

        //azione di eliminazione profilo
        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    //mostra dialog di conferma per eliminare il profilo
    private void showDeleteConfirmationDialog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showError("Nessun utente autenticato.");
            return;
        }

        //determina il provider
        String providerId = user.getProviderData().get(1).getProviderId();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Conferma eliminazione")
                .setMessage("Sei sicuro di voler eliminare il tuo profilo?")
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Elimina", (dialog, which) -> {
                    if ("google.com".equals(providerId)) {
                        reauthenticateWithGoogle(); //avvia la riautenticazione con Google
                    } else if ("password".equals(providerId)) {
                        showPasswordPrompt(); //chiede la password
                    } else {
                        showError("Provider non supportato: " + providerId);
                    }
                })
                .show();
    }

    //eliminazione account con email e password
    private void deleteUserAccount(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null || password == null || password.isEmpty()) {
            showError("Dati utente mancanti.");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        //riautenticazione prima dell’eliminazione
        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                proceedToDeleteUserAccount(user);
            } else {
                showError("Password errata o riautenticazione fallita.");
            }
        });
    }

    //avvia WelcomeActivity e chiude le precedenti
    private void startWelcomeActivity() {
        Intent intent = new Intent(requireContext(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //mostra messaggio di errore
    private void showError(String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    //avvia flusso di accesso Google per riautenticazione
    private void reauthenticateWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //ID token corretto da Firebase console
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_REAUTH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_REAUTH) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {
                                proceedToDeleteUserAccount(user);
                            } else {
                                showError("Riautenticazione con Google fallita.");
                            }
                        });
                    }
                } else {
                    showError("Accesso Google non riuscito.");
                }
            } catch (ApiException e) {
                showError("Errore Google Sign-In: " + e.getMessage());
            }
        }
    }

    //elimina l’account utente e i suoi dati dal database
    private void proceedToDeleteUserAccount(FirebaseUser user) {
        String userId = user.getUid();
        UserFirebaseDataSource userDataSource = new UserFirebaseDataSource();

        userDataSource.deleteUserData(userId,
                aVoid -> user.delete().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        if (eventViewModel != null) eventViewModel.clearLocalEvents();
                        FirebaseAuth.getInstance().signOut();
                        startWelcomeActivity();
                    } else {
                        showError("Errore durante l'eliminazione dell'account.");
                    }
                }),
                e -> showError("Errore durante l'eliminazione dei dati utente dal database.")
        );
    }

    //mostra dialog per richiedere la password all’utente
    private void showPasswordPrompt() {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Inserisci la password");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Conferma password")
                .setMessage("Inserisci la tua password per confermare l'eliminazione:")
                .setView(input)
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Conferma", (dialog, which) -> {
                    String password = input.getText().toString();
                    deleteUserAccount(password);
                })
                .show();
    }
}
