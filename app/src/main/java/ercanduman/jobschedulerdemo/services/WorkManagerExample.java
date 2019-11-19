package ercanduman.jobschedulerdemo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ercanduman.jobschedulerdemo.R;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;

public class WorkManagerExample extends Worker {
    private boolean isFinishedSuccessfully;

    public WorkManagerExample(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Works on a background thread
        createNotification("Task 1", "Desc 1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    SystemClock.sleep(1000);
                }
                isFinishedSuccessfully = true;
            }
        }).start();
        if (isFinishedSuccessfully) return Result.success();
        else return Result.failure();
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
