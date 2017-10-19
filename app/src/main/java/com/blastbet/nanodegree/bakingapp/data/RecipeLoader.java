package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by ilkka on 9.9.2017.
 */

public class RecipeLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] RECIPE_COLUMNS = {
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_ID,
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_NAME,
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_IMAGE,
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_SERVINGS
    };

    //static final int COL_ROW_ID = 0;
    public static final int COL_RECIPE_ID = 0;
    public static final int COL_RECIPE_NAME = 1;
    public static final int COL_RECIPE_IMAGE = 2;
    public static final int COL_RECIPE_SERVINGS = 3;

    private static final int RECIPE_LOADER = 0;

    private Context mContext;
    private LoaderManager mManager;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onLoadFinished(Cursor cursor);
        void onLoaderReset();
    }

    public RecipeLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        mContext = context;
        mManager = manager;
        mCallbacks = callbacks;
    }

    public void initRecipeLoader() {
        mManager.initLoader(RECIPE_LOADER, null, this);
    }

    public void initLoader() {
        mManager.initLoader(RECIPE_LOADER, null, this);
    }

    public void restartLoader() {
        mManager.restartLoader(RECIPE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(mContext, RecipeEntry.CONTENT_URI, RECIPE_COLUMNS, null, null, null);
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
