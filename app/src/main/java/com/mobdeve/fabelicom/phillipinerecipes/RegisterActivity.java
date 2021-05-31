package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView banner, registerUser;
    private EditText regUsername, regAge, regEmail, regPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.registerTitleTvR);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerBtnR);
        registerUser.setOnClickListener(this);

        regUsername = (EditText) findViewById(R.id.registerUsernameEtR);
        regAge = (EditText) findViewById(R.id.registerAgeEtR);
        regEmail = (EditText) findViewById(R.id.registerEmailEtR);
        regPassword = (EditText) findViewById(R.id.registerPasswordEtR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerTitleTvR:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.registerBtnR:
                registerUser();
        }
    }

    /**
     * Registers the user and uploads the authentication credentials to firebase.
     *
     */
    private void registerUser(){
        String username = regUsername.getText().toString().trim();
        String age = regAge.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();

        if(username.isEmpty()){
            regUsername.setError("Username required!");
            regUsername.requestFocus();
            return;
        }

        if(age.isEmpty()){
            regAge.setError("Age required!");
            regAge.requestFocus();
            return;
        }

        if(email.isEmpty()){
            regEmail.setError("Email required!");
            regEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            regEmail.setError("Email is not Valid");
            regEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            regPassword.setError("Password required!");
            regPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            regPassword.setError("Minimum password length is 6 characters");
            regPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(username,age,email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Registered succesfully, you can now log in.",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        RegisterActivity.this.startActivity(intent);
                                        //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"Registration Failed! Try Again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(RegisterActivity.this,"Registration Failed! Try Again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}