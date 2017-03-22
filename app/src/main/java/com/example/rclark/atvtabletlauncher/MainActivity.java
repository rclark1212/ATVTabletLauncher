package com.example.rclark.atvtabletlauncher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rclark on 3/21/17.
 * Trivial app which gives launcher functionality for tablet/phone apps
 */

public class MainActivity extends Activity {

    //add apks to this list to exclude them
    String[] exclusion_list = { "com.nvidia.shield.welcome",
                                "com.android.documentsui",
                                "com.nvidia.blakepairing",
                                "com.nvidia.inputviewer",
                                "com.plexapp.mediaserver.smb"
                                };

    GridView mGrid = null;
    ArrayList<AppDetail> mApps = null;
    public static final String PRIVATE_UPDATE = "com.example.rclark.atvtabletlauncher.BROADCAST";
    public static final String TAG = "ATVTabletLauncher";

    //We use broadcast intents to message back from Services to the init activity.
    //Define our handler for this here.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //well, only one message so no parameters. Just go ahead and update UI here.
            updateAppList();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mGrid = (GridView) findViewById(R.id.gridview_apps);

        mApps = loadApps(exclusion_list);

        mGrid.setAdapter(new GridAdapter(this, mApps));

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), mApps.get(position).app_apk, Toast.LENGTH_SHORT).show();
                launchApp(mApps.get(position));
            }
        });

        if (mApps.size() == 0) {
            //no apps...
            Toast.makeText(this, "No tablet apps found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(PRIVATE_UPDATE));
    }

    @Override
    public void onPause() {
        //Unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
    }

    void updateAppList() {
        Log.d(TAG, "Got an update event");
        mApps = loadApps(exclusion_list);
        mGrid.setAdapter(new GridAdapter(this, mApps));
    }

    void launchApp(AppDetail app) {
        PackageManager manager = this.getPackageManager();

        try {
            Intent i = manager.getLaunchIntentForPackage(app.app_apk);
            if (i == null) {
                Toast.makeText(this, "Oops - could not launch", Toast.LENGTH_SHORT).show();
            } else {
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Oops - could not launch", Toast.LENGTH_SHORT).show();
        }
    }


    //Loads non-leanback launcher apps into array list
    ArrayList<AppDetail> loadApps(String[] exclusion_list) {
        PackageManager manager = this.getPackageManager();
        ArrayList<AppDetail> apps = new ArrayList<AppDetail>();

        //Okay - get the tablet intents
        Intent tablet_intent = new Intent(Intent.ACTION_MAIN, null);
        tablet_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> tabletActivities = manager.queryIntentActivities(tablet_intent, 0);

        //And the leanback intents
        Intent lb_intent = new Intent(Intent.ACTION_MAIN, null);
        lb_intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> lbActivities = manager.queryIntentActivities(lb_intent, 0);

        //lets build up a leanback apk list right now...
        ArrayList<String> lb_apks = new ArrayList<String>();
        for (int i = 0; i < lbActivities.size(); i++) {
            ResolveInfo ri = lbActivities.get(i);
            lb_apks.add(ri.activityInfo.packageName);
        }

        //now loop through all tablet apps...
        for (int i = 0; i < tabletActivities.size(); i++) {
            ResolveInfo ri = tabletActivities.get(i);

            //okay - lets make sure this apk does not have a leanback intent and it is not in our excluded list
            boolean bInExclusion = false;
            if (exclusion_list != null) {
                for (int j = 0; j < exclusion_list.length; j++) {
                    if (exclusion_list[j].equals(ri.activityInfo.packageName)) {
                        bInExclusion = true;
                        break;
                    }
                }
            }

            if (!bInExclusion && !lb_apks.contains(ri.activityInfo.packageName)) {
                //okay - not in our exclusion list *and* does not have leanback intent
                AppDetail app = new AppDetail();
                app.app_label = (String) ri.loadLabel(manager);
                app.app_apk = ri.activityInfo.packageName;
                app.name = ri.activityInfo.name;
                app.icon = ri.activityInfo.loadIcon(manager);
                apps.add(app);
            }
        }
        return apps;
    }

}
