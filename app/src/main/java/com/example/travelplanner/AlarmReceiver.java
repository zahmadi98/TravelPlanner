package com.example.travelplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title  = intent.getStringExtra("title");
        String body   = intent.getStringExtra("body");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // کانال برای API 26+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("trip_alarm", "فردا سفر", NotificationManager.IMPORTANCE_HIGH );
            manager.createNotificationChannel(channel);
        }

        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        PendingIntent dismissPending = PendingIntent.getBroadcast(context, 0, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "trip_alarm")
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check_24, "متوجه شدم", dismissPending);

        manager.notify(1001, builder.build());
    }
}
