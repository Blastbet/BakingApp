package com.blastbet.nanodegree.bakingapp.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by ilkka on 1.9.2017.
 */

public class Recipe {
    @Expose
    private int id;

    @Expose
    private String name;

    @Expose
    private List<Ingredient> ingredients;

    @Expose
    private List<RecipeStep> steps;

    @Expose
    private int servings;

    @Expose
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Recipe parseJSON(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Recipe recipe = gson.fromJson(json, Recipe.class);
        return recipe;
    }
}
