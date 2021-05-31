package com.mobdeve.fabelicom.phillipinerecipes;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicMarkableReference;

import static android.content.ContentValues.TAG;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private ArrayList<Recipe> data;
    private Boolean fromYourRecipe,inHome, inFavorites;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbreference;
    private FirebaseUser user;

    public MyAdapter(ArrayList<Recipe> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.feed_row, parent, false);

        // Create an instance of the ViewHolder with the created ViewGroup
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name,author,date,location,category,desc,image,ingredients,steps;
        ArrayList<String> favorites = new ArrayList<>();


        name = this.data.get(position).getName();
        author = this.data.get(position).getAuthor();
        date = this.data.get(position).getDate();
        location = this.data.get(position).getLocation();
        category = this.data.get(position).getCategory();
        desc = this.data.get(position).getDesc();
        image = this.data.get(position).getImage_name();
        ingredients = this.data.get(position).getIngredients();
        steps = this.data.get(position).getSteps();

        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isFound = false;
                Log.d("NUM OF FAVRECIPES", String.valueOf(dataSnapshot.getChildrenCount()));
                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    Log.d("RECIPE FROM FB", String.valueOf(recipe.getUuid()));
                    Log.d(data.get(position).getName(), data.get(position).getUuid());
                    if(data.get(position).getUuid().equals(recipe.getUuid())){
                        Log.d("Set to filled", String.valueOf(holder.getAdapterPosition()));
                        Log.d("Set to filled", String.valueOf(position));
                        if(holder.getAdapterPosition() == position)
                            isFound = true;
                            //holder.setFavButtonImageFilled();
                        if(!inFavorites)
                            holder.setFavBtnClickable(false);
                        //return;
                    }

                    if(!isFound){
                        holder.setFavButtonImageOutline();
                    } else{
                        holder.setFavButtonImageFilled();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Debug", "onDataChange: canceled");
            }
        });

        holder.setRecipeName(name);
        holder.setRecipeAuthor(author);
        holder.setRecipeDate(date);
        holder.setRecipeLocation("Location: " + location);
        holder.setRecipeCategory("Category: " + category);
        holder.setRecipeDesc(desc);
        holder.setImage(image);

        this.firebaseDatabase = FirebaseDatabase.getInstance();

        if(fromYourRecipe){
            holder.setButtonVisible();
            holder.setFavButton(false);
        }
        else{
            holder.setButtonInvisible();
            holder.setFavButton(true);
        }

        holder.setNameButtonListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecipeActivity.class);

                intent.putExtra("name",name);
                intent.putExtra("author", author);
                intent.putExtra("date", date);
                intent.putExtra("location", location);
                intent.putExtra("category", category);
                intent.putExtra("desc", desc);
                intent.putExtra("image", image);
                intent.putExtra("steps", steps);
                intent.putExtra("ingredients", ingredients);
                v.getContext().startActivity(intent);
            }
        });

        holder.setDeleteButtonListener(new View.OnClickListener(){
            private FirebaseDatabase deleteDatabase;
            private DatabaseReference reference;
            @Override
            public void onClick(View v) {
                deleteDatabase = FirebaseDatabase.getInstance();
                reference = deleteDatabase.getReference("Recipe");
                reference.orderByChild("uuid").equalTo(data.get(position).getUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                            keyNode.getRef().removeValue();
                            Toast.makeText(v.getContext(),"Recipe Deleted!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Debug", "onDataChange: canceled");
                    }
                });
                data.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        holder.setFavoriteButtonListener(new View.OnClickListener(){
            Boolean exists=false;
            @Override
            public void onClick(View v) {
                if(inHome){
                    holder.setFavButtonImageFilled();
                    Recipe favoriteRecipe = new Recipe(data.get(position).getName(),data.get(position).getAuthor(),data.get(position).getDate(),data.get(position).getImage_name(),data.get(position).getCategory(),
                            data.get(position).getLocation(),data.get(position).getDesc(),data.get(position).getAuthorUID(),data.get(position).getIngredients(),data.get(position).getSteps(), data.get(position).getUuid());

                    FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites")//.orderByChild("uuid").equalTo(data.get(position).getUuid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d("UUID OF FAVRECIPES", String.valueOf(dataSnapshot.getChildrenCount()));
                                    for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                                        Recipe recipe = keyNode.getValue(Recipe.class);
                                        Log.d("UUID OF FAVRECIPES", String.valueOf(recipe.getUuid()));
                                        Log.d("UUID DATA", data.get(position).getUuid());
                                        if(data.get(position).getUuid().equals(recipe.getUuid())){
                                            exists = true;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("Debug", "onDataChange: canceled");
                                }
                            });

                    if(!exists){
                        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Favorites").push().setValue(favoriteRecipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(v.getContext(),"Added to Favorites",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(v.getContext(),"Failed to favorite! Try Again!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(v.getContext(),"Failed to favorite already exists! Try Again!", Toast.LENGTH_LONG).show();
                    }



                }
                else{
                    Log.d(TAG, "onClick: not in home press");
                    FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites").orderByChild("uuid").equalTo(data.get(position).getUuid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("Get Children", String.valueOf(dataSnapshot.getValue()));
                            for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                                keyNode.getRef().removeValue();
                                Toast.makeText(v.getContext(),"Deleted from Favorites",Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Debug", "onDataChange: canceled");
                        }
                    });
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            }
        });

        holder.setEditButtonListener(new View.OnClickListener(){
            //private FirebaseDatabase firebaseDatabase2;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddEditActivity.class);
                intent.putExtra("name",data.get(position).getName());
                intent.putExtra("category",data.get(position).getCategory());
                intent.putExtra("location",data.get(position).getLocation());
                intent.putExtra("image_name",data.get(position).getImage_name());
                intent.putExtra("ingredients",data.get(position).getIngredients());
                intent.putExtra("steps",data.get(position).getSteps());
                intent.putExtra("desc",data.get(position).getDesc());
                intent.putExtra("uuid",data.get(position).getUuid());
                view.getContext().startActivity(intent);
            }
        });
    }

    /**
     * Gets the size of the data array
     *
     */
    @Override
    public int getItemCount() {
        return this.data.size();
    }

    /**
     * Sets the boolean value if intent came from the your recipe tab
     *
     * @param bool boolean if it is true or false
     */
    public void setFromYourRecipe(Boolean bool){
        this.fromYourRecipe = bool;
    }

    /**
     * Sets the boolean value if intent came from the home tab
     *
     * @param bool boolean if it is true or false
     */
    public void setInHome(Boolean bool){
        this.inHome = bool;
    }

    /**
     * Sets the boolean value if intent came from the favorites tab
     *
     * @param bool boolean if it is true or false
     */
    public void setInFavorites(Boolean bool){
        this.inFavorites = bool;
    }
}
