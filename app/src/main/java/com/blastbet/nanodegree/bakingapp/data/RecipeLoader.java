package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by ilkka on 9.9.2017.
 */

public class RecipeLoader extends BakingLoader {

    public static final String[] RECIPE_COLUMNS = {
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

    public static final int RECIPE_LOADER = 0;

    public RecipeLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        super(context, manager, callbacks);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(), RecipeEntry.CONTENT_URI, RECIPE_COLUMNS, null, null, null);
    }

    @Override
    public int getLoaderId() {
        return RECIPE_LOADER;
    }
}
