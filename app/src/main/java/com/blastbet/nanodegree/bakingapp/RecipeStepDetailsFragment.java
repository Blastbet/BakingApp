package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.connection.ConnectivityMonitor;
import com.blastbet.nanodegree.bakingapp.data.RecipeStepDetailsLoader;
import com.blastbet.nanodegree.bakingapp.player.PlayerHandler;
import com.blastbet.nanodegree.bakingapp.recipe.RecipeStep;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeStepDetailsFragment extends Fragment
        implements Player.EventListener
        , SharedPreferences.OnSharedPreferenceChangeListener
        , RecipeStepDetailsLoader.Callbacks {

    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();

    private RecipeStepDetailsLoader mLoader;
    private Cursor mData;

    private int mRecipeId;
    private int mRecipeStep;
    private int mRecipeStepCount;

    private SimpleExoPlayer mPlayer;

    private boolean mIsConnected;

    @BindView(R.id.recipe_step_container) LinearLayout mContainer;
    @BindView(R.id.player_recipe_step_instruction) SimpleExoPlayerView mPlayerView;
/*    @BindView(R.id.play_button) ImageButton mButtonPlay;
    @BindView(R.id.overlay_player_controls) LinearLayout mPlayerOverlayControls;
    @BindView(R.id.pause_button) ImageButton mButtonPause;
    @BindView(R.id.rewind_button) ImageButton mButtonRewind;*/
    @BindView(R.id.text_player_alert) TextView mPlayerAlertText;
    @BindView(R.id.player_loading_overlay) FrameLayout mPlayerLoadingOverlay;
    @BindView(R.id.text_recipe_step_instruction) TextView mTextView;

    Button mPreviousButton;
    Button mNextButton;

    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_RECIPE_STEP = "recipe_step";
    public static final String KEY_RECIPE_STEP_COUNT = "recipe_step_count";

    public RecipeStepDetailsFragment() {
        mRecipeId = -1;
        mRecipeStep = -1;
    }

    public static RecipeStepDetailsFragment newInstance(int recipeId, int recipeStep, int stepCount) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID, recipeId);
        args.putInt(KEY_RECIPE_STEP, recipeStep);
        args.putInt(KEY_RECIPE_STEP_COUNT, stepCount);
        Log.d(TAG, "Creating new RecipeStepDetails for recipe " + recipeId
                + ", step " + recipeStep
                + " max step " + stepCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecipeStepDetailsFragment newEmptyInstance() {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        return fragment;
    }

    private void swapCursor(Cursor cursor) {
        mData = cursor;

        if (mData == null || !mData.moveToFirst()) {
            mData = null;
            onDataUnavailable();
        }
        else {
            mRecipeStep = mData.getInt(RecipeStepDetailsLoader.COL_STEP_INDEX);
            setupViews();
        }
    }

    public void setStep(int recipeId, int recipeStep, int recipeStepCount) {
        if (mRecipeId < 0 || mRecipeStep < 0) {
            mLoader.init(recipeId, recipeStep);
        }
        else {
            mLoader.restart(recipeId, recipeStep);
        }

        mRecipeId = recipeId;
        mRecipeStepCount = recipeStepCount;
    }
/*

    public void setStepData(RecipeStep step) {
        mRecipeStep = step;
        Log.d(TAG, "Set data:" + mRecipeStep.toString());
        if (!getResources().getBoolean(R.bool.landscape_only)) {
            setupNavigationButtons();
        }
        initPlayer();
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeId = getArguments().getInt(KEY_RECIPE_ID);
            mRecipeStep = getArguments().getInt(KEY_RECIPE_STEP);
            mRecipeStepCount = getArguments().getInt(KEY_RECIPE_STEP_COUNT);
            Log.d(TAG, "Arguments are not null. Recipestep is: " + mRecipeStep);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoader = new RecipeStepDetailsLoader(getContext(), getLoaderManager(), this);
        if (mRecipeStep >= 0 && mRecipeId >= 0) {
            mLoader.init(mRecipeId, mRecipeStep);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);
        ButterKnife.bind(this, view);
        Context context = view.getContext();
        if (context.getResources().getBoolean(R.bool.landscape_only)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT,
                    context.getResources().getInteger(R.integer.weight_recipe_step_details_fragment));
            view.setLayoutParams(params);
        }
        else {
            addStepNavigationBar();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mContainer.setPadding(0,0,0,0);
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.hide();
                }
            }
        }
        if (savedInstanceState != null) {
            Log.d(TAG, "Recipe step: " + savedInstanceState.getInt(KEY_RECIPE_STEP));
            mRecipeId = savedInstanceState.getInt(KEY_RECIPE_ID);
            mRecipeStep = savedInstanceState.getInt(KEY_RECIPE_STEP);
            mRecipeStepCount = savedInstanceState.getInt(KEY_RECIPE_STEP_COUNT);
        }

        // TODO: Check connectivity state and init player only if there is connectivity available.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(ConnectivityMonitor.NETWORK_CONNECTIVITY_STATE_KEY, false)) {
            mIsConnected = true;
            setupViews();
        }
        else {
            mIsConnected = false;
            onDisconnected();
        }

        preferences.registerOnSharedPreferenceChangeListener(this);
        return view;
    }

    private void setupViews() {
        if (mData != null && mData.moveToFirst()) {
            final String url = mData.getString(RecipeStepDetailsLoader.COL_STEP_VIDEO_URL);
            if (url == null || url.isEmpty()) {
                Log.w(TAG, "Invalid empty URL for video!");

            }
            else {
                initPlayer(url);

                mPlayerView.setVisibility(View.VISIBLE);
                mPlayerView.setPlayer(mPlayer);
                mPlayerView.requestLayout();
                onPlayerStateChanged(mPlayer.getPlayWhenReady(), mPlayer.getPlaybackState());
                mPlayerAlertText.setVisibility(View.GONE);
            }
            String description = mData.getString(RecipeStepDetailsLoader.COL_STEP_DESCRIPTION);
            mTextView.setText(description);

            Log.d(TAG, "Description: " + description);
            if (!getResources().getBoolean(R.bool.landscape_only)) {
                setupNavigationButtons();
            }

        }
        else {
            onDataUnavailable();
        }
    }

    private void onConnected() {
        setupViews();
    }

    private void releasePlayer() {
        mPlayerLoadingOverlay.setVisibility(View.GONE);
        mPlayerView.hideController();
        mPlayerView.setVisibility(View.GONE);
        PlayerHandler.getInstance().releasePlayer();
    }

    private void onDisconnected() {
        releasePlayer();
        mPlayerAlertText.setText(R.string.network_connectivity_alert);
        mPlayerAlertText.setVisibility(View.VISIBLE);
    }

    private void onDataUnavailable() {
        releasePlayer();
        mPlayerAlertText.setText(R.string.step_data_unavailable_alert);
        mPlayerAlertText.setVisibility(View.VISIBLE);
        mTextView.setText(R.string.step_data_unavailable_alert);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!getResources().getBoolean(R.bool.landscape_only) &&
            getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void initPlayer(String url) {
        Log.d(TAG, "Starting buffering player from url " + url);
        mPlayer = PlayerHandler.getInstance().getPlayer(getActivity(),
                url,
                this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeStep >= 0) {
            outState.putInt(KEY_RECIPE_ID, mRecipeId);
            outState.putInt(KEY_RECIPE_STEP, mRecipeStep);
            outState.putInt(KEY_RECIPE_STEP_COUNT, mRecipeStepCount);
        }
    }

    private void addStepNavigationBar() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.navigation_bar_step_details, mContainer, true);
        mPreviousButton = (Button) view.findViewById(R.id.button_left);
        mNextButton = (Button) view.findViewById(R.id.button_right);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoader.restart(mRecipeId, mRecipeStep - 1);
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoader.restart(mRecipeId, mRecipeStep + 1);
            }
        });
//        mContainer.addView(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setupNavigationButtons() {
        Log.d(TAG, "Setting up navigation buttons, recipe step: " + mRecipeStep);
        if (mPreviousButton == null || mNextButton == null) {
            return;
        }

        if (mRecipeStep <= 0) {
            mPreviousButton.setEnabled(false);
        }
        else {
            mPreviousButton.setEnabled(true);
        }
        if (mRecipeStep >= (mRecipeStepCount - 1)) {
            mNextButton.setEnabled(false);
        }
        else {
            mNextButton.setEnabled(true);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d(TAG, "onTimelineChanged : " + timeline.toString());
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "onTracksChanged");

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged : " + playWhenReady + " , " + playbackState);

        if (playbackState == Player.STATE_BUFFERING) {
            mPlayerLoadingOverlay.setVisibility(View.VISIBLE);
            //mPlayerView.setControllerAutoShow(false);
            mPlayerView.hideController();
        }
        else if (playbackState == Player.STATE_READY) {
            // Push controls out of view here
            mPlayerLoadingOverlay.setVisibility(View.GONE);
            if (playWhenReady) {
                mPlayerView.hideController();
            }
            else {
                mPlayerView.showController();
            }
            //mPlayerView.setControllerAutoShow(true);
        }
        else {
            mPlayerLoadingOverlay.setVisibility(View.GONE);
            mPlayerView.showController();
            //mPlayerView.setControllerAutoShow(true);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.d(TAG, "onRepeatModeChanged " + repeatMode );

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError: " + error.getMessage());

    }

    @Override
    public void onPositionDiscontinuity() {
        Log.d(TAG, "onPositionDiscontinuity");

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d(TAG, "onPlaybackParametersChanged : " + playbackParameters.toString());

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(ConnectivityMonitor.NETWORK_CONNECTIVITY_STATE_KEY)) {
            if (sharedPreferences.getBoolean(ConnectivityMonitor.NETWORK_CONNECTIVITY_STATE_KEY, false)) {
                onConnected();
            }
            else {
                onDisconnected();
            }
        }
    }

    @Override
    public void onLoadFinished(int id, Cursor cursor) {
        if (id == mLoader.getLoaderId()) {
            swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(int id) {
        if (id == mLoader.getLoaderId()) {
            swapCursor(null);
        }
    }
}
