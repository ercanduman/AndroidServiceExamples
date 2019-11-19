package ercanduman.jobschedulerdemo.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import ercanduman.jobschedulerdemo.R;
import ercanduman.jobschedulerdemo.services.BroadcastReceiverExample;
import ercanduman.jobschedulerdemo.services.IntentServiceExample;
import ercanduman.jobschedulerdemo.services.JobIntentServiceExample;
import ercanduman.jobschedulerdemo.services.JobServiceExample;
import ercanduman.jobschedulerdemo.services.ServiceExample;
import ercanduman.jobschedulerdemo.services.WorkManagerExample;

import static ercanduman.jobschedulerdemo.Constants.BROADCAST_ACTION;
import static ercanduman.jobschedulerdemo.Constants.BROADCAST_EXTRA;
import static ercanduman.jobschedulerdemo.Constants.JOB_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String INPUT_EXTRA = "INPUT_EXTRA";
    public static final String EXTRA_TASK_NAME = "EXTRA_TASK_NAME";
    public static final String EXTRA_TASK_DESC = "EXTRA_TASK_DESC";

    /**
     * Trigger broadcast receiver only when app is in foreground (onStart() method)
     * and unregister when app goes to background (onStop() method)
     */
    private BroadcastReceiverExample broadcastReceiver = new BroadcastReceiverExample();

    private TextView contentTextView;

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

        IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(implicitBroadcastReceiver, filter);

        contentTextView = findViewById(R.id.main_content_text_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(implicitBroadcastReceiver);
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

    /**
     * Sends an implicit broadcast
     */
    private void sendABroadcast() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_EXTRA, "This is an implicit broadcast example.");
        sendBroadcast(intent);
    }

    private BroadcastReceiver implicitBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedText = intent.getStringExtra(BROADCAST_EXTRA);
            Toast.makeText(context, receivedText, Toast.LENGTH_SHORT).show();
        }
    };

    private void startWorkManager() {
        Toast.makeText(this, "Work Manager started.", Toast.LENGTH_SHORT).show();

        // Passing data to Work Manager (works similar mechanism of Bundles)
        // can pass multiple inputs
        Data passingData = new Data.Builder()
                .putString(EXTRA_TASK_NAME, "Passed Task Name")
                .putString(EXTRA_TASK_DESC, "Passed Task Desc")
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(WorkManagerExample.class)
                .setInputData(passingData)
                .build();
        WorkManager.getInstance(this).enqueue(workRequest);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        String status = workInfo.getState().name();
                        contentTextView.append("Work State: " + status + "\n");
                    }
                });
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
            case R.id.action_start_broadcast_receiver:
                sendABroadcast();
                return true;
            case R.id.action_start_work_manager:
                startWorkManager();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
