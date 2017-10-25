package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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


public class RecipeStepDetailsFragment extends Fragment implements Player.EventListener {

    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();

    private static final String KEY_RECIPE_ID = "recipe_id";
    private RecipeStep mRecipeStep;
    private SimpleExoPlayer mPlayer;

    @BindView(R.id.recipe_step_container) LinearLayout mContainer;
    @BindView(R.id.player_recipe_step_instruction) SimpleExoPlayerView mPlayerView;
/*    @BindView(R.id.play_button) ImageButton mButtonPlay;
    @BindView(R.id.overlay_player_controls) LinearLayout mPlayerOverlayControls;
    @BindView(R.id.pause_button) ImageButton mButtonPause;
    @BindView(R.id.rewind_button) ImageButton mButtonRewind;*/
    @BindView(R.id.player_loading_overlay) FrameLayout mPlayerLoadingOverlay;
    @BindView(R.id.text_recipe_step_instruction) TextView mTextView;

    public static final String KEY_RECIPE_STEP = "recipe_step";

    public RecipeStepDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeStepDetailsFragment newInstance(RecipeStep step) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_RECIPE_STEP, step);
        Log.d(TAG, "Creating new RecipeStepDetails for recipe step:" + step.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public void setStepData(RecipeStep step) {
        mRecipeStep = step;
        Log.d(TAG, "Set data:" + mRecipeStep.toString());
        initPlayer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeStep = getArguments().getParcelable(KEY_RECIPE_STEP);
            Log.d(TAG, "Arguments are not null. Recipestep is: " + mRecipeStep.toString());
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
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mContainer.setPadding(0,0,0,0);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
        if (savedInstanceState != null) {
            Log.d(TAG, "Recipe step: " + savedInstanceState.getParcelable(KEY_RECIPE_STEP));
            mRecipeStep = savedInstanceState.getParcelable(KEY_RECIPE_STEP);
        }
/*
        mPlayer = new SimpleExoPlayer()
        mPlayer = mPlayerView.getPlayer();
*/
        //mPlayerView.setPlayer(mPlayer);

        initPlayer();
        Log.d(TAG, "**** Setting player to: " + mPlayer.toString());
        Log.d(TAG, "**** Player surface:  " + mPlayerView.getVideoSurfaceView().toString());
        mPlayerView.setPlayer(mPlayer);
        mPlayerView.requestLayout();
        mPlayer.addListener(this);
        onPlayerStateChanged(mPlayer.getPlayWhenReady(), mPlayer.getPlaybackState());
        /*mPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.getPlayWhenReady()) {
                    mPlayer.setPlayWhenReady(false);
                }
                else {
                    mPlayer.setPlayWhenReady(true);
                }
            }
        });*/

        mTextView.setText(mRecipeStep.getDescription());
        return view;
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
    }

    public void initPlayer() {
        mPlayer = PlayerHandler.getInstance()
                .getPlayer(getActivity(), mRecipeStep.getVideoURL());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeStep != null) {
            outState.putParcelable(KEY_RECIPE_STEP, mRecipeStep);
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
        else if (playbackState == Player.STATE_READY && playWhenReady) {
            // Push controls out of view here
            mPlayerLoadingOverlay.setVisibility(View.GONE);
            mPlayerView.hideController();
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
}
