package com.blastbet.nanodegree.bakingapp;

import android.database.Cursor;
import android.database.DataSetObserver;
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

public class RecipeDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecipeDetailsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecipeDetailsRecyclerViewAdapter.class.getSimpleName();

    private final OnRecipeStepFragmentInteractionListener mListener;

    private int mRecipeId;
    private Cursor mCursor;
    private boolean mDataValid;

    private DataSetObserver mDataSetObserver;

    private SparseBooleanArray mSelectedItems;

    public RecipeDetailsRecyclerViewAdapter(OnRecipeStepFragmentInteractionListener listener) {
        mSelectedItems = new SparseBooleanArray();
        mListener = listener;
        mRecipeId = -1;
        mDataValid = false;

        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mDataValid = true;
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                mDataValid = false;
                notifyDataSetChanged();
            }
        };

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

//        holder.mTextStepNum.setText(mCursor.getString(RecipeStepLoader.COL_STEP_INDEX));

        // TODO: Set correct text
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.selectItem();
/*
                if (mSelectedItems.get(holder.getAdapterPosition(), false)) {
                    mSelectedItems.delete(holder.getAdapterPosition());
                    holder.mView.setSelected(false);
                }
                else {
                    mSelectedItems.put(holder.getAdapterPosition(), true);
                    holder.mView.setSelected(true);
                }
                if (null != mListener) {
                    mListener.onRecipeStepClicked(getRecipeStepAt(holder.mStepNumber));
                }
*/
            }
        });
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getInt(RecipeStepLoader.COL_STEP_INDEX);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count (" + (mCursor == null ? "no data cursor, " : "valid data cursor, ") +
                (mDataValid ? "data valid" : "data not valid") + ") " + (mCursor != null && mDataValid ? mCursor.getCount() + " items" : " 0 items"));
        return (mCursor != null ? mCursor.getCount() : 0);
    }

    void swapCursor(Cursor cursor) {
        Log.d(TAG, "Swapping cursor to " + (cursor != null ? "new one" : "null"));
        if (cursor == mCursor) {
            Log.d(TAG, "same cursor -> nop");
            return;
        }
        if (mCursor != null) {
            Log.d(TAG, "Unregister observer from precious cursor");
            mCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mCursor = cursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                Log.d(TAG, "Register new data set observer");
                cursor.registerDataSetObserver(mDataSetObserver);
            }
            cursor.moveToFirst();
            mRecipeId = cursor.getInt(RecipeStepLoader.COL_RECIPE_ID);
            mDataValid = true;
        }
        else {
            Log.d(TAG, "Invalid data");
            mRecipeId = -1;
            mDataValid = false;
        }
        Log.d(TAG, "notify of data set change");

        notifyDataSetChanged();
    }

    void setEmptyView(final TextView emptyView, final RecyclerView recyclerView) {
        RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Log.d(TAG, "Got notified of change in data");
                if (getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };
        // Init state
        Log.d(TAG, "Set initial data state");
        dataObserver.onChanged();
        registerAdapterDataObserver(dataObserver);
    }

/*
    RecipeStep getRecipeStepAt(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new RuntimeException("Invalid recipe step (" + Integer.toString(position) + ") requested!");
        }

        return new RecipeStep(
                mCursor.getInt(RecipeStepLoader.COL_STEP_INDEX),
                mCursor.getString(RecipeStepLoader.COL_STEP_SHORT_DESCRIPTION),
                mCursor.getString(RecipeStepLoader.COL_STEP_DESCRIPTION),
                mCursor.getString(RecipeStepLoader.COL_STEP_VIDEO_URL),
                mCursor.getString(RecipeStepLoader.COL_STEP_THUMBNAIL_URL));
    }
*/

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