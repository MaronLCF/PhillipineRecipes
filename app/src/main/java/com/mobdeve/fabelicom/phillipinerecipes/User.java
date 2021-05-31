package com.mobdeve.fabelicom.phillipinerecipes;

import java.util.ArrayList;

public class User {

    public String username, age, email;
    public ArrayList<Recipe> favorites = new ArrayList<>();
    public User() {}
    public User(String username, String age, String email){
        this.username = username;
        this.age = age;
        this.email = email;
    }

    /**
     * Adds a recipe to the favorites arraylist
     *
     * @param recipe the category of the recipe
     */
    public void addFavorite(Recipe recipe){
        favorites.add(recipe);
    }

    /**
     * gets the ArrayList of the favorite recipes of the user.
     *
     * @return An ArrayList of the favorite recipes of the user.
     */
    public ArrayList getFavorites(){
        return favorites;
    }
}
