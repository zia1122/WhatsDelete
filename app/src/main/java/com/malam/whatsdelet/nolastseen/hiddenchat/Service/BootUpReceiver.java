package com.malam.whatsdelet.nolastseen.hiddenchat.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i) {
        Intent gpsIntent = new Intent(c, FileSystemObserverService.class);
        ContextCompat.startForegroundService(c, gpsIntent);
    }
}