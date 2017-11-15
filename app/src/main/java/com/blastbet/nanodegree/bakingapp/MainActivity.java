package com.blastbet.nanodegree.bakingapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.blastbet.nanodegree.bakingapp.connection.ConnectivityMonitor;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements RecipeFragment.OnRecipeListInteractionListener,
        RecipeDetailsFragment.OnRecipeStepFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ConnectivityMonitor mConnectivityMonitor;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fragment_container) ViewGroup mFragmentContainer;


    private int mRecipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            Log.d(TAG, "CREATING NEW, savedinstancestate = null");
            RecipeFragment rf = new RecipeFragment();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            int containerId = R.id.fragment_container;
            if (getResources().getBoolean(R.bool.landscape_only)) {
                containerId = R.id.container_navigation;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(containerId, rf, getString(R.string.recipe_list_fragment_tag))
                    .commit();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RecipeDetailsFragment.KEY_RECIPE_ID)) {
                Log.d(TAG, "Recipe ID: " + savedInstanceState.getInt(RecipeDetailsFragment.KEY_RECIPE_ID));
            }

            if (savedInstanceState.containsKey(RecipeStepDetailsFragment.KEY_RECIPE_STEP)) {
                Log.d(TAG, "Recipe ID: " + savedInstanceState.getParcelable(RecipeStepDetailsFragment.KEY_RECIPE_STEP));
            }
        }

        mConnectivityMonitor = new ConnectivityMonitor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectivityMonitor.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectivityMonitor.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRecipeClicked(int recipeId, String recipeName) {
        mRecipeId = recipeId;

        RecipeFragment rf = (RecipeFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_list_fragment_tag));

        int containerId = R.id.fragment_container;
        if (getResources().getBoolean(R.bool.landscape_only)) {
            containerId = R.id.container_navigation;
        }

        IngredientFragment ingredientFragment = IngredientFragment.newInstance(recipeId);
        RecipeDetailsFragment rdf = RecipeDetailsFragment.newInstance(recipeId, recipeName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .remove(rf)
                .add(containerId, ingredientFragment, getString(R.string.ingredient_fragment_tag))
                .add(containerId, rdf, getString(R.string.recipe_step_list_fragment_tag));

        if (getResources().getBoolean(R.bool.landscape_only)) {
            RecipeStepDetailsFragment rsdf = new RecipeStepDetailsFragment();
            transaction = transaction.add(R.id.fragment_container, rsdf, getString(R.string.recipe_step_details_fragment_tag));
        }

        transaction.addToBackStack(null).commit();
    }


    @Override
    public void onRecipeStepClicked(int recipeId, int stepNumber, int stepCount) {
        RecipeStepDetailsFragment rsdf = (RecipeStepDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_step_details_fragment_tag));

        if (rsdf != null) {
            rsdf.setStep(recipeId, stepNumber, stepCount);
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!getResources().getBoolean(R.bool.landscape_only)) {
            RecipeDetailsFragment rdf = (RecipeDetailsFragment) getSupportFragmentManager()
                    .findFragmentByTag(getString(R.string.recipe_step_list_fragment_tag));

            IngredientFragment ingredientFragment = (IngredientFragment) getSupportFragmentManager()
                    .findFragmentByTag(getString(R.string.ingredient_fragment_tag));

            transaction = transaction
                    .remove(rdf)
                    .remove(ingredientFragment);
        }

        rsdf = RecipeStepDetailsFragment.newInstance(recipeId, stepNumber, stepCount);

        transaction.add(R.id.fragment_container, rsdf, getString(R.string.recipe_step_details_fragment_tag))
                .addToBackStack(null)
                .commit();
    }
}
