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

public class RecipeStepLoader extends BakingLoader {

    private static final String[] RECIPE_STEP_COLUMNS = {
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_INDEX,
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_SHORT_DESCRIPTION,
    };

    //static final int COL_ROW_ID = 0;
    public static final int COL_RECIPE_ID = 0;
    public static final int COL_STEP_INDEX             = 1;
    public static final int COL_STEP_SHORT_DESCRIPTION = 2;

    private static final int RECIPE_STEP_LOADER = 1;

    public RecipeStepLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        super(context, manager, callbacks);
    }

    @Override
    public int getLoaderId() {
        return RECIPE_STEP_LOADER;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        int recipeId = bundle.getInt(KEY_RECIPE_ID);
        final String sortOrder = StepEntry.COLUMN_INDEX + " ASC";
        return new CursorLoader(getContext(), StepEntry.buildUri(recipeId),
                RECIPE_STEP_COLUMNS,
                null, null, sortOrder);
    }
}
