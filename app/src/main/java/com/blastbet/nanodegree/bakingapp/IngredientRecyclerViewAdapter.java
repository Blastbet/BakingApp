package com.blastbet.nanodegree.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.data.RecipeIngredientsLoader;
import com.blastbet.nanodegree.bakingapp.sync.BakingRecyclerViewAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientRecyclerViewAdapter extends BakingRecyclerViewAdapter<IngredientRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = IngredientRecyclerViewAdapter.class.getSimpleName();

    public IngredientRecyclerViewAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        Log.d(TAG, "Cursor contains following fields: " + Arrays.toString(mCursor.getColumnNames()));
        double quantity = mCursor.getDouble(RecipeIngredientsLoader.COL_INGREDIENT_QUANTITY);
        String measure = mCursor.getString(RecipeIngredientsLoader.COL_INGREDIENT_MEASURE);
        String ingredient = mCursor.getString(RecipeIngredientsLoader.COL_INGREDIENT_NAME);

        holder.mQuantity.setText(Double.toString(quantity));
        holder.mMeasure.setText(measure.toLowerCase());
        holder.mIngredientName.setText(ingredient);

        holder.mView.setClickable(false);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();

        Log.d(TAG, "Item count: " + count);
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        @BindView(R.id.text_quantity) TextView mQuantity;
        @BindView(R.id.text_measure) TextView mMeasure;
        @BindView(R.id.text_ingredient_name) TextView mIngredientName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '"
                    + mQuantity.getText() + " "
                    + mMeasure.getText() + " "
                    + mIngredientName.getText()
                    + "'";
        }
    }
}
