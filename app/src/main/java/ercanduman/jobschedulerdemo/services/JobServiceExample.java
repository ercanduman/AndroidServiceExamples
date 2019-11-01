package ercanduman.jobschedulerdemo.services;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import ercanduman.jobschedulerdemo.R;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;

public class JobServiceExample extends JobService {
    private static final String TAG = "JobServiceExample";
    private boolean jobCancelled;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: started...");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        createNotification();
        // do all stuff here
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run: " + i);

                    if (jobCancelled) return;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "run: job finished.");
                jobFinished(params, false);
            }
        }).start();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Job Service example")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();

            startForeground(1, notification);
        }

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: Job canceled before completion!");
        jobCancelled = true;
        return true;
    }
}
