package com.blastbet.nanodegree.bakingapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.blastbet.nanodegree.bakingapp.data.RecipeIngredientsLoader;
import com.blastbet.nanodegree.bakingapp.sync.RecipeSyncAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by ilkka on 25.11.2017.
 */

@RunWith(AndroidJUnit4.class)
public class IngredientFragmentTest {

    private static final String TAG = IngredientFragmentTest.class.getSimpleName();

    private static final int RECIPE_ID = 1234;

    private RecipeIngredientsLoader recipeIngredientsLoader;

    @BeforeClass
    public static void setup() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.setIsSyncable(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority), 0);
    }

    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

    @Rule
    public FragmentTestRule<TestActivity, IngredientFragment> fragmentTestRule =
            new FragmentTestRule<TestActivity, IngredientFragment>(
                    TestActivity.class, IngredientFragment.class, true, true) {

                @Override
                protected IngredientFragment createFragment() {
                    recipeIngredientsLoader = Mockito.mock(RecipeIngredientsLoader.class);

                    IngredientFragment frag = IngredientFragment.newInstance(RECIPE_ID);
                    frag.mLoader = recipeIngredientsLoader;
                    return frag;
                }
            };


    @Test
    public void emptyStepListWhenNotLoaded() throws Exception {
        Log.d(TAG, "emptyStepListWhenNotLoaded start");

        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(0));
    }

    private Cursor getCursorWithSingleIngredient(int id, String name, String measure, double quantity) {
        MatrixCursor cursor = new MatrixCursor(RecipeIngredientsLoader.RECIPE_INGREDIENT_COLUMNS);

        cursor.addRow(new Object[] {id, name, measure, quantity} );
        return cursor;
    }

    @Test
    public void singleStepInList() throws Exception {
        final String NAME = "Test Ingredient";
        final String MEASURE = "Test Measure";
        final double QUANTITY = 99.1234001d;

        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(0));

        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeIngredientsLoader.getLoaderId(),
                            getCursorWithSingleIngredient(RECIPE_ID, NAME, MEASURE, QUANTITY));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(1));

        onView(withId(R.id.ingredients_card)).perform(click());
        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(0));

     }

    @AfterClass
    public static void teardown() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.cancelSync(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority));
    }
}
