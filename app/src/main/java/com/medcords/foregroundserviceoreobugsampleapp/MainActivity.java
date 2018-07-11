package com.medcords.foregroundserviceoreobugsampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
    }

    public void startForegroundService(View view) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                inform(getApplicationContext(), "Starting foreground service.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getApplicationContext(),
                            SampleForegroundService.class));
                } else {
                    startService(new Intent(getApplicationContext(),
                            SampleForegroundService.class));
                }
            }
        }, 1000);
        finish();
    }

    @Override
    protected void onPause() {
        inform(getApplicationContext(), "App went to background.");
        super.onPause();
    }

    public static void inform(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
