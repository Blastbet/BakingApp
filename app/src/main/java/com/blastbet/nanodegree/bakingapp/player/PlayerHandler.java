package com.blastbet.nanodegree.bakingapp.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.blastbet.nanodegree.bakingapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * Created by ilkka on 23.10.2017.
 */

public class PlayerHandler implements Player.EventListener {

    private static final String TAG = PlayerHandler.class.getSimpleName();

    private static PlayerHandler instanceS;

    private SimpleExoPlayer mPlayer;
    private MediaSource mMediaSource;
    private MediaSessionConnector mMediaSessionConnector;
    private String mUrl;

    private PlayerHandler() {
    }

    public static PlayerHandler getInstance() {
        if (instanceS == null) {
            instanceS = new PlayerHandler();
        }
        return instanceS;
    }

    public SimpleExoPlayer getPlayer(Context context, String url) {
        if (mPlayer == null) {
            Handler mainHandler = new Handler();

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);

            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(context),
                    trackSelector, new DefaultLoadControl());
        }

        if (url == null) {
            throw new NullPointerException("null String 'url' when getting Player");
        }

        if (!url.equals(mUrl)) {
            mUrl = url;
            Uri uri = Uri.parse(mUrl);
            Log.d(TAG, "Parsed Uri for player: " + uri.toString());

            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                    Util.getUserAgent(context, context.getString(R.string.app_name)));

            MediaSource mediaSource = new ExtractorMediaSource(uri,
                    dataSourceFactory,
                    new DefaultExtractorsFactory(),
                    null, null);

            mPlayer.addListener(this);
            mPlayer.prepare(mediaSource);
        }

        return mPlayer;
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
        Log.d(TAG, "onLoadingChanged" + (isLoading ? " -> loading" : " -> not loading"));
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged : " + playWhenReady + " , " + playbackState);

        switch (playbackState) {
            case Player.STATE_IDLE:
                Log.d(TAG, "onPlayerStateChanged to STATE_IDLE");
                break;
            case Player.STATE_BUFFERING:
                Log.d(TAG, "onPlayerStateChanged to STATE_BUFFERING");
                break;
            case Player.STATE_READY:
                if (playWhenReady)
                    Log.d(TAG, "onPlayerStateChanged to STATE_READY (PLAYING)");
                else
                    Log.d(TAG, "onPlayerStateChanged to STATE_READY (PAUSED)");
                break;
            case Player.STATE_ENDED:
                Log.d(TAG, "onPlayerStateChanged to STATE_ENDED");
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.d(TAG, "onRepeatModeChanged " + repeatMode );

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError: " + error.toString());

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
