package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ilkka on 2.9.2017.
 */

public class RecipeDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    protected static final String DATABASE_NAME = "recipe.db";

    public RecipeDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints by default
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_RECIPE_ID_REFERENCE = "REFERENCES " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry.COLUMN_ID + ")";

        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeEntry.TABLE_NAME + "(" +
                RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                RecipeEntry.COLUMN_ID + " INTEGER UNIQUE NOT NULL, " +
                RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, " +
                RecipeEntry.COLUMN_IMAGE + " TEXT" +
                " );";

        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + StepEntry.TABLE_NAME + "(" +
                StepEntry._ID                      + " INTEGER PRIMARY KEY, " +
                StepEntry.COLUMN_RECIPE_ID         + " INTEGER NOT NULL, " +
                StepEntry.COLUMN_INDEX             + " INTEGER NOT NULL, " +
                StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                StepEntry.COLUMN_DESCRIPTION       + " TEXT NOT NULL, " +
                StepEntry.COLUMN_VIDEO_URL         + " TEXT, " +
                StepEntry.COLUMN_THUMBNAIL_URL     + " TEXT, " +
                "UNIQUE (" + StepEntry.COLUMN_RECIPE_ID + ", " + StepEntry.COLUMN_INDEX + "), " +
                "FOREIGN KEY (" + StepEntry.COLUMN_RECIPE_ID + ") " + SQL_RECIPE_ID_REFERENCE +
                " );";

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + IngredientEntry.TABLE_NAME + "(" +
                IngredientEntry._ID              + " INTEGER PRIMARY KEY, " +
                IngredientEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                IngredientEntry.COLUMN_NAME      + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_MEASURE   + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_QUANTITY  + " REAL NOT NULL, " +
                "UNIQUE (" + IngredientEntry.COLUMN_RECIPE_ID + ", " + IngredientEntry.COLUMN_NAME + "), " +
                "FOREIGN KEY (" + IngredientEntry.COLUMN_RECIPE_ID + ") " + SQL_RECIPE_ID_REFERENCE +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        clearAll(sqLiteDatabase);
    }

    public void clearAll(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StepEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
