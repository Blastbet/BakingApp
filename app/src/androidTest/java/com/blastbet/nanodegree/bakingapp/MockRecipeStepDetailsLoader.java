package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.blastbet.nanodegree.bakingapp.data.RecipeStepDetailsLoader;

/**
 * Created by ilkka on 3.12.2017.
 */

public class MockRecipeStepDetailsLoader extends RecipeStepDetailsLoader {
    int recipeId;
    int recipeStep;

    public MockRecipeStepDetailsLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        super(context, manager, callbacks);
    }

    @Override
    public void init(int recipeId, int recipeStep) {
        this.recipeId = recipeId;
        this.recipeStep = recipeStep;
    }

    @Override
    public void restart(int recipeId, int recipeStep) {
        this.recipeId = recipeId;
        this.recipeStep = recipeStep;
    }
}
