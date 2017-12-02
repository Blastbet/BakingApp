package com.blastbet.nanodegree.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by ilkka on 26.11.2017.
 */

public class TestActivity extends AppCompatActivity implements
        RecipeFragment.OnRecipeListInteractionListener,
        RecipeDetailsFragment.OnRecipeStepFragmentInteractionListener
{
    private static final String TAG = TestActivity.class.getSimpleName();
    public int mRecipeId;
    public String mRecipeName;
    public int mStepNumber;
    public int mStepCount;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DESTROY");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CREATE");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "RESUME");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "PAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "STOP");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "START");
    }

    @Override
    public void onRecipeClicked(int recipeId, String recipeName) {
        mRecipeId = recipeId;
        mRecipeName = recipeName;
    }

    @Override
    public void onRecipeStepClicked(int recipeId, int stepNumber, int stepCount) {
        mRecipeId = recipeId;
        mStepNumber = stepNumber;
        mStepCount = stepCount;
    }
}
