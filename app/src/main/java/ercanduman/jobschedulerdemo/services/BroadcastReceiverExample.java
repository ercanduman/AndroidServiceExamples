package ercanduman.jobschedulerdemo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class BroadcastReceiverExample extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverExampl";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: called for CONNECTIVITY_ACTION");
            boolean noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (noConnection) {
                Toast.makeText(context, "No network Connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Network connection is available!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
