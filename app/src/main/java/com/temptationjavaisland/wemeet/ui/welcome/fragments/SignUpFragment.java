package com.temptationjavaisland.wemeet.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.temptationjavaisland.wemeet.R;

import org.apache.commons.validator.routines.EmailValidator;

public class SignUpFragment extends Fragment {

    public static final String TAG = SignUpFragment.class.getSimpleName();
    private TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextConfermaPassword, editTextPassword;

    public SignUpFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextNome = view.findViewById(R.id.inputTextNome);
        editTextCognome = view.findViewById(R.id.inputTextCognome);
        editTextEmail = view.findViewById(R.id.inputTextEmail);
        editTextPassword = view.findViewById(R.id.inputTextPassword);
        editTextConfermaPassword = view.findViewById(R.id.inputTextConfermaPassword);

        editTextNome.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        editTextCognome.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        editTextEmail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
        editTextPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        editTextConfermaPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });


        Button arrowBackButton = view.findViewById(R.id.arrowBackWelcome);
        Button registerButton = view.findViewById(R.id.registerButton);

        arrowBackButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_welcomeFragment);
        });

        registerButton.setOnClickListener(v -> {
            if(editTextNome.getText() != null){
                if(editTextCognome.getText() != null){
                    if(isEmailOk(editTextEmail.getText().toString()) && editTextEmail.getText() != null){
                        if(editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())){
                            if(checkPasswords()/*editTextPassword.getText().toString() == editTextConfermaPassword.getText().toString()*/){
                                Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_homePageActivity);
                            }else{
                                editTextConfermaPassword.setError("Le password non corrispondono");
                                Snackbar.make(view, "Reinserisci la password", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }else{
                            Snackbar.make(view, "Inserisci una password corretta", Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }else{
                        Snackbar.make(view, "Inserisci una mail corretta", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }else{
                    Snackbar.make(view, "Inserisci il cognome", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }else{
                Snackbar.make(view, "Inserisci il nome", Snackbar.LENGTH_SHORT)
                        .show();
            }
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            Log.i(TAG, user + "");

            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_homePageActivity);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                //Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }

                    });
        });





    }//fine metodo onViewCreated

    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }

    private boolean checkPasswords() {
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfermaPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            return false;
        }else{
            return true;
        }
    }

}