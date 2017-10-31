package com.blastbet.nanodegree.bakingapp.sync;

import com.blastbet.nanodegree.bakingapp.R;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.blastbet.nanodegree.bakingapp.recipe.Ingredient;
import com.blastbet.nanodegree.bakingapp.recipe.Recipe;
import com.blastbet.nanodegree.bakingapp.recipe.RecipeStep;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilkka on 3.9.2017.
 */

public class RecipeSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = RecipeSyncAdapter.class.getSimpleName();

    private static final String RECIPE_URL = "http://go.udacity.com/android-baking-app-json";

    private static final int SYNC_INTERVAL = 60 * 60 * 24;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 24;

    public RecipeSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    HttpURLConnection connectRedirectable(String url) {
        HttpURLConnection httpConnection = null;

        try {
            boolean redirected = false;
            do {
                httpConnection = (HttpURLConnection) new URL(url).openConnection();
                httpConnection.setRequestMethod("GET");
                HttpURLConnection.setFollowRedirects(false);
                int response = httpConnection.getResponseCode();
                redirected = (response == HttpURLConnection.HTTP_MOVED_PERM ||
                        response == HttpURLConnection.HTTP_MOVED_TEMP ||
                        response == HttpURLConnection.HTTP_SEE_OTHER);
                if (redirected) {
                    url = httpConnection.getHeaderField("Location");
                    Log.i(TAG, "Redirected to: " + url);
                    httpConnection.disconnect();
                }
            } while (redirected);


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpConnection;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        Log.v(TAG, "onPerformSync");

        HttpURLConnection httpConnection = connectRedirectable(RECIPE_URL);
        InputStreamReader reader = null;

        try {
            // Read the input stream into a String
            InputStream inputStream = httpConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                Log.w(TAG, "onPerformSync() could not get input stream");
                return;
            }
            reader = new InputStreamReader(inputStream);

            Gson gson = new GsonBuilder().create();
            Type RecipeList = new TypeToken<ArrayList<Recipe>>(){}.getType();
            List<Recipe> recipeList = gson.fromJson(reader,RecipeList);

            Log.d(TAG, "Updating recipes");

            updateRecipes(recipeList);

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

    private ContentValues[] getRecipeSteps(Recipe recipe) {
        List<RecipeStep> steps = recipe.getSteps();
        ContentValues[] v = new ContentValues[steps.size()];
        for (int i = 0; i < steps.size(); i++) {
            RecipeStep s = steps.get(i);
            ContentValues cv = new ContentValues();
            cv.put(StepEntry.COLUMN_RECIPE_ID, recipe.getId());
            cv.put(StepEntry.COLUMN_INDEX, s.getIndex());
            cv.put(StepEntry.COLUMN_DESCRIPTION, s.getDescription());
            cv.put(StepEntry.COLUMN_SHORT_DESCRIPTION, s.getShortDescription());
            cv.put(StepEntry.COLUMN_VIDEO_URL, s.getVideoURL());
            cv.put(StepEntry.COLUMN_THUMBNAIL_URL, s.getThumbnailUrl());
            v[i] = cv;
        }
        return v;
    }

    private ContentValues[] getRecipeIngredients(Recipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        ContentValues[] v = new ContentValues[ingredients.size()];
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ContentValues cv = new ContentValues();
            cv.put(IngredientEntry.COLUMN_RECIPE_ID, recipe.getId());
            cv.put(IngredientEntry.COLUMN_NAME, ingredient.getName());
            cv.put(IngredientEntry.COLUMN_MEASURE, ingredient.getMeasure());
            cv.put(IngredientEntry.COLUMN_QUANTITY, ingredient.getQuantity());
            v[i] = cv;
        }
        return v;
    }

    private ContentValues getBaseRecipe(Recipe recipe) {
        ContentValues retVal = new ContentValues();
        retVal.put(RecipeEntry.COLUMN_ID, recipe.getId());
        retVal.put(RecipeEntry.COLUMN_NAME, recipe.getName());
        retVal.put(RecipeEntry.COLUMN_SERVINGS, recipe.getServings());
        retVal.put(RecipeEntry.COLUMN_IMAGE, recipe.getImage());
        return retVal;
    }

    private void updateRecipes(List<Recipe> recipeList) {
        ContentValues[] v;
        ContentValues cv;
        ContentResolver resolver = getContext().getContentResolver();

        Log.d(TAG, "Updating recipes, count: " + Integer.toString(recipeList.size()));
        for (Recipe r : recipeList) {
            Log.d(TAG, "Inserting recipe: " + r.getName());
            cv = getBaseRecipe(r);
            resolver.insert(RecipeEntry.CONTENT_URI, cv);
            v = getRecipeIngredients(r);
            resolver.bulkInsert(IngredientEntry.buildUri(r.getId()), v);
            v = getRecipeSteps(r);
            resolver.bulkInsert(StepEntry.buildUri(r.getId()), v);
        }
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // Check if account exists
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void syncRecipesNow(Context context) {
        syncNow(getSyncAccount(context), null, context.getString(R.string.content_authority));
        Log.v(TAG, "Synchronizing recipes");
    }

    private static void syncNow(final Account account, Bundle extras, final String authority) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Log.v(TAG, "Synchronizing recipes: " + extras.toString());
        ContentResolver.requestSync(account, authority, extras);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        // Configure periodic sync
        final String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME)
                    .setSyncAdapter(newAccount, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(newAccount, authority, new Bundle(), SYNC_INTERVAL);
        }


        // TODO: Enable periodic sync ?
        // ContentResolver.setSyncAutomatically(newAccount, authority, true);

        /*
         * Finally, let's do a sync to get things started
         */
        Log.v(TAG, "onAccountCreated - Synchronizing recipes");
        syncNow(newAccount, null, authority);
    }

}
