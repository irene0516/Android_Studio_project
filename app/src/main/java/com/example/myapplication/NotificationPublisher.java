package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;


import static com.example.myapplication.NotificationSchedulerApplication.CHANNEL_ID;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // The notification may come from the received intent (see SettingsActivity for how to build
        // it and add it to the intent). If not then we'll build a notification here.
        Notification notification;
        if (intent.hasExtra(NOTIFICATION)) {
            notification = intent.getParcelableExtra(NOTIFICATION);
        } else {
            notification = buildNotification(ctx);
        }

        // notificationId is a unique int for each notification.
        // TODO Is the current time good enough?
        int notificationId = (int) System.currentTimeMillis();

        notificationManager.notify(notificationId, notification);
    }

    private Notification buildNotification(Context ctx) {
        Intent intent = new Intent(ctx, Page2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
        Message msg = Message.obtain();
        msg.what = 123;
        msg.obj="timeup";
        Page2.handler.sendMessage(msg);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("ERROR!")
                .setContentText("請把手機關掉!!!!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        return builder.build();
    }
}
