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

public class RecipeStepLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String KEY_RECIPE_ID = "recipe_id";

    private static final String[] RECIPE_STEP_COLUMNS = {
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_INDEX,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_SHORT_DESCRIPTION,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_DESCRIPTION,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_VIDEO_URL,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_THUMBNAIL_URL
    };

    //static final int COL_ROW_ID = 0;
    public static final int COL_RECIPE_ID = 0;
    public static final int COL_STEP_INDEX             = 1;
    public static final int COL_STEP_SHORT_DESCRIPTION = 2;
    public static final int COL_STEP_DESCRIPTION       = 3;
    public static final int COL_STEP_VIDEO_URL         = 4;
    public static final int COL_STEP_THUMBNAIL_URL     = 5;

    private static final int RECIPE_STEP_LOADER = 1;

    private Context mContext;
    private LoaderManager mManager;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onLoadFinished(Cursor cursor);
        void onLoaderReset();
    }

    public RecipeStepLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        mContext = context;
        mManager = manager;
        mCallbacks = callbacks;
    }

    public void initLoader(int recipeId) {
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        mManager.initLoader(RECIPE_STEP_LOADER, args, this);
    }

    public void restartLoader(int recipeId) {
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        mManager.restartLoader(RECIPE_STEP_LOADER, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        int recipeId = bundle.getInt(KEY_RECIPE_ID);
        final String sortOrder = StepEntry.COLUMN_INDEX + " ASC";
        return new CursorLoader(mContext, StepEntry.buildUriForRecipe(recipeId),
                RECIPE_STEP_COLUMNS,
                null, null, sortOrder);
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
