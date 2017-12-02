package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Vector;

/**
 * Created by ilkka on 2.9.2017.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDBTest {
    private static final String LOG_TAG = RecipeDBTest.class.getSimpleName();

    @Test
    public void createDBTest() throws Exception {

        final HashSet<String> tableNames = new HashSet<>();
        tableNames.add(RecipeEntry.TABLE_NAME);
        tableNames.add(StepEntry.TABLE_NAME);
        tableNames.add(IngredientEntry.TABLE_NAME);

        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(RecipeDBHelper.DATABASE_NAME);

        RecipeDBHelper dbHelper = new RecipeDBHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: The database has not been created correctly", c.moveToFirst());

        do {
            tableNames.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: The was created without tables: " + tableNames.toString(),
                tableNames.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + RecipeEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for recipe table information.",
                c.moveToFirst());

        Log.d(LOG_TAG, "Cursor: " + c.toString());

        final HashSet<String> columns = new HashSet<>();
        columns.add(RecipeEntry._ID);
        columns.add(RecipeEntry.COLUMN_ID);
        columns.add(RecipeEntry.COLUMN_NAME);
        columns.add(RecipeEntry.COLUMN_SERVINGS);
        columns.add(RecipeEntry.COLUMN_IMAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                columns.isEmpty());


        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + IngredientEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for recipe table information.",
                c.moveToFirst());

        Log.d(LOG_TAG, "Cursor: " + c.toString());

        columns.add(IngredientEntry._ID);
        columns.add(IngredientEntry.COLUMN_RECIPE_ID);
        columns.add(IngredientEntry.COLUMN_NAME);
        columns.add(IngredientEntry.COLUMN_MEASURE);
        columns.add(IngredientEntry.COLUMN_QUANTITY);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                columns.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + StepEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for recipe table information.",
                c.moveToFirst());

        Log.d(LOG_TAG, "Cursor: " + c.toString());

        columns.add(StepEntry._ID);
        columns.add(StepEntry.COLUMN_RECIPE_ID);
        columns.add(StepEntry.COLUMN_INDEX);
        columns.add(StepEntry.COLUMN_DESCRIPTION);
        columns.add(StepEntry.COLUMN_SHORT_DESCRIPTION);
        columns.add(StepEntry.COLUMN_VIDEO_URL);
        columns.add(StepEntry.COLUMN_THUMBNAIL_URL);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                columns.isEmpty());

        db.close();
    }

    @Test
    public void testRecipeTableConfiguration() {

        final int RECIPE_ID = 123456;

        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(RecipeDBHelper.DATABASE_NAME);

        RecipeDBHelper dbHelper = new RecipeDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        TestUtilities.insertTestRecipe(context, RECIPE_ID);

        Vector<ContentValues> ingredients = TestUtilities.getTestRecipeIngredients(RECIPE_ID, 10);
        Vector<ContentValues> steps = TestUtilities.getTestRecipeSteps(RECIPE_ID, 10);

        TestUtilities.insertContentVector(context, "Ingredients",
                IngredientEntry.TABLE_NAME, ingredients);

        TestUtilities.insertContentVector(context, "Recipe tests",
                StepEntry.TABLE_NAME, steps);

        String ingredientSelection =
                IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_RECIPE_ID + " = ? ";

        Cursor c = db.query(
                IngredientEntry.TABLE_NAME, null,
                ingredientSelection, new String[]{Integer.toString(RECIPE_ID)},
                null,null,IngredientEntry.COLUMN_QUANTITY + " ASC");

        assertTrue("Error: The database has not been created correctly", c.moveToFirst());

        do {
            assertFalse("Error: More ingredients in the database than inserted", ingredients.isEmpty());
            ContentValues v = ingredients.remove(0);
            TestUtilities.validateRecord(c, v);
        } while (c.moveToNext());

        String stepSelection =
                StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID + " = ? ";

        c = db.query(
                StepEntry.TABLE_NAME, null,
                stepSelection, new String[]{Integer.toString(RECIPE_ID)},
                null,null,null);

        assertTrue("Error: The database has not been created correctly", c.moveToFirst());

        do {
            assertFalse("Error: More ingredients in the database than inserted", steps.isEmpty());
            ContentValues v = steps.remove(0);
            TestUtilities.validateRecord(c, v);
        } while (c.moveToNext());

        db.close();
    }
}
