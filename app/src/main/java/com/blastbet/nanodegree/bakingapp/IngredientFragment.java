package com.blastbet.nanodegree.bakingapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.data.BakingLoader;
import com.blastbet.nanodegree.bakingapp.data.RecipeIngredientsLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IngredientFragment extends Fragment implements BakingLoader.Callbacks {

    private static final String TAG = IngredientFragment.class.getSimpleName();

    private static final String KEY_RECIPE_ID = "recipe_id";

    private int mRecipeId;

    protected RecipeIngredientsLoader mLoader;

    @BindView(R.id.list) RecyclerView mIngredientListView;
    @BindView(R.id.ingredients_card) CardView mIngredientsCard;

    private boolean mExpanded;
    private Cursor mIngredientsData;

    Unbinder mUnbinder;

    IngredientRecyclerViewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientFragment() {
        mRecipeId = -1;
        mExpanded = false;
    }

    public static IngredientFragment newInstance(int recipeId) {
        IngredientFragment fragment = new IngredientFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRecipeId = getArguments().getInt(KEY_RECIPE_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeId >= 0) {
            outState.putInt(KEY_RECIPE_ID, mRecipeId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient_list, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mAdapter = new IngredientRecyclerViewAdapter();

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mIngredientListView.setLayoutManager(mLayoutManager);
        mIngredientListView.setAdapter(mAdapter);
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(getContext(), R.anim.layout_animation_slide_down);
        mIngredientListView.setLayoutAnimation(animation);

        if (savedInstanceState != null) {
            mRecipeId = savedInstanceState.getInt(KEY_RECIPE_ID);
        }

        mIngredientsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Ingredient list view clicked.");
                toggleIngredients();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        if (id == mLoader.getLoaderId()) {
            mIngredientsData = cursor;
            expandIngredients();
        }
    }

    @Override
    public void onLoaderReset(int id) {
        if (id == mLoader.getLoaderId()) {
            mIngredientsData = null;
            mAdapter.swapCursor(null);
        }
    }


    private void expandIngredients() {
        mAdapter.swapCursor(mIngredientsData);
        mIngredientListView.scheduleLayoutAnimation();
        mExpanded = true;
    }

    private void collapseIngredients() {
        mIngredientsData = null;
        mAdapter.swapCursor(null);
        mIngredientListView.scheduleLayoutAnimation();
        mExpanded = false;
    }

    private void toggleIngredients() {
        if (mExpanded) {
            collapseIngredients();
            mIngredientsCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            mIngredientsCard.setCardElevation(getResources().getDimension(R.dimen.ingredient_card_elevation));
        }
        else {
            mIngredientsCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

            if (mLoader == null) {
                mLoader = new RecipeIngredientsLoader(getContext(), getLoaderManager(), this);
            }

            mLoader.restart(mRecipeId);

            mIngredientsCard.setCardElevation(0);
        }
    }
}
