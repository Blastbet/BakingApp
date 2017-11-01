package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.data.BakingLoader;
import com.blastbet.nanodegree.bakingapp.data.RecipeIngredientsLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnIngredientFragmentInteractionListener}
 * interface.
 */
public class IngredientFragment extends Fragment implements BakingLoader.Callbacks {

    private static final String TAG = IngredientFragment.class.getSimpleName();

    private static final String KEY_RECIPE_ID = "recipe_id";

    private OnIngredientFragmentInteractionListener mListener;
    private int mRecipeId;

    private RecipeIngredientsLoader mLoader;

    @BindView(R.id.list) RecyclerView mIngredientListView;
    @BindView(R.id.empty_view) TextView mEmptyView;

    Unbinder mUnbinder;

    IngredientRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientFragment() {
        mRecipeId = -1;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoader = new RecipeIngredientsLoader(getContext(), getLoaderManager(), this);
        mLoader.init(mRecipeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient_list, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        mAdapter = new IngredientRecyclerViewAdapter();

        mIngredientListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mIngredientListView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mRecipeId = savedInstanceState.getInt(KEY_RECIPE_ID);
        }

        mIngredientListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Ingredient list view clicked.");
                mListener.onListFragmentClicked();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnIngredientFragmentInteractionListener) {
            mListener = (OnIngredientFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIngredientFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        if (id == mLoader.getLoaderId()) {
            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(int id) {
        if (id == mLoader.getLoaderId()) {
            mAdapter.swapCursor(null);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnIngredientFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentClicked();
    }
}
