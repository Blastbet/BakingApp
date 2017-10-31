package com.blastbet.nanodegree.bakingapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ilkka on 2.9.2017.
 */

public class RecipeContract {
    public static final String CONTENT_AUTHORITY = "com.blastbet.nanodegree.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECIPES = "recipes";

    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";

    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;

        public static final String TABLE_NAME  = "recipe";

        public static final String COLUMN_ID       = "id";
        public static final String COLUMN_NAME     = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE    = "image";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class IngredientEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        public static final String TABLE_NAME       = "ingredient";

        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NAME      = "name";
        public static final String COLUMN_MEASURE   = "measure";
        public static final String COLUMN_QUANTITY  = "quantity";

        public static Uri buildUri(long recipeId) {
            return RecipeEntry.buildUri(recipeId).buildUpon()
                    .appendPath(PATH_INGREDIENTS).build();
        }
    }

    public static final class StepEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;

        public static final String TABLE_NAME   = "step";

        public static final String COLUMN_RECIPE_ID         = "recipe_id";
        public static final String COLUMN_INDEX             = "step_index";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_DESCRIPTION       = "description";
        public static final String COLUMN_VIDEO_URL         = "video_url";
        public static final String COLUMN_THUMBNAIL_URL     = "thumbnail_url";

        public static Uri buildUri(long recipeId) {
            return RecipeEntry.buildUri(recipeId).buildUpon()
                    .appendPath(PATH_STEPS).build();
        }

        public static Uri buildUriForRecipeStep(long recipeId, long recipeStep) {
            return ContentUris.withAppendedId(buildUri(recipeId), recipeStep);
        }
    }
}
