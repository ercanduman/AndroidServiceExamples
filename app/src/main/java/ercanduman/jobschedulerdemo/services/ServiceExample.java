package ercanduman.jobschedulerdemo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ercanduman.jobschedulerdemo.R;
import ercanduman.jobschedulerdemo.ui.MainActivity;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;
import static ercanduman.jobschedulerdemo.Constants.JOB_ID;

public class ServiceExample extends Service {
    private static final String TAG = "ServiceExample";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: called only once....");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // all codes written here runs on main thread
        // so do the heavy work on the background

        Log.d(TAG, "onStartCommand: called every  time service started...");
        String passedData = intent.getStringExtra(MainActivity.INPUT_EXTRA);
        Log.d(TAG, "onStartCommand: passedData: " + passedData);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Plain Service Example")
                .setContentText(passedData)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(JOB_ID, notification);

        // when work finished, should stop service
        // stopSelf();

        for (int i = 0; i < 5; i++) {
            Log.d(TAG, "onStartCommand: called for i: " + i);
            SystemClock.sleep(1000);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called only once when service is stopped.");
        super.onDestroy();
    }
}
