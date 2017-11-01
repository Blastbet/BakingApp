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

    private Rect mOutRect;

    public CardViewItemDecoration() {
        super();
        mOutRect = OUTRECT;
    }

    public CardViewItemDecoration(final int margin) {
        super();
        mOutRect = new Rect(margin, margin, margin, margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (view instanceof CardView) {
            outRect.set(mOutRect);
            Log.d(TAG, "Setting cardview insets to " + mOutRect.toString());
        }
        else {
            Log.d(TAG, "Cannot set insets for class " + view.getClass().getSimpleName());
        }
    }
}
