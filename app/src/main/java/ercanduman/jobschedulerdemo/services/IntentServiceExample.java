package ercanduman.jobschedulerdemo.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ercanduman.jobschedulerdemo.R;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;
import static ercanduman.jobschedulerdemo.ui.MainActivity.INPUT_EXTRA;

/**
 * Starts a service and stops itself automatically when work in done
 */
public class IntentServiceExample extends IntentService {
    private static final String TAG = "IntentServiceExample";
    private PowerManager.WakeLock wakeLock;

    public IntentServiceExample() {
        super("IntentServiceExample");
        setIntentRedelivery(true); // false; means if system kills the service, it wont be created again
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called");

        activateWakeLock();
        createNotification();
    }

    /**
     * Keeps the CPU to running while service is activated,
     * this way service could keep running.
     */
    private void activateWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ExampleApp:WakeLock");
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
            Log.d(TAG, "activateWakeLock: wakeLock acquired!");
        }
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Intent Service Example")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();
            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: called...");
        if (intent != null) {
            String passedData = intent.getStringExtra(INPUT_EXTRA);
            Log.d(TAG, "onHandleIntent: passedData: " + passedData);
        }

        for (int i = 0; i < 10; i++) {
            Log.d(TAG, "onHandleIntent: called for i: " + i);
            SystemClock.sleep(1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called.");
        wakeLock.release();
        Log.d(TAG, "onDestroy: wakeLock released!");
    }


}
