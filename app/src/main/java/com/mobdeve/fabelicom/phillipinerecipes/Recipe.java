package com.mobdeve.fabelicom.phillipinerecipes;


public class Recipe {

    public String name, author, date, image_name, category, location, desc, authorUID, ingredients, steps, uuid;

    public Recipe() {}

    public Recipe(String name, String author, String date, String image_name, String category, String location, String desc, String authorUID, String ingredients, String steps, String uuid) {
        this.name = name;
        this.author = author;
        this.date = date;
        this.image_name = image_name;
        this.category = category;
        this.location = location;
        this.desc = desc;
        this.authorUID = authorUID;
        this.ingredients = ingredients;
        this.steps = steps;
        this.uuid = uuid;
    }

    /**
     * Gets the name of the recipe
     *
     * @return the name of the recipe
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the author of the recipe
     *
     * @return the authorof the recipe
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the date when the recipe was posted
     *
     * @return date when the recipe was posted
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the name of the image of the recipe
     *
     * @return the name of the image of the recipe
     */
    public String getImage_name() {
        return image_name;
    }

    /**
     * Gets the category of the recipe
     *
     * @return the category of the recipe
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the location of the recipe
     *
     * @return the location of the recipe
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the description of the recipe
     *
     * @return the description of the recipe
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Gets the UID of the author of the recipe
     *
     * @return UID of the author of the recipe
     */
    public String getAuthorUID() {
        return authorUID;
    }

    /**
     * Gets the ingredients of the recipe
     *
     * @return the ingredients of the recipe
     */
    public String getIngredients() {
        return ingredients;
    }

    /**
     * Gets the steps of how to make the recipe
     *
     * @return the steps of the recipe
     */
    public String getSteps() {
        return steps;
    }

    /**
     * Gets the uuid of the recipe
     *
     * @return the uuid of the recipe
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the name of the recipe
     *
     * @param name name of the recipe
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the author of the recipe
     *
     * @param author name of the recipe
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the date when the recipe was posted
     *
     * @param date date when the recipe was posted
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the image inside the imageview
     *
     * @param image_name  A string of the name of the image
     */
    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    /**
     * Sets the category of the recipe
     *
     * @param category the category of the recipe
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Sets the location of the recipe
     *
     * @param location the location of the recipe
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the description of the recipe
     *
     * @param desc the description of the recipe
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Sets the UID of the author of the recipe
     *
     * @param authorUID the description of the recipe
     */
    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    /**
     * Sets the ingredients of the recipe
     *
     * @param ingredients the description of the recipe
     */
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Sets the steps of the recipe
     *
     * @param steps the description of the recipe
     */
    public void setSteps(String steps) {
        this.steps = steps;
    }

    /**
     * Sets the uuid of the recipe
     *
     * @param uuid the description of the recipe
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
