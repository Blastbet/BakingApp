package com.blastbet.nanodegree.bakingapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.blastbet.nanodegree.bakingapp.recipe.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements RecipeFragment.OnRecipeListInteractionListener,
        RecipeStepFragment.OnRecipeStepFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.container_fragment) ViewGroup mFragmentContainer;

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
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_fragment, rf, getString(R.string.recipe_list_fragment_tag))
                    .commit();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RecipeStepFragment.KEY_RECIPE_ID)) {
                Log.d(TAG, "Recipe ID: " + savedInstanceState.getInt(RecipeStepFragment.KEY_RECIPE_ID));
            }

            if (savedInstanceState.containsKey(RecipeStepDetailsFragment.KEY_RECIPE_STEP)) {
                Log.d(TAG, "Recipe ID: " + savedInstanceState.getParcelable(RecipeStepDetailsFragment.KEY_RECIPE_STEP));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRecipeClicked(int recipeId) {
        RecipeFragment rf = (RecipeFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_list_fragment_tag));

        RecipeStepFragment rsf = RecipeStepFragment.newInstance(recipeId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .remove(rf)
                .add(R.id.container_fragment, rsf, getString(R.string.recipe_step_list_fragment_tag));

        if (getResources().getBoolean(R.bool.landscape_only)) {
            RecipeStepDetailsFragment rsdf = new RecipeStepDetailsFragment();
            transaction = transaction.add(R.id.container_fragment, rsdf, getString(R.string.recipe_step_details_fragment_tag));
        }

        transaction.addToBackStack(null).commit();
    }


    @Override
    public void onRecipeStepClicked(RecipeStep step) {
        RecipeStepDetailsFragment rsdf = (RecipeStepDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_step_details_fragment_tag));

        if (rsdf != null) {
            rsdf.setStepData(step);
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!getResources().getBoolean(R.bool.landscape_only)) {
            RecipeStepFragment rsf = (RecipeStepFragment) getSupportFragmentManager()
                    .findFragmentByTag(getString(R.string.recipe_step_list_fragment_tag));

            transaction = transaction.remove(rsf);
        }

        rsdf = RecipeStepDetailsFragment.newInstance(step);

        transaction.add(R.id.container_fragment, rsdf, getString(R.string.recipe_step_details_fragment_tag))
                .addToBackStack(null)
                .commit();

    }
}
