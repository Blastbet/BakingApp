package com.blastbet.nanodegree.bakingapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ilkka on 3.9.2017.
 */

public class RecipeSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static RecipeSyncAdapter sRecipeSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sRecipeSyncAdapter == null) {
                sRecipeSyncAdapter = new RecipeSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sRecipeSyncAdapter.getSyncAdapterBinder();
    }
}
