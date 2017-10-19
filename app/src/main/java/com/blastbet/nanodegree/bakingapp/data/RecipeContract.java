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

    public static final String PATH_RECIPE = "recipe";

    public static final String PATH_INGREDIENT = "ingredient";
    public static final String PATH_STEP = "step";

    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

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
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENT;

        public static final String TABLE_NAME       = "ingredient";

        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NAME      = "name";
        public static final String COLUMN_MEASURE   = "measure";
        public static final String COLUMN_QUANTITY  = "quantity";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriForRecipe(long recipeId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(recipeId)).build();
        }
    }

    public static final class StepEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_STEP;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" + PATH_STEP;

        public static final String TABLE_NAME   = "step";

        public static final String COLUMN_RECIPE_ID         = "recipe_id";
        public static final String COLUMN_INDEX             = "step_index";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_DESCRIPTION       = "description";
        public static final String COLUMN_VIDEO_URL         = "video_url";
        public static final String COLUMN_THUMBNAIL_URL     = "thumbnail_url";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriForRecipe(long recipeId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(recipeId)).build();
        }
    }
}
