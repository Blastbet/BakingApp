package com.blastbet.nanodegree.bakingapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.blastbet.nanodegree.bakingapp.connection.ConnectivityMonitor;
import com.blastbet.nanodegree.bakingapp.recipe.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements RecipeFragment.OnRecipeListInteractionListener,
        RecipeDetailsFragment.OnRecipeStepFragmentInteractionListener,
        RecipeStepDetailsFragment.OnRecipeStepDetailsFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ConnectivityMonitor mConnectivityMonitor;

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
    public void onRecipeClicked(int recipeId) {
        RecipeFragment rf = (RecipeFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_list_fragment_tag));

        RecipeDetailsFragment rsf = RecipeDetailsFragment.newInstance(recipeId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .remove(rf)
                .add(R.id.container_fragment, rsf, getString(R.string.recipe_step_list_fragment_tag));

        if (getResources().getBoolean(R.bool.landscape_only)) {
            RecipeStepDetailsFragment rsdf = new RecipeStepDetailsFragment();
            transaction = transaction.add(R.id.container_fragment, rsdf, getString(R.string.recipe_step_details_fragment_tag));
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
            RecipeDetailsFragment rsf = (RecipeDetailsFragment) getSupportFragmentManager()
                    .findFragmentByTag(getString(R.string.recipe_step_list_fragment_tag));

            transaction = transaction.remove(rsf);
        }

        rsdf = RecipeStepDetailsFragment.newInstance(recipeId, stepNumber, stepCount);

        transaction.add(R.id.container_fragment, rsdf, getString(R.string.recipe_step_details_fragment_tag))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPreviousStepRequested(RecipeStep step) {
        RecipeDetailsFragment rsf = (RecipeDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_step_list_fragment_tag));
        if (rsf != null) {
            rsf.selectPreviousStep(step.getIndex());
        }
    }

    @Override
    public void onNextStepRequested(RecipeStep step) {
        RecipeDetailsFragment rsf = (RecipeDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.recipe_step_list_fragment_tag));
        if (rsf != null) {
            rsf.selectNextStep(step.getIndex());
        }
    }
}
