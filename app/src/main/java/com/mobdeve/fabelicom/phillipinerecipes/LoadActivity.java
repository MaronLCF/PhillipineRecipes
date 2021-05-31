package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class LoadActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mAuth = FirebaseAuth.getInstance();
        readSPandLogin();
    }

    /**
     * Reads the shared preference if there is an existing sp and logs in if it exists.
     *
     */
    private void readSPandLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String gson1 = sharedPreferences.getString("gson1", null);
        String gson2 = sharedPreferences.getString("gson2", null);

        String sgson1 = gson.fromJson(gson1, String.class);
        String sgson2 = gson.fromJson(gson2, String.class);

        Log.d("Email",String.valueOf(sgson1));
        Log.d("Password",String.valueOf(sgson2));

        if(sgson1!=null && sgson2!=null){
            mAuth.signInWithEmailAndPassword(sgson1, sgson2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        LoadActivity.this.startActivity(intent);
                        //startActivity(new Intent(LoadActivity.this, MainActivity.class));
                    }else{
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        LoadActivity.this.startActivity(intent);
                    }
                }
            });
        } else{
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            LoadActivity.this.startActivity(intent);
        }
    }
}