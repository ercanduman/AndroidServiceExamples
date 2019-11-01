package ercanduman.jobschedulerdemo.services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import ercanduman.jobschedulerdemo.R;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;
import static ercanduman.jobschedulerdemo.Constants.JOB_ID;
import static ercanduman.jobschedulerdemo.ui.MainActivity.INPUT_EXTRA;

/**
 * Do not need to handle WakeLock as in {@link IntentServiceExample}
 * {@link JobIntentService} handles the WakeLock by automatically
 */
public class JobIntentServiceExample extends JobIntentService {
    private static final String TAG = "JobIntentServiceExample";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called.");

        createNotification();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Job Intent Service example")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();

            startForeground(1, notification);
        }
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, JobIntentServiceExample.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork: started...");
        String passedData = intent.getStringExtra(INPUT_EXTRA);
        Log.d(TAG, "onHandleWork: passedData: " + passedData);

        for (int i = 0; i < 10; i++) {
            if (isStopped()) return; // if job is stopped than do not execute anymore.

            Log.d(TAG, "onHandleWork: run for i: " + i);
            SystemClock.sleep(1000);
        }
    }

    /**
     * If jobScheduler is used and current job stopped than this method is triggered
     * in API 26+
     *
     * @return boolean; if the job need to resumed later or just drop it.
     * Default value is true.
     * false: current intent and following intents will be dropped and job will be cancelled.
     */
    @Override
    public boolean onStopCurrentWork() {
        Log.d(TAG, "onStopCurrentWork: called.");
        return super.onStopCurrentWork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }
}
