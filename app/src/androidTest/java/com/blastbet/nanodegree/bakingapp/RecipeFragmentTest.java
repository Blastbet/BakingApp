package com.blastbet.nanodegree.bakingapp;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.blastbet.nanodegree.bakingapp.data.RecipeLoader;
import com.blastbet.nanodegree.bakingapp.sync.RecipeSyncAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by ilkka on 25.11.2017.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeFragmentTest {

    private static final String TAG = RecipeFragmentTest.class.getSimpleName();

    RecipeLoader recipeLoader;

    @BeforeClass
    public static void setup() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.setIsSyncable(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority), 0);
    }

    @Rule
    public FragmentTestRule<TestActivity, RecipeFragment> fragmentTestRule =
            new FragmentTestRule<TestActivity, RecipeFragment>(
                    TestActivity.class, RecipeFragment.class, true, true) {

                @Override
                protected RecipeFragment createFragment() {
                    recipeLoader = Mockito.mock(RecipeLoader.class);

                    RecipeFragment frag = super.createFragment();
                    frag.mRecipeLoader = recipeLoader;
                    return frag;
                }
            };

    @Test
    public void emptyRecipeListWhenNotLoaded() throws Exception {
        onView(withId(R.id.list_recipe)).check(new RecyclerViewItemCountAssertion(0));
        onView(withId(R.id.list_recipe)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.empty_view)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.empty_view)).check(matches(ViewMatchers.withText(R.string.no_data)));
    }

    Cursor getCursorWithSingleRecipe(int id, String name, String imageUrl, int servings) {
        MatrixCursor cursor = new MatrixCursor(RecipeLoader.RECIPE_COLUMNS);

        cursor.addRow(new Object[] {id, name, imageUrl, servings} );
        return cursor;
    }

    @Test
    public void singleRecipeInRecipeList() throws Exception {
        final int ID = 0;
        final String NAME = "Test recipe";
        final String IMAGE_URL = "";
        final int SERVINGS = 16;
        final String SERVINGS_TEXT = "x 16";

        fragmentTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentTestRule.getFragment().onLoadFinished(recipeLoader.getLoaderId(),
                        getCursorWithSingleRecipe(ID, NAME, IMAGE_URL, SERVINGS));
            }
        });

        onView(withId(R.id.list_recipe)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.list_recipe)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_list_item)).check(matches(ViewMatchers.withText(NAME)));
        onView(withId(R.id.text_recipe_servings)).check(matches(ViewMatchers.withText(SERVINGS_TEXT)));

        onView(withId(R.id.empty_view)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void singleRecipeInList_clickTriggersCallback() throws Exception {
        final int ID = 5;
        final String NAME = "Test recipe click";
        final String IMAGE_URL = "";
        final int SERVINGS = -1;

        fragmentTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentTestRule.getFragment().onLoadFinished(recipeLoader.getLoaderId(),
                        getCursorWithSingleRecipe(ID, NAME, IMAGE_URL, SERVINGS));
            }
        });

        onView(withId(R.id.list_recipe)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.list_recipe)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_list_item)).check(matches(ViewMatchers.withText(NAME)));

        onView(withId(R.id.card_recipe_list_item)).perform(click());

        TestActivity activity = fragmentTestRule.getActivity();
        assertEquals(ID, activity.mRecipeId);
        assertEquals(NAME, activity.mRecipeName);
    }

    @AfterClass
    public static void teardown() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.cancelSync(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority));
    }

}
