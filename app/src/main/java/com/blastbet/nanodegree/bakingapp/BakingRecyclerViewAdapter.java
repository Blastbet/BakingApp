package com.blastbet.nanodegree.bakingapp;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ilkka on 1.11.2017.
 */
public abstract class BakingRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String TAG = BakingRecyclerViewAdapter.class.getSimpleName();

    private DataSetObserver mDataSetObserver;
    protected Cursor mCursor;
    protected boolean mDataValid;

    public BakingRecyclerViewAdapter() {
        mDataValid = false;

        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mDataValid = true;
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                mDataValid = false;
                notifyDataSetChanged();
            }
        };
    }

    public void swapCursor(Cursor cursor) {
        Log.d(TAG, "Swapping cursor to " + (cursor != null ? "new one" : "null"));
        if (cursor == mCursor) {
            Log.d(TAG, "same cursor -> nop");
            return;
        }
        if (mCursor != null) {
            Log.d(TAG, "Unregister observer from precious cursor");
            mCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mCursor = cursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                Log.d(TAG, "Register new data set observer");
                cursor.registerDataSetObserver(mDataSetObserver);
            }
            cursor.moveToFirst();
            mDataValid = true;
        }
        else {
            Log.d(TAG, "Invalid data");
            mDataValid = false;
        }
        Log.d(TAG, "notify of data set change");

        notifyDataSetChanged();
    }

    public void setEmptyView(final TextView emptyView, final RecyclerView recyclerView) {
        RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Log.d(TAG, "Got notified of change in data");
                if (getItemCount() <= 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };
        // Init state
        Log.d(TAG, "Set initial data state");
        dataObserver.onChanged();
        registerAdapterDataObserver(dataObserver);
    }

    @Override
    public int getItemCount() {
        return (mCursor != null && mDataValid) ? mCursor.getCount() : 0;
    }
}
