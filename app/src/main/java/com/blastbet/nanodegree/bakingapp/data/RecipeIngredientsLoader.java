package com.blastbet.nanodegree.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;

/**
 * Created by ilkka on 9.9.2017.
 */

public class RecipeIngredientsLoader extends BakingLoader {

    public static final String[] RECIPE_INGREDIENT_COLUMNS = {
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_RECIPE_ID,
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_NAME,
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_MEASURE,
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_QUANTITY
    };

    //static final int COL_ROW_ID = 0;
    public static final int COL_RECIPE_ID = 0;
    public static final int COL_INGREDIENT_NAME        = 1;
    public static final int COL_INGREDIENT_MEASURE     = 2;
    public static final int COL_INGREDIENT_QUANTITY    = 3;

    private static final int RECIPE_INGREDIENT_LOADER = 2;

    public RecipeIngredientsLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        super(context, manager, callbacks);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        int recipeId = bundle.getInt(KEY_RECIPE_ID);
        return new CursorLoader(getContext(), IngredientEntry.buildUri(recipeId),
                RECIPE_INGREDIENT_COLUMNS,
                null, null, null);
    }

    @Override
    public int getLoaderId() {
        return RECIPE_INGREDIENT_LOADER;
    }
}
