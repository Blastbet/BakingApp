package com.blastbet.nanodegree.bakingapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.blastbet.nanodegree.bakingapp.data.RecipeStepDetailsLoader;
import com.blastbet.nanodegree.bakingapp.sync.RecipeSyncAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.blastbet.nanodegree.bakingapp.connection.ConnectivityMonitor.NETWORK_CONNECTIVITY_STATE_KEY;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

/**
 * Created by ilkka on 25.11.2017.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeStepDetailsFragmentTest {

    private static final String TAG = RecipeStepDetailsFragmentTest.class.getSimpleName();

    private static final int RECIPE_ID = 1000000;
    private static final int RECIPE_INIT_STEP = 2;
    private static final int RECIPE_STEP_COUNT = 1000;

    public MockRecipeStepDetailsLoader recipeStepDetailsLoader;

    @BeforeClass
    public static void setup() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.setIsSyncable(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority), 0);
    }

    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

    @Rule
    public FragmentTestRule<TestActivity, RecipeStepDetailsFragment> fragmentTestRule =
            new FragmentTestRule<TestActivity, RecipeStepDetailsFragment>(
                    TestActivity.class, RecipeStepDetailsFragment.class, true, true) {

                @Override
                protected RecipeStepDetailsFragment createFragment() {
                    TestActivity.onlyLandscape = false;
                    RecipeStepDetailsFragment frag = RecipeStepDetailsFragment.newInstance(RECIPE_ID, RECIPE_INIT_STEP, RECIPE_STEP_COUNT);
                    recipeStepDetailsLoader = new MockRecipeStepDetailsLoader(InstrumentationRegistry.getTargetContext(), getActivity().getSupportLoaderManager(), frag);
                    frag.mLoader = recipeStepDetailsLoader;
                    return frag;
                }


            };


    @Test
    public void noNetworkNotificationWhenNoNetwork() throws Exception {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, false);
        editor.apply();

        final String alert = InstrumentationRegistry.getTargetContext().getString(R.string.network_connectivity_alert);
        onView(withId(R.id.text_player_alert)).check(matches(ViewMatchers.withText(alert)));
    }

    @Test
    public void noDataNotificationWhenNotLoaded() throws Exception {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, true);
        editor.apply();

        final String alert = InstrumentationRegistry.getTargetContext().getString(R.string.step_data_unavailable_alert);
        onView(withId(R.id.text_player_alert)).check(matches(ViewMatchers.withText(alert)));
        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.withText(alert)));
    }


    @Test
    public void navigationButtonsDisplayed_Mobile() throws Exception {
        onView(withId(R.id.button_left)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.button_right)).check(matches(ViewMatchers.isDisplayed()));
    }

    private Cursor getCursorWithStepDetails(int id, int index, String description, String videoUrl) {
        MatrixCursor cursor = new MatrixCursor(RecipeStepDetailsLoader.RECIPE_STEP_COLUMNS);

        cursor.addRow(new Object[] {id, index, description, videoUrl} );
        return cursor;
    }

    @Test
    public void stepDetailsLoaded() throws Exception {
        final int INDEX = 999;
        final String DESCRIPTION = "TEST STEP INSTRUCTIONS";
        final String URL = "https://foo.bar/dummyvideo";

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, true);
        editor.apply();

        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeStepDetailsLoader.getLoaderId(),
                            getCursorWithStepDetails(RECIPE_ID, INDEX, DESCRIPTION, URL));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.withText(DESCRIPTION)));
    }

    @Test
    public void testNavigationPrevious_recipeIdDecrementsWhenNotFirst() throws Exception {
        final int INDEX = 1;
        final String DESCRIPTION = "TEST STEP INSTRUCTIONS";
        final String URL = "https://foo.bar/dummyvideo";

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, true);
        editor.apply();


        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeStepDetailsLoader.getLoaderId(),
                            getCursorWithStepDetails(RECIPE_ID, INDEX, DESCRIPTION, URL));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.withText(DESCRIPTION)));

        onView(withId(R.id.button_left)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.button_right)).check(matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.button_left)).perform(click());

        assertEquals(RECIPE_ID, recipeStepDetailsLoader.recipeId);
        assertEquals(INDEX - 1, recipeStepDetailsLoader.recipeStep);
    }

    @Test
    public void testNavigationPrevious_disabledWhenFirst() throws Exception {
        final int INDEX = 0;
        final String DESCRIPTION = "TEST STEP INSTRUCTIONS";
        final String URL = "https://foo.bar/dummyvideo";

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, true);
        editor.apply();

        recipeStepDetailsLoader.recipeStep = INDEX;

        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeStepDetailsLoader.getLoaderId(),
                            getCursorWithStepDetails(RECIPE_ID, INDEX, DESCRIPTION, URL));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.withText(DESCRIPTION)));

        onView(withId(R.id.button_left)).check(matches(not(ViewMatchers.isEnabled())));
        onView(withId(R.id.button_left)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationNext_recipeIdIncrementsWhenNotLast() throws Exception {
        final int INDEX = RECIPE_STEP_COUNT - 2;
        final String DESCRIPTION = "TEST STEP INSTRUCTIONS";
        final String URL = "https://foo.bar/dummyvideo";

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, true);
        editor.apply();


        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeStepDetailsLoader.getLoaderId(),
                            getCursorWithStepDetails(RECIPE_ID, INDEX, DESCRIPTION, URL));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.withText(DESCRIPTION)));

        onView(withId(R.id.button_left)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.button_right)).check(matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.button_right)).perform(click());

        assertEquals(RECIPE_ID, recipeStepDetailsLoader.recipeId);
        assertEquals(INDEX + 1, recipeStepDetailsLoader.recipeStep);
    }

    @Test
    public void testNavigationNext_disabledWhenLast() throws Exception {
        final int INDEX = RECIPE_STEP_COUNT - 1;
        final String DESCRIPTION = "TEST STEP INSTRUCTIONS";
        final String URL = "https://foo.bar/dummyvideo";

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, true);
        editor.apply();

        recipeStepDetailsLoader.recipeStep = INDEX;

        try {
            uiThreadTestRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentTestRule.getFragment().onLoadFinished(recipeStepDetailsLoader.getLoaderId(),
                            getCursorWithStepDetails(RECIPE_ID, INDEX, DESCRIPTION, URL));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.text_recipe_step_instruction)).check(matches(ViewMatchers.withText(DESCRIPTION)));

        onView(withId(R.id.button_right)).check(matches(not(ViewMatchers.isEnabled())));
        onView(withId(R.id.button_right)).check(matches(ViewMatchers.isDisplayed()));
    }


    @AfterClass
    public static void teardown() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver.cancelSync(RecipeSyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority));
    }
}
