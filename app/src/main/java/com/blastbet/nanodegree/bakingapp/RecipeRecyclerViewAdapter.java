package com.blastbet.nanodegree.bakingapp;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.RecipeFragment.OnRecipeListInteractionListener;
import com.blastbet.nanodegree.bakingapp.data.RecipeLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecipeRecyclerViewAdapter.class.getSimpleName();

    private final OnRecipeListInteractionListener mListener;

    private Cursor mCursor;

    private DataSetObserver mDataSetObserver;

    private boolean mDataValid;

    public RecipeRecyclerViewAdapter(OnRecipeListInteractionListener listener) {

        mListener = listener;
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
            mDataValid = true;
        }
        else {
            Log.d(TAG, "Invalid data");
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Resources res = holder.mTextServings.getResources();
        holder.id = mCursor.getInt(RecipeLoader.COL_RECIPE_ID);
        holder.mTextView.setText(mCursor.getString(RecipeLoader.COL_RECIPE_NAME));
        holder.mTextServings.setText(res.getString(R.string.formatted_servings, mCursor.getInt(RecipeLoader.COL_RECIPE_SERVINGS)));

        // TODO: fetch background image
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRecipeClicked(holder.id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count (" + (mCursor == null ? "no data cursor, " : "valid data cursor, ") +
                (mDataValid ? "data valid" : "data not valid") + ")");
        return (mCursor != null && mDataValid) ? mCursor.getCount() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_recipe_list_item) TextView mTextView;
        @BindView(R.id.text_recipe_servings) TextView mTextServings;

        public final View mView;
        public int id;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText() + "'";
        }
    }
}
