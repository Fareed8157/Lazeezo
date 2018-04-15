package com.example.fareed.lazeezo.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.fareed.lazeezo.R;

/**
 * Created by fareed on 14/04/2018.
 */

public class NotificationHelper extends ContextWrapper{

    private static final String lazeezo_channel_id="com.example.fareed.lazeezo.LAZEEZO";
    private static final String lazeezo_channel_name="Lazeezo";


    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ //working where api is above 26
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel lazChannel=new NotificationChannel(lazeezo_channel_id,lazeezo_channel_name,NotificationManager.IMPORTANCE_DEFAULT);
        lazChannel.enableLights(false);
        lazChannel.enableVibration(true);
        lazChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(lazChannel);
    }

    public NotificationManager getManager() {
        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getLazChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(),lazeezo_channel_id)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getLazChannelNotification(String title, String body, Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(),lazeezo_channel_id)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
