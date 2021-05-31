package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class RecipeActivity extends AppCompatActivity {
    private String name,author,date,location,category,desc,image,ingredients,steps;
    private TextView nameTv, authorTv, dateTv, locationTv, categoryTv, descTv, ingredientsTv, stepsTv, backBtn;
    private ImageView imageIV;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        author = intent.getStringExtra("author");
        date = intent.getStringExtra("date");
        location = intent.getStringExtra("location");
        category = intent.getStringExtra("category");
        desc = intent.getStringExtra("desc");
        image = intent.getStringExtra("image");
        ingredients = intent.getStringExtra("ingredients");
        steps = intent.getStringExtra("steps");

        this.nameTv = findViewById(R.id.recipeTitleTvAR);
        this.authorTv = findViewById(R.id.recipeAuthorTvAR);
        this.dateTv = findViewById(R.id.recipeDateTvAR);
        this.locationTv = findViewById(R.id.locationTvAR);
        this.categoryTv = findViewById(R.id.categoryTvAR);
        this.descTv = findViewById(R.id.recipeDescTvAR);
        this.ingredientsTv = findViewById(R.id.ingredientsTvAAR);
        this.stepsTv = findViewById(R.id.recipeStepsTvAR);
        this.imageIV = findViewById(R.id.foodImgIvAR);

        this.backBtn = findViewById(R.id.backBtnAR);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                RecipeActivity.this.startActivity(intent);
            }
        });

        nameTv.setText(name);
        authorTv.setText("Author: " + author);
        dateTv.setText(date);
        locationTv.setText("Location: " + location);
        categoryTv.setText("Category: "+ category);
        descTv.setText(desc);
        ingredientsTv.setText(ingredients);
        stepsTv.setText(steps);
        setImage(image);

    }

    /**
     * Loads and sets the image inside the imageview
     *
     * @param imageName  A string of the name of the image
     */
    public void setImage(String imageName) {
        // With the storageReference, get the image based on its name
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = this.storageReference.child(imageName);
        Log.d("img ref", String.valueOf(imageRef));
        // Download the image and display via Picasso accordingly
        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Log.d("Debug", "onComplete: got image");
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(imageIV);
                } else {
                    Log.d("Debug", "onComplete: did not get image");
                }
            }
        });
    }
}