package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import static org.junit.Assert.*;

/**
 * Created by ilkka on 2.9.2017.
 */

public class TestUtilities {
    private static final String LOG_TAG = TestUtilities.class.getSimpleName();

    static ContentValues getTestRecipe(long recipeId) {
        ContentValues testRecipe = new ContentValues();
        testRecipe.put(RecipeEntry.COLUMN_ID, recipeId);
        testRecipe.put(RecipeEntry.COLUMN_NAME, UUID.randomUUID().toString());
        testRecipe.put(RecipeEntry.COLUMN_SERVINGS, Integer.valueOf(new Random().nextInt(10) + 5));
        testRecipe.put(RecipeEntry.COLUMN_IMAGE, UUID.randomUUID().toString());
        return testRecipe;
    }

    static Vector<ContentValues> getTestRecipeSteps(long recipeId, int length) {
        Vector<ContentValues> valuesArray = new Vector<>(length);
        for (int i = 0; i < length; i++) {
            ContentValues testRecipeStep = new ContentValues();
            testRecipeStep.put(StepEntry.COLUMN_INDEX, i);
            testRecipeStep.put(StepEntry.COLUMN_RECIPE_ID, recipeId);
            testRecipeStep.put(StepEntry.COLUMN_DESCRIPTION, UUID.randomUUID().toString());
            testRecipeStep.put(StepEntry.COLUMN_SHORT_DESCRIPTION, UUID.randomUUID().toString());
            testRecipeStep.put(StepEntry.COLUMN_DESCRIPTION, UUID.randomUUID().toString());
            testRecipeStep.put(StepEntry.COLUMN_VIDEO_URL, UUID.randomUUID().toString());
            testRecipeStep.put(StepEntry.COLUMN_THUMBNAIL_URL, UUID.randomUUID().toString());
            valuesArray.add(testRecipeStep);
        }
        return valuesArray;
    }

    static Vector<ContentValues> getTestRecipeIngredients(long recipeId, int length) {
        Vector<ContentValues> valuesArray = new Vector<>(length);
        for (int i = 0; i < length; i++) {
            ContentValues testRecipeIngredient = new ContentValues();
            testRecipeIngredient.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
            testRecipeIngredient.put(IngredientEntry.COLUMN_NAME, UUID.randomUUID().toString());
            testRecipeIngredient.put(IngredientEntry.COLUMN_MEASURE, UUID.randomUUID().toString());
            testRecipeIngredient.put(IngredientEntry.COLUMN_QUANTITY, new Random().nextInt(100) / 100.0);
            valuesArray.add(testRecipeIngredient);
        }
        return valuesArray;
    }

    static long insertContent(Context context, String dataDescription,
                              String tableName, ContentValues content) {
        SQLiteDatabase db = new RecipeDBHelper(context).getWritableDatabase();

        long rowId = db.insert(tableName, null, content);

        assertFalse("Error: Could not insert " + dataDescription + " into database!", rowId == -1);

        Log.i(LOG_TAG, "Inserting content \"" + content.toString() + " to table: " + tableName);
        // Do a simple query to table where the data was inserted.
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        assertTrue("Error: No entries were found from the table \"" + tableName + "\" after insert",
                cursor.moveToFirst());

        ContentValues queriedContent = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, queriedContent);

        Log.i(LOG_TAG, "Database query result: " + queriedContent.toString());

        db.close();

        return rowId;
    }

    static void insertContentVector(Context context, String dataDescription,
                              String tableName, Vector<ContentValues> values) {

        SQLiteDatabase db = new RecipeDBHelper(context).getWritableDatabase();

        for (ContentValues cv : values) {
            long rowId = db.insert(tableName, null, cv);
            assertFalse("Error: Could not insert " + dataDescription + " into database!", rowId == -1);
        }

        // Do a simple query to table where the data was inserted.
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        assertTrue("Error: No entries were found from the table \"" + tableName + "\" after insert",
                cursor.moveToFirst());

        ContentValues queriedContent = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, queriedContent);

        Log.i(LOG_TAG, "Database query result: " + queriedContent.toString());

        db.close();
    }


    static long insertTestRecipe(Context context, long id) {
        ContentValues values = getTestRecipe(id);
        return insertContent(context, "Test recipe", RecipeEntry.TABLE_NAME, values);
    }

    static void insertTestRecipeSteps(Context context, long recipeId, int numSteps) {
        Vector<ContentValues> values = getTestRecipeSteps(recipeId, numSteps);
        insertContentVector(context, "Test recipe steps", StepEntry.TABLE_NAME, values);
    }

    static void insertTestRecipeIngredients(Context context, long recipeId, int numIngredients) {
        Vector<ContentValues> values = getTestRecipeSteps(recipeId, numIngredients);
        insertContentVector(context, "Test recipe ingredients", IngredientEntry.TABLE_NAME, values);
    }

    static void validateRecord(Cursor cursor, ContentValues expected) {
        Set<Map.Entry<String, Object>> expectedValues = expected.valueSet();
        for (Map.Entry<String, Object> entry : expectedValues) {
            String column = entry.getKey();
            int columnIndex = cursor.getColumnIndex(column);
            assertFalse("Error: Column \"" + column + "\" was not found!", columnIndex == -1);
            String expectedValue = entry.getValue().toString();
            String value = cursor.getString(columnIndex);
            assertEquals("Error: Column \"" + column + "\" was \"" + value + "\", \"" +
                            expectedValue + "\" was expected!",
                    expectedValue, value);
        }
    }

}
