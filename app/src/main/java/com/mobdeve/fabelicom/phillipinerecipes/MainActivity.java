package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private Button addRecipe, yourRecipeBtn;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private String userID;
    public BottomNavigationView bottomNavigationView;
    private boolean fromAcc = false;

    private ArrayList<Recipe> feed = new ArrayList();
    private ArrayList<Recipe> yourRecipe = new ArrayList();

    private RecyclerView homeRv;
    private MyAdapter myAdapter;
    DatabaseReference dbreference;
    FirebaseDatabase fbDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.homeRv = findViewById(R.id.feedRV);
        this.homeRv.setLayoutManager(new LinearLayoutManager(this));
        this.myAdapter = new MyAdapter(feed);
        this.homeRv.setAdapter(this.myAdapter);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            fromAcc = true;
        }

        this.firebaseDatabase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        addRecipe = (Button) findViewById(R.id.addRecipeBtn);
        yourRecipeBtn = (Button) findViewById(R.id.savedRecipeBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        myAdapter.setInHome(true);
        if (fromAcc){
            bottomNavigationView.setSelectedItemId(R.id.favoritesBtn);
        }else {
            bottomNavigationView.setSelectedItemId(R.id.hotBtn);
        }

        if (fromAcc == true){
            myAdapter.setInFavorites(false);
            myAdapter.setFromYourRecipe(false);
            myAdapter.setInHome(false);
            homeRv.getRecycledViewPool().clear();
            myAdapter.notifyDataSetChanged();
            this.fbDatabase = FirebaseDatabase.getInstance();
            this.dbreference = this.fbDatabase.getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Favorites");
            Log.d("mainref", String.valueOf(reference));
            feed.clear();

            this.dbreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> keys = new ArrayList<>();
                    for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        keys.add(keyNode.getKey());
                        Recipe rec = keyNode.getValue(Recipe.class);
                        feed.add(0, rec);
                        Log.d("Debug", "onDataChange: " + rec.getImage_name() + " " + rec.getName() + " ");
                        // Once done loading data, notify the adapter that data has loaded in
                        myAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Debug", "onDataChange: canceled");
                }
            });
        }else {
            this.reference = this.firebaseDatabase.getReference("Recipe");
            Log.d("mainref", String.valueOf(reference));
            myAdapter.setInFavorites(false);
            myAdapter.setFromYourRecipe(false);
            myAdapter.setInHome(true);
            this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> keys = new ArrayList<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        keys.add(keyNode.getKey());
                        Recipe rec = keyNode.getValue(Recipe.class);
                        feed.add(0, rec);
                        Log.d("Debug", "onDataChange: " + rec.getImage_name() + " " + rec.getName() + " ");
                        // Once done loading data, notify the adapter that data has loaded in
                        myAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Debug", "onDataChange: canceled");
                }
            });
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            DatabaseReference dbreference;
            FirebaseDatabase fbDatabase;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.hotBtn:
                        myAdapter.setInFavorites(false);
                        myAdapter.setFromYourRecipe(false);
                        myAdapter.setInHome(true);
                        homeRv.getRecycledViewPool().clear();
                        myAdapter.notifyDataSetChanged();
                        this.fbDatabase = FirebaseDatabase.getInstance();
                        this.dbreference = this.fbDatabase.getReference("Recipe");
                        Log.d("mainref", String.valueOf(reference));
                        feed.clear();

                        this.dbreference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<String> keys = new ArrayList<>();
                                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                                    keys.add(keyNode.getKey());
                                    Recipe rec = keyNode.getValue(Recipe.class);
                                    feed.add(0, rec);
                                    Log.d("Debug", "onDataChange: " + rec.getImage_name() + " " + rec.getName() + " ");
                                    // Once done loading data, notify the adapter that data has loaded in
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Debug", "onDataChange: canceled");
                            }
                        });
                        return true;
                    case R.id.favoritesBtn:
                        myAdapter.setInFavorites(true);
                        myAdapter.setFromYourRecipe(false);
                        myAdapter.setInHome(false);
                        homeRv.getRecycledViewPool().clear();
                        myAdapter.notifyDataSetChanged();
                        this.fbDatabase = FirebaseDatabase.getInstance();
                        this.dbreference = this.fbDatabase.getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Favorites");
                        Log.d("mainref", String.valueOf(reference));
                        feed.clear();

                        this.dbreference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<String> keys = new ArrayList<>();
                                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                                    keys.add(keyNode.getKey());
                                    Recipe rec = keyNode.getValue(Recipe.class);
                                    feed.add(0, rec);
                                    Log.d("Debug", "onDataChange: " + rec.getImage_name() + " " + rec.getName() + " ");
                                    // Once done loading data, notify the adapter that data has loaded in
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Debug", "onDataChange: canceled");
                            }
                        });
                        return true;
                    case R.id.accountBtn:
                        startActivity(new Intent(MainActivity.this, AccountActivity.class));
                        return true;
                }
                return false;
            }
        });


        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddEditActivity.class));
            }
        });


        yourRecipeBtn.setOnClickListener(new View.OnClickListener() {
            private DatabaseReference dbreference;
            private FirebaseDatabase fbDatabase;
            @Override
            public void onClick(View v) {
                homeRv.getRecycledViewPool().clear();
                myAdapter.notifyDataSetChanged();
                this.fbDatabase = FirebaseDatabase.getInstance();
                this.dbreference = this.fbDatabase.getReference("Recipe");
                Log.d("mainref", String.valueOf(dbreference));
                feed.clear();
                myAdapter.setFromYourRecipe(true);
                myAdapter.setInHome(false);
                this.dbreference.orderByChild("authorUID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> keys = new ArrayList<>();
                        for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                            keys.add(keyNode.getKey());
                            Recipe rec = keyNode.getValue(Recipe.class);
                            feed.add(0, rec);
                            Log.d("Debug", "onDataChange: " + rec.getImage_name() + " " + rec.getName() + " ");
                            // Once done loading data, notify the adapter that data has loaded in
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Debug", "onDataChange: canceled");
                    }
                });
            }
        });
    }
}