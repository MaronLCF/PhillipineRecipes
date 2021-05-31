package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    public BottomNavigationView bottomNavigationView;

    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        logout = (Button) findViewById(R.id.logoutBtnA);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.accountBtn);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            DatabaseReference dbreference;
            FirebaseDatabase fbDatabase;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.hotBtn:
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        AccountActivity.this.startActivity(intent);
                        return true;
                    case R.id.favoritesBtn:
                        Intent intent2 = new Intent(AccountActivity.this, MainActivity.class);
                        intent2.putExtra("fromAccount",true);
                        startActivity(intent2);
                        return true;
                    case R.id.accountBtn:
                        return true;
                }
                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                AccountActivity.this.startActivity(intent);
                //startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView usernameTV = (TextView) findViewById(R.id.usernameTvA);
        final TextView emailTV = (TextView) findViewById(R.id.emailTvA);
        final TextView ageTV = (TextView) findViewById(R.id.ageTvA);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String username = userProfile.username;
                    String email = userProfile.email;
                    String age = userProfile.age;

                    usernameTV.setText("Username: " + username);
                    emailTV.setText("Email: " + email);
                    ageTV.setText("Age: " +age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}