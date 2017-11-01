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
import com.blastbet.nanodegree.bakingapp.sync.BakingRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeRecyclerViewAdapter extends BakingRecyclerViewAdapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecipeRecyclerViewAdapter.class.getSimpleName();

    private final OnRecipeListInteractionListener mListener;

    public RecipeRecyclerViewAdapter(OnRecipeListInteractionListener listener) {
        super();
        mListener = listener;
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
