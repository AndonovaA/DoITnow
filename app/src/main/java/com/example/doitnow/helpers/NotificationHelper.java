package com.example.doitnow.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.doitnow.App;
import com.example.doitnow.R;
import com.example.doitnow.TodoItemDetailsActivity;
import com.example.doitnow.models.TodoItem;

import java.util.Random;

public class NotificationHelper extends ContextWrapper {

    private static final String TAG = "NotificationHelper";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.doitnow" + CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("This is the description of the channel.");
        notificationChannel.setLightColor(Color.CYAN);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public void sendHighPriorityNotification(TodoItem todoItem) {

        int NOTIFICATION_ID = new Random().nextInt();

        Intent intent = new Intent(this, TodoItemDetailsActivity.class);
        intent.putExtra("TodoTitle", todoItem.getTitle());
        intent.putExtra("TodoDescription", todoItem.getDescription());
        intent.putExtra("TodoGeofenceId", todoItem.getGeofenceID());
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, App.APP_UNIQUE_NUMBER, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_logo)
                .setContentTitle(todoItem.getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(todoItem.getTitle()).bigText(todoItem.getDescription()))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }

}
