package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.data.RecipeLoader;
import com.blastbet.nanodegree.bakingapp.sync.RecipeSyncAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRecipeListInteractionListener}
 * interface.
 */
public class RecipeFragment extends Fragment implements RecipeLoader.Callbacks {

    private static final String TAG = RecipeFragment.class.getSimpleName();

    @BindView(R.id.empty_view) TextView mEmptyView;
    @BindView(R.id.list_recipe) RecyclerView mRecipeListView;

    private int mColumnCount = 1;

    private OnRecipeListInteractionListener mListener;
    RecipeLoader mRecipeLoader;
    private RecipeRecyclerViewAdapter mRecipeAdapter;

    private Unbinder mUnbinder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRecipeLoader == null) {
            mRecipeLoader = new RecipeLoader(getContext(), getLoaderManager(), this);
        }
        mRecipeLoader.init(null);
        if (!getResources().getBoolean(R.bool.landscape_only)) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionbar = activity.getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(R.string.app_name);
            }
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    public void updateRecipes() {
        Log.d(TAG, "update recipes.");
        RecipeSyncAdapter.syncRecipesNow(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - update recipes.");
        updateRecipes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.landscape_only)) {
            mColumnCount = 3;
        }
        else {
            mColumnCount = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mUnbinder = ButterKnife.bind(this, view);
        Log.d(TAG, "onCreateView");

        mRecipeListView.addItemDecoration(new CardViewItemDecoration());

        Context context = view.getContext();
        if (mColumnCount <= 1) {
            Log.d(TAG, " -> linear list layout");
            mRecipeListView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            Log.d(TAG, " -> grid list layout");
            mRecipeListView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecipeAdapter = new RecipeRecyclerViewAdapter(mListener);
        mRecipeAdapter.setEmptyView(mEmptyView, mRecipeListView);
        mRecipeListView.setAdapter(mRecipeAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeListInteractionListener) {
            mListener = (OnRecipeListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        if (id == mRecipeLoader.getLoaderId()) {
            Log.d(TAG, "Received recipe cursor");
            mRecipeAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(int id) {
        if (id == mRecipeLoader.getLoaderId()) {
            mRecipeAdapter.swapCursor(null);
        }
    }

    public interface OnRecipeListInteractionListener {
        void onRecipeClicked(int recipeId, String recipeName);
    }
}
