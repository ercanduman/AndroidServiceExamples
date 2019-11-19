package ercanduman.jobschedulerdemo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ercanduman.jobschedulerdemo.R;
import ercanduman.jobschedulerdemo.ui.MainActivity;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;

public class WorkManagerExample extends Worker {
    private static final String TAG = "WorkManagerExample";
    public static final String TASK_OUTPUT = "TASK_OUTPUT";

    public WorkManagerExample(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: started...");
        // Works on a background thread

        //Retrieve the passed data
        String taskName = getInputData().getString(MainActivity.EXTRA_TASK_NAME);
        String taskDesc = getInputData().getString(MainActivity.EXTRA_TASK_DESC);
        int i = 0;
        while (i < 10) {
            Log.d(TAG, "doWork: run for " + i);
            SystemClock.sleep(1000);
            i++;
        }
        if (i == 10) {
            // Can pass data back
            Data outputData = new Data.Builder()
                    .putBoolean(TASK_OUTPUT, true)
                    .build();

            Log.d(TAG, "doWork: notification going to be shown \" SUCCESS!\"");
            createNotification(taskName + " SUCCESS!", taskDesc);
            return Result.success(outputData);
        } else {
            // Can pass data back
            Data outputData = new Data.Builder()
                    .putBoolean(TASK_OUTPUT, false)
                    .build();
            Log.d(TAG, "doWork: notification going to be shown \" FAILED!\"");
            createNotification(taskName + " FAILED!", taskDesc);
            return Result.failure(outputData);
        }
    }

    private void createNotification(String task, String desc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(task)
                    .setContentText(desc)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.notify(1, notification);
        }
    }
}
