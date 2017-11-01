package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.data.BakingLoader;
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
    private OnRecipeStepFragmentInteractionListener mListener;

    private int mRecipeId;

    private RecipeStepLoader mStepLoader;

    private RecipeDetailsRecyclerViewAdapter mRecipeAdapter;

    @BindView(R.id.ingredients_card) CardView mIngredientsCard;
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

    static RecipeDetailsFragment newInstance(int recipeId) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (getArguments() != null) {
            mRecipeId = arguments.getInt(KEY_RECIPE_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RECIPE_ID, mRecipeId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStepLoader = new RecipeStepLoader(getContext(), getLoaderManager(), this);
        mStepLoader.init(mRecipeId);
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
        }

        if (mRecipeId < 0) {
            throw new RuntimeException("Invalid recipe id when initializing recipe step list");
        }

        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (context.getResources().getBoolean(R.bool.landscape_only)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT,
                    context.getResources().getInteger(R.integer.weight_recipe_step_fragment));
            view.setLayoutParams(params);
        }
        mRecipeAdapter = new RecipeDetailsRecyclerViewAdapter(mListener);
        mRecipeAdapter.setEmptyView(mEmptyView, mRecyclerView);
        mRecyclerView.setAdapter(mRecipeAdapter);

        mIngredientsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onIngredientsClicked(mRecipeId);
            }
        });
        return view;
    }

//    public void selectStep(long stepNumber) {
//        RecipeDetailsRecyclerViewAdapter.ViewHolder viewHolder = (RecipeDetailsRecyclerViewAdapter.ViewHolder)
//                mRecyclerView.findViewHolderForItemId(stepNumber);
//
//        viewHolder.selectItem();
//    }
//
//    public void selectNextStep(long stepNumber) {
//        Log.d(TAG, "Selecting step for stepnumber: " + Long.toString(stepNumber));
//        final int position = mRecyclerView.findViewHolderForItemId(stepNumber).getAdapterPosition() + 1;
//
//        RecipeDetailsRecyclerViewAdapter.ViewHolder viewHolder = (RecipeDetailsRecyclerViewAdapter.ViewHolder)
//                mRecyclerView.findViewHolderForAdapterPosition(position);
//        if (viewHolder != null) {
//            viewHolder.selectItem();
//        }
//    }
//
//    public void selectPreviousStep(long stepNumber) {
//        final int position = mRecyclerView.findViewHolderForItemId(stepNumber).getAdapterPosition() - 1;
//
//        if (position < 0) {
//            return;
//        }
//        RecipeDetailsRecyclerViewAdapter.ViewHolder viewHolder = (RecipeDetailsRecyclerViewAdapter.ViewHolder)
//                mRecyclerView.findViewHolderForAdapterPosition(position);
//        if (viewHolder != null) {
//            viewHolder.selectItem();
//        }
//    }

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
        if (context instanceof OnRecipeStepFragmentInteractionListener) {
            mListener = (OnRecipeStepFragmentInteractionListener) context;
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
        if (id == mStepLoader.getLoaderId()) {
            mRecipeAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(int id) {
        if (id == mStepLoader.getLoaderId()) {
            mRecipeAdapter.swapCursor(null);
        }
    }

    public interface OnRecipeStepFragmentInteractionListener {
        void onRecipeStepClicked(int recipeId, int stepNumber, int stepCount);
        void onIngredientsClicked(int recipeId);
    }
}
