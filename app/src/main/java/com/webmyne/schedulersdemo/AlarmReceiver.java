package com.webmyne.schedulersdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Random;

/**
 * Created by priyasindkar on 11-04-2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private Context _ctx;
    private NotificationManager alarmNotificationManager;
    private NotificationCompat.Builder alamNotificationBuilder;
    private int Id;

    @Override
    public void onReceive(Context context, Intent intent) {
        _ctx = context;
        Id = intent.getIntExtra("ID", -1);
        sendNotification("Daily scheduled Alarm Fired! " + new Date(System.currentTimeMillis()).toString());

    }

    private void sendNotification(String msg) {
        Log.e("NOTIFICATION", "Daily scheduled Alarm Fired! " + Id);
        alarmNotificationManager = (NotificationManager)_ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent newIntent = new Intent(_ctx, MainActivity.class);
        // newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(_ctx, 0, newIntent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        alamNotificationBuilder = new NotificationCompat.Builder(
                _ctx).setContentTitle("Scheduler").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000 })
                .setSound(alarmSound)
                .setContentText(msg);

        alamNotificationBuilder.setAutoCancel(true);
        alamNotificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        alamNotificationBuilder.setContentIntent(contentIntent);

        Random random = new Random();
        int randomNo = random.nextInt(9999 - 1000) + 1000;

        alarmNotificationManager.notify(randomNo, alamNotificationBuilder.build());
        Toast.makeText(_ctx, "Notification sent.", Toast.LENGTH_SHORT).show();
    }
}
