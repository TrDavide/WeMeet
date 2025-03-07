package com.temptationjavaisland.wemeet.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.temptationjavaisland.wemeet.R;
import com.google.android.material.textfield.TextInputEditText;
import org.apache.commons.validator.routines.EmailValidator;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getName();
    private TextInputEditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        /*editTextEmail = findViewById(R.id.textInputEmail);
        editTextPassword = findViewById(R.id.textInputPassword);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            if(isEmailOk(editTextEmail.getText().toString())){
                if(isPasswordOk(editTextPassword.getText().toString())){
                    //Intent intent = new Intent(this, .class);
                    //startActivity(intent);
                    Log.d("suca", "password ok");
                    //Navigation.findNavController(v).navigate(R.id.action_welcomeFragment_to_loginFragment); //cambio pagina con navigation
                }
                else{
                    editTextPassword.setError("The password must have at least 8 chars");
                    Snackbar.make(findViewById(android.R.id.content), "Check your password", Snackbar.LENGTH_SHORT)
                            .show();
                }
                //Log.d(TAG, "Launch new activity");
            }
            else{
                //Log.d(TAG, "Error");
                editTextEmail.setError("Check your email");
                Snackbar.make(findViewById(android.R.id.content), "Insert a correct email", Snackbar.LENGTH_SHORT)
                        .show(); // content: restituisce il primo elemento del layout, quindi in questo caso LinearLayout (gli viene assegnato un id)
            }
        });
    }

    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }*/
    }
}