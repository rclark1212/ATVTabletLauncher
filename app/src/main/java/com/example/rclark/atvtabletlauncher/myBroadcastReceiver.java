package com.example.rclark.atvtabletlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by rclark on 3/22/17.
 */

public class myBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {

        if ((intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED))
            || (intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED))) {

            //go ahead and update via a private msg
            Intent localIntent = new Intent(MainActivity.PRIVATE_UPDATE);
            //And broadcast the message
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);

        }
    }
}
