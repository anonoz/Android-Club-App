package com.anonoz.androidmmu.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TodoSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TodoSyncAdapter sTodoSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("TodoSyncService", "onCreate - TodoSyncService");
        synchronized (sSyncAdapterLock) {
            if (sTodoSyncAdapter == null) {
                sTodoSyncAdapter = new TodoSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTodoSyncAdapter.getSyncAdapterBinder();
    }
}