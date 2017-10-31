package com.blastbet.nanodegree.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;

/**
 * Created by ilkka on 31.10.2017.
 */

public abstract class BakingLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private LoaderManager mManager;
    private Callbacks mCallbacks;

    public static final String KEY_RECIPE_ID = "recipe_id";

    public BakingLoader(Context context, LoaderManager manager, Callbacks callbacks) {
        mContext = context;
        mManager = manager;
        mCallbacks = callbacks;
    }

    public abstract int getLoaderId();

    public interface Callbacks {
        void onLoadFinished(int id, Cursor cursor);
        void onLoaderReset(int id);
    }

    Context getContext() {
        return mContext;
    }

    public void init(@Nullable Bundle args) {
        mManager.initLoader(getLoaderId(), args, this);
    }

    public void restart(@Nullable Bundle args) {
        mManager.restartLoader(getLoaderId(), args, this);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mCallbacks.onLoadFinished(getLoaderId(), cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mCallbacks.onLoaderReset(getLoaderId());
    }

}
