package com.blastbet.nanodegree.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

/**
 * Created by ilkka on 31.10.2017.
 */

public class RecipeStepDetailsLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RecipeStepDetailsLoader.class.getSimpleName();

    private static final String KEY_RECIPE_ID = "recipe_id";
    private static final String KEY_RECIPE_STEP = "recipe_step";

    private static final String[] RECIPE_STEP_COLUMNS = {
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_INDEX,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_DESCRIPTION,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_VIDEO_URL,
    };

    //static final int COL_ROW_ID = 0;
    public static final int COL_RECIPE_ID = 0;
    public static final int COL_STEP_INDEX             = 1;
    public static final int COL_STEP_DESCRIPTION       = 2;
    public static final int COL_STEP_VIDEO_URL         = 3;

    private static final int RECIPE_STEP_DETAILS_LOADER = 3;

    private Context mContext;
    private LoaderManager mManager;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onLoadFinished(Cursor cursor);
        void onLoaderReset();
    }

    public RecipeStepDetailsLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        mContext = context;
        mManager = manager;
        mCallbacks = callbacks;
    }

    public void initLoader(int recipeId, int recipeStep) {
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        args.putInt(KEY_RECIPE_STEP, recipeStep);
        mManager.initLoader(RECIPE_STEP_DETAILS_LOADER, args, this);
    }

    public void restartLoader(int recipeId, int recipeStep) {
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        args.putInt(KEY_RECIPE_STEP, recipeStep);
        mManager.restartLoader(RECIPE_STEP_DETAILS_LOADER, args, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        int recipeId = bundle.getInt(KEY_RECIPE_ID);
        int recipeStep = bundle.getInt(KEY_RECIPE_STEP);

        Log.d(TAG, "Creating new cursorloader for recipe: " + recipeId + " step number: " + recipeStep);

        return new CursorLoader(mContext, StepEntry.buildUriForRecipeStep(recipeId, recipeStep),
                RECIPE_STEP_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallbacks.onLoadFinished(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallbacks.onLoaderReset();
    }
}
