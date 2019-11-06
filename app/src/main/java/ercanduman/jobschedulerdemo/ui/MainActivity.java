package ercanduman.jobschedulerdemo.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import ercanduman.jobschedulerdemo.services.BroadcastReceiverExample;
import ercanduman.jobschedulerdemo.services.IntentServiceExample;
import ercanduman.jobschedulerdemo.services.JobIntentServiceExample;
import ercanduman.jobschedulerdemo.services.JobServiceExample;
import ercanduman.jobschedulerdemo.services.ServiceExample;

import static ercanduman.jobschedulerdemo.Constants.JOB_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String INPUT_EXTRA = "INPUT_EXTRA";

    /**
     * Trigger broadcast receiver only when app is in foreground (onStart() method)
     * and unregister when app goes to background (onStop() method)
     */
    private BroadcastReceiverExample broadcastReceiver = new BroadcastReceiverExample();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Stop running service?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "onClick: The service will be stopped...");
                                stopPlainService();
                                stopJobService();
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    private void startPlainService() {
        String textToPass = "Plain Service example text";
        Intent serviceIntent = new Intent(this, ServiceExample.class);
        serviceIntent.putExtra(INPUT_EXTRA, textToPass);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void startJobService() {
        ComponentName componentName = new ComponentName(this, JobServiceExample.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000) // cannot be set to less than 15 min, even if set to less than 15 min, then it will be turn to 15 min internally
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            int resultCode = scheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "startJobService: job scheduled!");
            } else {
                Log.d(TAG, "startJobService: cannot schedule job!");
            }
        }
    }

    private void startIntentService() {
        String textToPass = "Intent service example text";
        Intent serviceIntent = new Intent(this, IntentServiceExample.class);
        serviceIntent.putExtra(INPUT_EXTRA, textToPass);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void startJobIntentService() {
        String textToPass = "Job Intent Service example text";
        Intent serviceIntent = new Intent(this, JobIntentServiceExample.class);
        serviceIntent.putExtra(INPUT_EXTRA, textToPass);
        JobIntentServiceExample.enqueueWork(this, serviceIntent);
    }

    private void stopPlainService() {
        Log.d(TAG, "stopPlainService: called...");
        Intent serviceIntent = new Intent(this, ServiceExample.class);
        stopService(serviceIntent);
    }

    private void stopJobService() {
        Log.d(TAG, "stopJobService:called...");
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            scheduler.cancel(JOB_ID);
            Log.d(TAG, "cancelJob: Job cancelled.");
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
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start_plain_service:
                startPlainService();
                return true;
            case R.id.action_start_job_service:
                startJobService();
                return true;
            case R.id.action_start_intent_service:
                startIntentService();
                return true;
            case R.id.action_start_job_intent_service:
                startJobIntentService();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
