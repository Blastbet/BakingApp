package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by ilkka on 5.10.2017.
 */

public class CardViewItemDecoration extends RecyclerView.ItemDecoration {
    Context mContext;

    private static final String TAG = CardViewItemDecoration.class.getSimpleName();
    private static final Rect OUTRECT = new Rect(16, 16, 16, 16);

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (view instanceof CardView) {
            outRect.set(OUTRECT);
            Log.d(TAG, "Setting cardview insets to " + OUTRECT.toString());
        }
        else {
            Log.d(TAG, "Cannot set insets for class " + view.getClass().getSimpleName());
        }
    }
}
