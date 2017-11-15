package com.blastbet.nanodegree.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.RecipeDetailsFragment.OnRecipeStepFragmentInteractionListener;
import com.blastbet.nanodegree.bakingapp.data.RecipeStepLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsRecyclerViewAdapter extends BakingRecyclerViewAdapter<RecipeDetailsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecipeDetailsRecyclerViewAdapter.class.getSimpleName();

    private final OnRecipeStepFragmentInteractionListener mListener;

    private int mRecipeId;

    private int mSelectedPos;

    private SparseBooleanArray mSelectedItems;

    public RecipeDetailsRecyclerViewAdapter(OnRecipeStepFragmentInteractionListener listener) {
        super();
        mSelectedItems = new SparseBooleanArray();
        mListener = listener;
        mRecipeId = -1;
        mSelectedPos = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_step_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mTextStepDescription.setText(
                mCursor.getString(RecipeStepLoader.COL_STEP_SHORT_DESCRIPTION)
        );
        Log.d(TAG, "Selected is " + mSelectedPos + ", this is " + position);
        holder.mView.setSelected(mSelectedPos == position);

        int recipeId = mCursor.getInt(RecipeStepLoader.COL_RECIPE_ID);
        int stepNumber = mCursor.getInt(RecipeStepLoader.COL_STEP_INDEX);
        holder.setStepDetails(stepNumber, mCursor.getCount(), recipeId);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.selectItem();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getInt(RecipeStepLoader.COL_STEP_INDEX);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        //@BindView(R.mId.text_step_number) TextView mTextStepNum;
        @BindView(R.id.text_recipe_step_list_item) TextView mTextStepDescription;
        private int mStepNumber;
        private int mRecipeId;
        private int mStepCount;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mStepNumber = -1;
            mRecipeId = -1;
            mStepCount = -1;
            ButterKnife.bind(this, mView);
        }

        void setStepDetails(int stepNumber, int stepCount, int recipeId) {
            mStepNumber = stepNumber;
            mStepCount = stepCount;
            mRecipeId = recipeId;
        }

        /*
                void selectItem() {
                    if (mSelectedItems.get(getAdapterPosition(), false)) {
                        mSelectedItems.delete(getAdapterPosition());
                        mView.setSelected(false);
                    }
                    else {
                        mSelectedItems.put(getAdapterPosition(), true);
                        mView.setSelected(true);
                    }
                    if (null != mListener) {
                        mListener.onRecipeStepClicked(this.mRecipeId, this.mStepNumber, this.mStepCount);
                    }
        */
        void selectItem() {
            if (mSelectedPos != getAdapterPosition()) {
                Log.d(TAG, "Selection changed!");
                mSelectedPos = getAdapterPosition();
                notifyDataSetChanged();

                if (null != mListener) {
                    mListener.onRecipeStepClicked(this.mRecipeId, this.mStepNumber, this.mStepCount);
                }
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextStepDescription.getText() + "'";
        }
    }
}