package com.blastbet.nanodegree.bakingapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.blastbet.nanodegree.bakingapp.data.RecipeStepLoader;
import com.blastbet.nanodegree.bakingapp.sync.RecipeSyncAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Created by ilkka on 25.11.2017.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsFragmentTest {

    private static final String TAG = RecipeDetailsFragmentTest.class.getSimpleName();

    private static final String RECIPENAME = "TEST RECIPE";
    private static final int RECIPE_ID = 1000000;

    public RecipeStepLoader recipeStepLoader;

    @BeforeClass
    public static void setup() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.setIsSyncable(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority), 0);
    }

    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

    @Rule
    public FragmentTestRule<TestActivity, RecipeDetailsFragment> fragmentTestRule =
            new FragmentTestRule<TestActivity, RecipeDetailsFragment>(
                    TestActivity.class, RecipeDetailsFragment.class, true, true) {

                @Override
                protected RecipeDetailsFragment createFragment() {
                    recipeStepLoader = Mockito.mock(RecipeStepLoader.class);

                    RecipeDetailsFragment frag = RecipeDetailsFragment.newInstance(RECIPE_ID, RECIPENAME);
                    frag.mStepLoader = recipeStepLoader;
                    return frag;
                }
            };


    @Test
    public void emptyStepListWhenNotLoaded() throws Exception {
        Log.d(TAG, "emptyStepListWhenNotLoaded start");
        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(0));
        onView(withId(R.id.list)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.empty_view)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.empty_view)).check(matches(ViewMatchers.withText(R.string.no_data)));
    }

    Cursor getCursorWithSingleStep(int id, int index, String description) {
        MatrixCursor cursor = new MatrixCursor(RecipeStepLoader.RECIPE_STEP_COLUMNS);

        cursor.addRow(new Object[] {id, index, description} );
        return cursor;
    }

    @Test
    public void singleStepInList() throws Exception {
        final int INDEX = 999;
        final String DESCRIPTION = "TEST STEP";

        Log.d(TAG, "singleStepInList start");


        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeStepLoader.getLoaderId(),
                            getCursorWithSingleStep(RECIPE_ID, INDEX, DESCRIPTION));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Log.d(TAG, "Cursor swapped... maybe");

        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.list)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_list_item)).check(matches(ViewMatchers.withText(DESCRIPTION)));

        onView(withId(R.id.empty_view)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void singleRecipeInList_clickTriggersCallback() throws Exception {
        // The ID and INDEX should be fine with whatever values
        final int INDEX = 999;
        final String DESCRIPTION = "TEST STEP CLICK";

        Log.d(TAG, "singleRecipeInList_clickTriggersCallback start");
        fragmentTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentTestRule.getFragment().onLoadFinished(recipeStepLoader.getLoaderId(),
                        getCursorWithSingleStep(RECIPE_ID, INDEX, DESCRIPTION));
            }
        });

        onView(withId(R.id.list)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.list)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_list_item)).check(matches(ViewMatchers.withText(DESCRIPTION)));

        onView(withId(R.id.card_recipe_step_list_item)).perform(click());

        TestActivity activity = fragmentTestRule.getActivity();

        assertEquals(RECIPE_ID, activity.mRecipeId);
        assertEquals(INDEX, activity.mStepNumber);
        assertEquals(1, activity.mStepCount);

    }

    @AfterClass
    public static void teardown() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.cancelSync(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority));
    }
}
