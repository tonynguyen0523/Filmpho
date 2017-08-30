package com.android.tonynguyen0523.filmpho.sync;

/**
 * Created by Tony Nguyen on 1/13/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FilmphoSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static FilmphoSyncAdapter sFilmphoSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("FilmphoSyncService", "onCreate - FilmphoSyncService");
        synchronized (sSyncAdapterLock) {
            if (sFilmphoSyncAdapter == null) {
                sFilmphoSyncAdapter = new FilmphoSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFilmphoSyncAdapter.getSyncAdapterBinder();
    }
}