package com.blastbet.nanodegree.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.RecipeDetailsFragment.OnRecipeStepFragmentInteractionListener;
import com.blastbet.nanodegree.bakingapp.data.RecipeStepLoader;
import com.blastbet.nanodegree.bakingapp.sync.BakingRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsRecyclerViewAdapter extends BakingRecyclerViewAdapter<RecipeDetailsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecipeDetailsRecyclerViewAdapter.class.getSimpleName();

    private final OnRecipeStepFragmentInteractionListener mListener;

    private int mRecipeId;

    private SparseBooleanArray mSelectedItems;

    public RecipeDetailsRecyclerViewAdapter(OnRecipeStepFragmentInteractionListener listener) {
        super();
        mSelectedItems = new SparseBooleanArray();
        mListener = listener;
        mRecipeId = -1;
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
        holder.mStepNumber = position;
        holder.mTextStepDescription.setText(
                mCursor.getString(RecipeStepLoader.COL_STEP_SHORT_DESCRIPTION)
        );
        holder.mView.setSelected(mSelectedItems.get(position, false));

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
        //@BindView(R.id.text_step_number) TextView mTextStepNum;
        @BindView(R.id.text_recipe_step_list_item) TextView mTextStepDescription;
        int mStepNumber;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }

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
                mListener.onRecipeStepClicked(mRecipeId, mStepNumber, mCursor.getCount());
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextStepDescription.getText() + "'";
        }
    }
}