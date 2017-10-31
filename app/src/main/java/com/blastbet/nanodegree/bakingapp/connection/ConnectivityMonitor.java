package com.blastbet.nanodegree.bakingapp.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by ilkka on 25.10.2017.
 */

public class ConnectivityMonitor extends BroadcastReceiver {

    private static final String TAG = ConnectivityMonitor.class.getSimpleName();

    public static final String NETWORK_CONNECTIVITY_STATE_KEY = "connectivity_state";

    private Context mContext;
    private boolean mIsConnected;

    public ConnectivityMonitor(Context context) {
        mIsConnected = false;
        mContext = context;
        // set initial connectivity state
        updateConnectivityPreference(context);
    }

    public void onResume() {
        // Check for changes
        updateConnectivityPreference(mContext);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(this, filter);
    }

    public void onPause() {
        mContext.unregisterReceiver(this);
    }

    private void updateConnectivityPreference(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (mIsConnected != isConnected) {
                Log.d(TAG, "Connectivity state changed to " + (isConnected ? "CONNECTED" : "NO CONNECTION"));
                mIsConnected = isConnected;
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(context);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(NETWORK_CONNECTIVITY_STATE_KEY, isConnected);
                editor.apply();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        updateConnectivityPreference(context);
    }
}
