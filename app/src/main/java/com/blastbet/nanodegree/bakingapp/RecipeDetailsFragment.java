package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.common.AppConfiguration;
import com.blastbet.nanodegree.bakingapp.data.BakingLoader;
import com.blastbet.nanodegree.bakingapp.data.RecipeIngredientsLoader;
import com.blastbet.nanodegree.bakingapp.data.RecipeStepLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRecipeStepFragmentInteractionListener}
 * interface.
 */
public class RecipeDetailsFragment extends Fragment implements BakingLoader.Callbacks {

    private static final String TAG = RecipeDetailsFragment.class.getSimpleName();

    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_RECIPE_NAME = "recipe_name";
    private OnRecipeStepFragmentInteractionListener mListener;

    private int mRecipeId;
    private String mName;

    protected RecipeStepLoader mStepLoader;

    private RecipeDetailsRecyclerViewAdapter mRecipeAdapter;

    private AppConfiguration mAppConfig;

    @BindView(R.id.list) RecyclerView mRecyclerView;
    @BindView(R.id.empty_view) TextView mEmptyView;

    Unbinder mUnbinder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailsFragment() {
        mRecipeId = -1;
    }

    static RecipeDetailsFragment newInstance(int recipeId, String name) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        Log.d(TAG, "New details for recipe: " + name + " (" + recipeId + ")");
        args.putInt(KEY_RECIPE_ID, recipeId);
        args.putString(KEY_RECIPE_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (getArguments() != null) {
            mRecipeId = arguments.getInt(KEY_RECIPE_ID);
            mName = arguments.getString(KEY_RECIPE_NAME);
            Log.d(TAG, "Creating recipe: " + mName + " (" + mRecipeId + ")");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RECIPE_ID, mRecipeId);
        outState.putString(KEY_RECIPE_NAME, mName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mStepLoader == null) {
            mStepLoader = new RecipeStepLoader(getContext(), getLoaderManager(), this);
        }
        mStepLoader.init(mRecipeId);
        if (!mAppConfig.isOnlyLandscape()) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionbar = activity.getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(mName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        int cardviewInsets = getResources().getDimensionPixelSize(R.dimen.recipe_detail_cardview_insets);
        mRecyclerView.addItemDecoration(new CardViewItemDecoration(cardviewInsets));
        if (savedInstanceState != null) {
            mRecipeId = savedInstanceState.getInt(KEY_RECIPE_ID);
            mName = savedInstanceState.getString(KEY_RECIPE_NAME);
            Log.d(TAG, "Reverting to saved recipe state: " + mName + " (" + mRecipeId + ")");
        }

        if (mRecipeId < 0) {
            throw new RuntimeException("Invalid recipe mId when initializing recipe step list");
        }

        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecipeAdapter = new RecipeDetailsRecyclerViewAdapter(mListener);
        mRecipeAdapter.setEmptyView(mEmptyView, mRecyclerView);
        mRecyclerView.setAdapter(mRecipeAdapter);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeStepFragmentInteractionListener &&
                context instanceof AppConfiguration) {
            mListener = (OnRecipeStepFragmentInteractionListener) context;
            mAppConfig = (AppConfiguration) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeStepFragmentInteractionListener and AppConfiguration");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        if (id == mStepLoader.getLoaderId()) {
            Log.d(TAG, "Finished loading " + cursor.getCount() + " items");
            mRecipeAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(int id) {
        if (id == mStepLoader.getLoaderId()) {
            Log.d(TAG, "Reset loader");
            mRecipeAdapter.swapCursor(null);
        }
    }

    public interface OnRecipeStepFragmentInteractionListener {
        void onRecipeStepClicked(int recipeId, int stepNumber, int stepCount);
    }
}
