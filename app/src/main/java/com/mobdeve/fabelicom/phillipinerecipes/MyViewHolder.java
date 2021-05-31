package com.mobdeve.fabelicom.phillipinerecipes;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private TextView nameTv, authorTv, dateTv, locationTv, categoryTv, descTv;
    private ImageView imageIv;
    private ImageButton editBtn, deleteBtn, favBtn;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        this.nameTv = itemView.findViewById(R.id.foodNameTv);
        this.authorTv = itemView.findViewById(R.id.authorTv);
        this.dateTv = itemView.findViewById(R.id.dateTv);
        this.locationTv = itemView.findViewById(R.id.locationTv);
        this.categoryTv = itemView.findViewById(R.id.categoryTv);
        this.descTv = itemView.findViewById(R.id.descTv);
        this.imageIv = itemView.findViewById(R.id.foodImg);
        this.editBtn = itemView.findViewById(R.id.editBtnFR);
        this.deleteBtn = itemView.findViewById(R.id.deleteBtnFR);
        this.favBtn = itemView.findViewById(R.id.favoriteBtn);
    }

    /**
     * Sets the name of the recipe
     *
     * @param name name of the recipe
     */
    public void setRecipeName(String name) {
        this.nameTv.setText(name);
    }

    /**
     * Sets the author of the recipe
     *
     * @param author name of the recipe
     */
    public void setRecipeAuthor(String author) {
        this.authorTv.setText(author);
    }

    /**
     * Sets the date when the recipe was posted
     *
     * @param date date when the recipe was posted
     */
    public void setRecipeDate(String date) {
        this.dateTv.setText(date);
    }

    /**
     * Sets the location of the recipe
     *
     * @param location the location of the recipe
     */
    public void setRecipeLocation(String location) {
        this.locationTv.setText(location);
    }

    /**
     * Sets the category of the recipe
     *
     * @param category the category of the recipe
     */
    public void setRecipeCategory(String category) {
        this.categoryTv.setText(category);
    }

    /**
     * Sets the description of the recipe
     *
     * @param desc the description of the recipe
     */
    public void setRecipeDesc(String desc) {
        this.descTv.setText(desc);
    }

    /**
     * Loads and sets the image inside the imageview
     *
     * @param imageName  A string of the name of the image
     */
    public void setImage(String imageName) {
        // With the storageReference, get the image based on its name
        StorageReference imageRef = this.storageRef.child(imageName);
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
                            .into(imageIv);
                } else {
                    Log.d("Debug", "onComplete: did not get image");
                }
            }
        });
    }

    public void setEditButtonListener(View.OnClickListener click){
        this.editBtn.setOnClickListener(click);
    }

    public void setDeleteButtonListener(View.OnClickListener click){
        this.deleteBtn.setOnClickListener(click);
    }

    public void setFavoriteButtonListener(View.OnClickListener click){
        this.favBtn.setOnClickListener(click);
    }

    public void setButtonVisible(){
        this.editBtn.setVisibility(View.VISIBLE);
        this.deleteBtn.setVisibility(View.VISIBLE);
    }

    public void setButtonInvisible(){
        this.editBtn.setVisibility(View.GONE);
        this.deleteBtn.setVisibility(View.GONE);
    }

    public void setFavButton(Boolean bool){
        if(bool)
            this.favBtn.setVisibility(View.VISIBLE);
        else this.favBtn.setVisibility(View.GONE);
    }

    public void setFavButtonImageFilled(){
        this.favBtn.setImageResource(R.drawable.cookie_filled);
    }

    public void setFavBtnClickable(Boolean bool){
        this.favBtn.setClickable(bool);
    }

    public void setFavButtonImageOutline(){
        this.favBtn.setImageResource(R.drawable.cookie_outline);
    }

    public void setNameButtonListener(View.OnClickListener click){
        this.nameTv.setOnClickListener(click);
    }

}
