package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
/**
 * Created by ilkka on 2.9.2017.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeProviderTest {

    @Test
    public void deleteAllRecordsFromProvider() throws Exception{
        Context context = InstrumentationRegistry.getTargetContext();
        context.getContentResolver().delete(RecipeEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(IngredientEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(StepEntry.CONTENT_URI, null, null);
        Cursor cursor = context.getContentResolver().query(
                RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Recipe table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = context.getContentResolver().query(
                StepEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Step table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = context.getContentResolver().query(
                IngredientEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Ingredient table during delete", 0, cursor.getCount());
        cursor.close();
    }
}

