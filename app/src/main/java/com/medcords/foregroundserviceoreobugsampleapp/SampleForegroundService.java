package com.medcords.foregroundserviceoreobugsampleapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class SampleForegroundService extends Service {
    private boolean isProcessing;

    public SampleForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(startId, getNotification());
        startProcessingAsync();
        return START_STICKY;
    }

    private void startProcessingAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isProcessing) {
                    return;
                }
                isProcessing = true;

                MainActivity.inform(getApplicationContext(), "Started processing something.");

                long time = 0;
                while (time <= 10000) {
                    try {
                        Thread.sleep(2000);
                        MainActivity.inform(getApplicationContext(), "still processing.");
                        time += 2000;
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }

                MainActivity.inform(getApplicationContext(), "Done processing. Stopping service now");
                MainActivity.inform(getApplicationContext(), "The bug is not reproducible on this device!");

                stopForeground(true);
                stopSelf();
            }
        }).start();
    }

    private Notification getNotification() {
        createLowPriorityNotificationChannelIfNeeded(getApplicationContext());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                "LOW_PRIORITY");
        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Oreo bug repro app")
                .setContentText("Doing something in background...")
                .setAutoCancel(true)
                .setColor(this.getResources().getColor(R.color.colorAccent));
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        Notification notification = builder.build();
        return notification;
    }

    public static void createLowPriorityNotificationChannelIfNeeded(Context mContext) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel =
                new NotificationChannel("LOW_PRIORITY",
                        "Avoidable",
                        NotificationManager.IMPORTANCE_MIN);
        notificationManager.createNotificationChannel(channel);
    }
}
