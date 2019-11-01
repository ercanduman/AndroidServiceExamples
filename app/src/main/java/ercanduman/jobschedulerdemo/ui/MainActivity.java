package ercanduman.jobschedulerdemo.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import ercanduman.jobschedulerdemo.R;
import ercanduman.jobschedulerdemo.services.ExampleJobService;
import ercanduman.jobschedulerdemo.services.ForegroundServiceExample;

import static ercanduman.jobschedulerdemo.Constants.CHANNEL_ID;
import static ercanduman.jobschedulerdemo.Constants.JOB_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String INPUT_EXTRA = "INPUT_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start job?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "onClick: will start the job...");
//                                scheduleJob();
                                startForegroundService();
                            }
                        }).show();
            }
        });
    }

    private void startForegroundService() {
        String textToPass = "Example text";
        Intent serviceIntent = new Intent(this, ForegroundServiceExample.class);
        serviceIntent.putExtra(INPUT_EXTRA, textToPass);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void stopForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundServiceExample.class);
        stopService(serviceIntent);
    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000) // cannot be set to less than 15 min, even if set to less than 15 min, then it will be turn to 15 min internally
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            int resultCode = scheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "scheduleJob: job scheduled!");
            } else {
                Log.d(TAG, "scheduleJob: cannot schedule job!");
            }
        }
    }

    private void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            scheduler.cancel(JOB_ID);
            Log.d(TAG, "cancelJob: Job cancelled.");
        }
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel: Notification created...");
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_stop_job) {
            Log.d(TAG, "onOptionsItemSelected: will stop job....");
//            cancelJob();
            stopForegroundService();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
