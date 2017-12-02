package com.blastbet.nanodegree.bakingapp.data;

import com.blastbet.nanodegree.bakingapp.data.RecipeContract.RecipeEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.IngredientEntry;
import com.blastbet.nanodegree.bakingapp.data.RecipeContract.StepEntry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ilkka on 2.9.2017.
 */

public class RecipeProvider extends ContentProvider {

    private static final String TAG = RecipeProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int RECIPES = 100;
    private static final int RECIPES_WITH_ID = 101;

    private static final int INGREDIENTS = 200;
    private static final int INGREDIENTS_WITH_RECIPE_ID = 201;

    private static final int STEPS = 300;
    private static final int STEPS_WITH_RECIPE_ID = 301;
    private static final int STEPS_WITH_INDEX = 302;

    RecipeDBHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = RecipeContract.CONTENT_AUTHORITY;
        final String recipePath = RecipeContract.PATH_RECIPES + "/#";

        matcher.addURI(AUTHORITY, RecipeContract.PATH_RECIPES, RECIPES);
        matcher.addURI(AUTHORITY, recipePath, RECIPES_WITH_ID);

        matcher.addURI(AUTHORITY, RecipeContract.PATH_INGREDIENTS, INGREDIENTS);

        matcher.addURI(AUTHORITY, recipePath +
                "/" + RecipeContract.PATH_INGREDIENTS, INGREDIENTS_WITH_RECIPE_ID);

        matcher.addURI(AUTHORITY, RecipeContract.PATH_STEPS, STEPS);

        matcher.addURI(AUTHORITY, recipePath +
                "/" + RecipeContract.PATH_STEPS, STEPS_WITH_RECIPE_ID);

        matcher.addURI(AUTHORITY, recipePath +
                "/" + RecipeContract.PATH_STEPS + "/#", STEPS_WITH_INDEX);

        Log.d(TAG, "matcher STEPS_WITH_INDEX: " + recipePath +
                "/" + RecipeContract.PATH_STEPS + "/#");
        return matcher;
    }

    private static final String sRecipeIdSelection =
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_ID + " = ? ";

    private static final String sIngredientSelection =
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_RECIPE_ID + " = ? ";

    private static final String sStepSelection =
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID + " = ? ";

    private static final String sStepIndexSelection =
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID + " = ? AND " +
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_INDEX + " = ?";

    private static final String sIndexSortAscending = StepEntry.COLUMN_INDEX + " ASC";
    private static final String sIdSortAscending = StepEntry.COLUMN_INDEX + " ASC";
    private static final String sNameSortAscending = StepEntry.COLUMN_INDEX + " ASC";

    private static Long getIdFromUri(Uri uri) {
        return Long.parseLong(uri.getPathSegments().get(1));
    }

    private static Long getStepFromUri(Uri uri) {
        return Long.parseLong(uri.getPathSegments().get(3));
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RecipeDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "At query " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);

        long recipeId;

        if (selection != null) {
            Log.d(TAG, "selection: " + selection + " *** with args: " + selectionArgs[0].toString());
        }
        Cursor retCursor = null;
        switch (match) {
            case RECIPES:
                retCursor = db.query(RecipeEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        RecipeEntry.COLUMN_ID + " ASC");
                break;
            case RECIPES_WITH_ID:
                recipeId = getIdFromUri(uri);
                retCursor = db.query(RecipeEntry.TABLE_NAME,
                        projection, sRecipeIdSelection, new String[]{Long.toString(recipeId)},
                        null, null, sortOrder);
                break;
            case INGREDIENTS:
                retCursor = db.query(IngredientEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        IngredientEntry.COLUMN_RECIPE_ID + " ASC");
                break;
            case INGREDIENTS_WITH_RECIPE_ID:
                recipeId = getIdFromUri(uri);
                Log.d(TAG, "Querying ingredients for recipe no " + recipeId);
                retCursor = db.query(IngredientEntry.TABLE_NAME,
                        projection, sIngredientSelection, new String[]{Long.toString(recipeId)},
                        null, null,
                        IngredientEntry._ID + " ASC");
                break;
            case STEPS:
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        StepEntry.COLUMN_RECIPE_ID + " ASC");
                break;
            case STEPS_WITH_RECIPE_ID:
                recipeId = getIdFromUri(uri);
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection, sStepSelection, new String[]{Long.toString(recipeId)},
                        null, null,
                        StepEntry.COLUMN_INDEX + " ASC");
                break;
            case STEPS_WITH_INDEX:
                recipeId = getIdFromUri(uri);
                long stepIndex = getStepFromUri(uri);
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection, sStepIndexSelection,
                        new String[]{Long.toString(recipeId), Long.toString(stepIndex)},
                        null, null,
                        StepEntry.COLUMN_INDEX + " ASC");
                break;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                return RecipeEntry.CONTENT_TYPE;
            case RECIPES_WITH_ID:
                return RecipeEntry.CONTENT_ITEM_TYPE;
            case INGREDIENTS_WITH_RECIPE_ID:
                return IngredientEntry.CONTENT_TYPE;
            case STEPS_WITH_RECIPE_ID:
                return StepEntry.CONTENT_TYPE;
            case STEPS_WITH_INDEX:
                return StepEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "At insert " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {
            case RECIPES:
                _id = db.insertWithOnConflict(
                        RecipeEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = RecipeEntry.CONTENT_URI;
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case INGREDIENTS_WITH_RECIPE_ID:
                _id = db.insertWithOnConflict(
                        IngredientEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = IngredientEntry.buildUri(
                            contentValues.getAsInteger(IngredientEntry.COLUMN_RECIPE_ID));
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case STEPS_WITH_RECIPE_ID:
                _id = db.insertWithOnConflict(
                        StepEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = StepEntry.buildUri(
                            contentValues.getAsInteger(StepEntry.COLUMN_RECIPE_ID));
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case STEPS_WITH_INDEX:
                _id = db.insertWithOnConflict(
                        StepEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = StepEntry.buildUriForRecipeStep(getIdFromUri(uri), getStepFromUri(uri));
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (returnUri != null) {
            getContext().getContentResolver().notifyChange(returnUri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsDeleted;
        long id;
        Log.d(TAG, "At delete " + uri.toString() + " match: " + Integer.toString(match));

        switch (match) {
            case RECIPES:
                numRowsDeleted = db.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPES_WITH_ID:
                id = getIdFromUri(uri);
                numRowsDeleted = db.delete(RecipeEntry.TABLE_NAME, sRecipeIdSelection,
                        new String[]{Long.toString(id)});
                break;

            case INGREDIENTS:
                numRowsDeleted = db.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case INGREDIENTS_WITH_RECIPE_ID:
//                numRowsDeleted = db.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case INGREDIENTS_WITH_ID:
                id = getIdFromUri(uri);
                numRowsDeleted = db.delete(IngredientEntry.TABLE_NAME, sIngredientSelection,
                        new String[]{Long.toString(id)});
                break;

            case STEPS:
                numRowsDeleted = db.delete(StepEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case STEPS_WITH_RECIPE_ID:
//                numRowsDeleted = db.delete(StepEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case STEPS_WITH_ID:
                id = getIdFromUri(uri);
                numRowsDeleted = db.delete(StepEntry.TABLE_NAME, sStepSelection,
                        new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsUpdated;
        long id;

        Log.d(TAG, "At update " + uri.toString());

        switch (match) {
            case RECIPES:
                numRowsUpdated = db.update(RecipeEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case RECIPES_WITH_ID:
                id = getIdFromUri(uri);
                numRowsUpdated = db.update(RecipeEntry.TABLE_NAME, contentValues, sRecipeIdSelection,
                        new String[]{Long.toString(id)});
                break;
            case INGREDIENTS_WITH_RECIPE_ID:
//                numRowsUpdated = db.update(IngredientEntry.TABLE_NAME, contentValues, selection, selectionArgs);
//                break;
//            case INGREDIENTS_WITH_ID:
                id = getIdFromUri(uri);
                numRowsUpdated = db.update(IngredientEntry.TABLE_NAME, contentValues, sIngredientSelection,
                        new String[]{Long.toString(id)});
                break;
            case STEPS_WITH_RECIPE_ID:
//                numRowsUpdated = db.update(StepEntry.TABLE_NAME, contentValues, selection, selectionArgs);
//                break;
//            case STEPS_WITH_ID:
                id = getIdFromUri(uri);
                numRowsUpdated = db.update(StepEntry.TABLE_NAME, contentValues, sStepSelection,
                        new String[]{Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numInserted = 0;

        String table;

        switch (match) {
            case RECIPES:
                table = RecipeEntry.TABLE_NAME;
                break;
            case STEPS_WITH_RECIPE_ID:
                table = StepEntry.TABLE_NAME;
                break;
            case INGREDIENTS_WITH_RECIPE_ID:
                table = IngredientEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                Log.v(TAG, "Inserting to recipe table: " + value.toString());
                final long _id = db.insertWithOnConflict(
                        table,
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_IGNORE);
                if (_id != -1) numInserted++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.v(TAG, "notifying for change in uri: " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return numInserted;
    }
}
