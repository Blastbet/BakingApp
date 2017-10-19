package com.blastbet.nanodegree.bakingapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ilkka on 3.9.2017.
 */

public class RecipeAuthenticatorService extends Service {
    private RecipeAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new RecipeAuthenticator(this);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
