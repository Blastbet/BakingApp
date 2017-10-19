package com.blastbet.nanodegree.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

/**
 * Created by ilkka on 9.9.2017.
 */

public class RecipeIngredientLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String KEY_RECIPE_ID = "recipe_id";

    private static final String[] RECIPE_INGREDIENT_COLUMNS = {
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_RECIPE_ID,
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_NAME,
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_MEASURE,
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_QUANTITY
    };

    //static final int COL_ROW_ID = 0;
    static final int COL_RECIPE_ID = 1;
    static final int COL_INGREDIENT_NAME        = 2;
    static final int COL_INGREDIENT_MEASURE     = 3;
    static final int COL_INGREDIENT_QUANTITY    = 4;

    private static final int RECIPE_INGREDIENT_LOADER = 2;

    private Context mContext;
    private LoaderManager mManager;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onLoadFinished(Cursor cursor);
        void onLoaderReset();
    }

    public RecipeIngredientLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        mContext = context;
        mManager = manager;
        mCallbacks = callbacks;
    }

    public void initLoader(int recipeId) {
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        mManager.initLoader(RECIPE_INGREDIENT_LOADER, args, this);
    }

    public void restartLoader(int recipeId) {
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        mManager.restartLoader(RECIPE_INGREDIENT_LOADER, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        int recipeId = bundle.getInt(KEY_RECIPE_ID);
        return new CursorLoader(mContext, IngredientEntry.buildUriForRecipe(recipeId),
                RECIPE_INGREDIENT_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCallbacks.onLoadFinished(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallbacks.onLoaderReset();
    }
}
