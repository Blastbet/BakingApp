package com.blastbet.nanodegree.bakingapp;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static org.hamcrest.Matchers.is;

/**
 * Created by ilkka on 26.11.2017.
 */

public class RecyclerViewItemCountAssertion implements ViewAssertion {

    private final int mExpectedCount;

    public RecyclerViewItemCountAssertion(int count) {
        mExpectedCount = count;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw  noViewFoundException;
        }

        RecyclerView.Adapter adapter = ((RecyclerView) view).getAdapter();
        ViewMatchers.assertThat(adapter.getItemCount(), is(mExpectedCount));
    }
}
