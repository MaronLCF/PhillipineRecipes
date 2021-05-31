package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register;
    private EditText loginEmail, loginPassword;
    private Button signIn;
    private Boolean isLogged = false;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        register = (TextView) findViewById(R.id.registerTvL);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.loginBtnL);
        signIn.setOnClickListener(this);

        loginEmail = (EditText) findViewById(R.id.emailEtL);
        loginPassword = (EditText) findViewById(R.id.passwordEtL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerTvL:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.loginBtnL:
                userLogin();
                break;
        }
    }

    /**
     * Logs in the user after inputting the email and password and checks in the user if the
     * authentication is successful.
     *
     */
    private void userLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if(email.isEmpty()){
            loginEmail.setError("Email is required!");
            loginEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please enter a valid email!");
            loginEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            loginPassword.setError("Password is required!");
            loginPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            loginPassword.setError("Password is too short!");
            loginPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    saveLoginToSP(email,password);
                    isLogged = true;
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    LoginActivity.this.startActivity(intent);
                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this,"Failed to login. Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Saves the email and password to shared preferences.
     *
     */
    private void saveLoginToSP(String email, String password){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String gson1 = gson.toJson(email);
        String gson2 = gson.toJson(password);

        editor.putString("gson1",gson1);
        editor.putString("gson2",gson2);

        editor.apply();
    }
}