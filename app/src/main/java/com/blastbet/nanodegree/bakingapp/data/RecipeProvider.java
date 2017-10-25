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

    private static final String LOG_TAG = RecipeProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int RECIPE             = 100;
    private static final int RECIPE_WITH_ID     = 101;

    private static final int INGREDIENT         = 200;
    private static final int INGREDIENT_WITH_ID = 201;

    private static final int STEP               = 300;
    private static final int STEP_WITH_ID       = 301;

    RecipeDBHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = RecipeContract.CONTENT_AUTHORITY;

        matcher.addURI(AUTHORITY, RecipeContract.PATH_RECIPE,            RECIPE);
        matcher.addURI(AUTHORITY, RecipeContract.PATH_RECIPE + "/#",     RECIPE_WITH_ID);

        matcher.addURI(AUTHORITY, RecipeContract.PATH_INGREDIENT,        INGREDIENT);
        matcher.addURI(AUTHORITY, RecipeContract.PATH_INGREDIENT + "/#", INGREDIENT_WITH_ID);

        matcher.addURI(AUTHORITY, RecipeContract.PATH_STEP,        STEP);
        matcher.addURI(AUTHORITY, RecipeContract.PATH_STEP + "/#", STEP_WITH_ID);

        return matcher;
    }

    private static final String sRecipeIdSelection =
            RecipeEntry.TABLE_NAME + "." + RecipeEntry.COLUMN_ID + " = ? ";

    private static final String sIngredientIdSelection =
            IngredientEntry.TABLE_NAME + "." + IngredientEntry.COLUMN_RECIPE_ID + " = ? ";

    private static final String sStepIdSelection =
            StepEntry.TABLE_NAME + "." + StepEntry.COLUMN_RECIPE_ID + " = ? ";

    private static final String sIndexSortAscending = StepEntry.COLUMN_INDEX + " ASC";
    private static final String sIdSortAscending = StepEntry.COLUMN_INDEX + " ASC";
    private static final String sNameSortAscending = StepEntry.COLUMN_INDEX + " ASC";

    private static Long getIdFromUri(Uri uri) {
        return Long.parseLong(uri.getPathSegments().get(1));
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RecipeDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "At query " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);

        long recipeId;

        Cursor retCursor = null;
        switch (match) {
            case RECIPE:
                retCursor = db.query(RecipeEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        RecipeEntry.COLUMN_ID + " ASC");
                break;
            case RECIPE_WITH_ID:
                recipeId = getIdFromUri(uri);
                retCursor = db.query(RecipeEntry.TABLE_NAME,
                        projection, sRecipeIdSelection, new String[]{Long.toString(recipeId)},
                        null, null, sortOrder);
                break;
            case INGREDIENT:
                retCursor = db.query(IngredientEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        IngredientEntry.COLUMN_RECIPE_ID + " ASC");
                break;
            case INGREDIENT_WITH_ID:
                recipeId = getIdFromUri(uri);
                retCursor = db.query(IngredientEntry.TABLE_NAME,
                        projection, sIngredientIdSelection, new String[]{Long.toString(recipeId)},
                        null, null,
                        IngredientEntry._ID + " ASC");
                break;
            case STEP:
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        StepEntry.COLUMN_RECIPE_ID + " ASC");
                break;
            case STEP_WITH_ID:
                recipeId = getIdFromUri(uri);
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection, sStepIdSelection, new String[]{Long.toString(recipeId)},
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
            case RECIPE:
                return RecipeEntry.CONTENT_TYPE;
            case RECIPE_WITH_ID:
                return RecipeEntry.CONTENT_ITEM_TYPE;
            case INGREDIENT_WITH_ID:
                return IngredientEntry.CONTENT_TYPE;
            case STEP_WITH_ID:
                return StepEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(LOG_TAG, "At insert " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {
            case RECIPE:
                _id = db.insertWithOnConflict(
                        RecipeEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = RecipeEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case INGREDIENT:
                _id = db.insertWithOnConflict(
                        IngredientEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = IngredientEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case INGREDIENT_WITH_ID:
                _id = db.insertWithOnConflict(
                        IngredientEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = IngredientEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case STEP:
                _id = db.insertWithOnConflict(
                        StepEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = StepEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case STEP_WITH_ID:
                _id = db.insertWithOnConflict(
                        StepEntry.TABLE_NAME,
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = StepEntry.buildUri(_id);
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
        Log.d(LOG_TAG, "At delete " + uri.toString() + " match: " + Integer.toString(match));

        switch (match) {
            case RECIPE:
                numRowsDeleted = db.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_WITH_ID:
                id = getIdFromUri(uri);
                numRowsDeleted = db.delete(RecipeEntry.TABLE_NAME, sRecipeIdSelection,
                        new String[]{Long.toString(id)});
                break;
            case INGREDIENT:
                numRowsDeleted = db.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INGREDIENT_WITH_ID:
                id = getIdFromUri(uri);
                numRowsDeleted = db.delete(IngredientEntry.TABLE_NAME, sIngredientIdSelection,
                        new String[]{Long.toString(id)});
                break;
            case STEP:
                numRowsDeleted = db.delete(StepEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STEP_WITH_ID:
                id = getIdFromUri(uri);
                numRowsDeleted = db.delete(StepEntry.TABLE_NAME, sStepIdSelection,
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

        Log.d(LOG_TAG, "At update " + uri.toString());

        switch (match) {
            case RECIPE:
                numRowsUpdated = db.update(RecipeEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case RECIPE_WITH_ID:
                id = getIdFromUri(uri);
                numRowsUpdated = db.update(RecipeEntry.TABLE_NAME, contentValues, sRecipeIdSelection,
                        new String[]{Long.toString(id)});
                break;
            case INGREDIENT:
                numRowsUpdated = db.update(IngredientEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case INGREDIENT_WITH_ID:
                id = getIdFromUri(uri);
                numRowsUpdated = db.update(IngredientEntry.TABLE_NAME, contentValues, sIngredientIdSelection,
                        new String[]{Long.toString(id)});
                break;
            case STEP:
                numRowsUpdated = db.update(StepEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case STEP_WITH_ID:
                id = getIdFromUri(uri);
                numRowsUpdated = db.update(StepEntry.TABLE_NAME, contentValues, sStepIdSelection,
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
            case RECIPE:
                table = RecipeEntry.TABLE_NAME;
                break;
            case STEP:
                table = StepEntry.TABLE_NAME;
                break;
            case INGREDIENT:
                table = IngredientEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                Log.v(LOG_TAG, "Inserting to recipe table: " + value.toString());
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
        Log.v(LOG_TAG, "notifying for change in uri: " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return numInserted;
    }
}
