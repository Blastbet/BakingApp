package com.blastbet.nanodegree.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blastbet.nanodegree.bakingapp.recipe.RecipeStep;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRecipeStepDetailsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeStepDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepDetailsFragment extends Fragment {

    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();

    private static final String KEY_RECIPE_ID = "recipe_id";
    private RecipeStep mRecipeStep;
    private SimpleExoPlayer mPlayer;

    @BindView(R.id.player_recipe_step_instruction) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.text_recipe_step_instruction) TextView mTextView;

    private static final String KEY_RECIPE_STEP = "recipe_step";

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
        if (savedInstanceState != null) {
            mRecipeStep = savedInstanceState.getParcelable(KEY_RECIPE_STEP);
        }
/*
        mPlayer = new SimpleExoPlayer()
        mPlayer = mPlayerView.getPlayer();
*/
        //mPlayerView.setPlayer(mPlayer);
        initPlayer();
        mPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.getPlayWhenReady()) {
                    mPlayer.setPlayWhenReady(false);
                }
                else {
                    mPlayer.setPlayWhenReady(true);
                }
            }
        });
        return view;
    }

    public void initPlayer() {

        if (mPlayer == null) {
            Handler mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//            mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    trackSelector, new DefaultLoadControl());
            mPlayerView.setPlayer(mPlayer);
        }
        else if (mPlayer.isLoading() || mPlayer.getPlayWhenReady()) {
            mPlayer.stop();
        }

        Uri uri = Uri.parse(mRecipeStep.getVideoURL());

        Log.d(TAG, "Uri for recipe step: " + mRecipeStep.getVideoURL());
        Log.d(TAG, "Parsed Uri for recipe step: " + uri.toString());

        DataSource.Factory dataSourceFactory;/* = new DefaultDataSourceFactory(
                getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)),
                null);*/
        dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(getContext(), getString(R.string.app_name)));
        MediaSource mediaSource = new ExtractorMediaSource(uri,
                dataSourceFactory,
                new DefaultExtractorsFactory(),
                null, null);
        mPlayer.prepare(mediaSource);

        mTextView.setText(mRecipeStep.getDescription());
//        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeStep != null) {
            outState.putParcelable(KEY_RECIPE_STEP, mRecipeStep);
        }
    }

}
