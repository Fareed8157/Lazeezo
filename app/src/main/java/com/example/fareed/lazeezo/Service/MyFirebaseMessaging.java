package com.example.fareed.lazeezo.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.fareed.lazeezo.Cart;
import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Helper.NotificationHelper;
import com.example.fareed.lazeezo.MainActivity;
import com.example.fareed.lazeezo.OrderStatus;
import com.example.fareed.lazeezo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

/**
 * Created by fareed on 2/6/2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData()!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationAPI26(remoteMessage);
            else
                sendNotification(remoteMessage);
        }
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        Map<String,String> data=remoteMessage.getData();
        String title=data.get("title");
        String message=data.get("message");

        PendingIntent pendingIntent;
        NotificationHelper helper;
        Notification.Builder builder;



        if(Common.currentUser!=null) {
            Intent intent = new Intent(this, OrderStatus.class);
            intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper = new NotificationHelper(this);
            builder = helper.getLazChannelNotification(title, message, pendingIntent, notificationSound);

            helper.getManager().notify(new Random().nextInt(), builder.build());
        }else{
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getLazChannelNotification(title, message, notificationSound);

            helper.getManager().notify(new Random().nextInt(), builder.build());
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String,String> data=remoteMessage.getData();
        String title=data.get("title");
        String message=data.get("message");

        // RemoteMessage.Notification notification=remoteMessage.getNotification();
        Intent intent=new Intent(this, OrderStatus.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());
    }
//    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
//        RemoteMessage.Notification notification=remoteMessage.getNotification();
//        String title=notification.getTitle();
//        String content=notification.getBody();
//
//        Intent intent=new Intent(this, OrderStatus.class);
//        intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_ONE_SHOT);
//        Uri notificationSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationHelper helper=new NotificationHelper(this);
//        Notification.Builder builder=helper.getLazChannelNotification(title,content,pendingIntent,notificationSound);
//
//        helper.getManager().notify(new Random().nextInt(),builder.build());
//
//    }
//
//    private void sendNotification(RemoteMessage remoteMessage) {
//        RemoteMessage.Notification notification=remoteMessage.getNotification();
//        Intent intent=new Intent(this, OrderStatus.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_ONE_SHOT);
//
//        Uri notificationSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(notification.getTitle())
//                .setContentText(notification.getBody())
//                .setAutoCancel(true)
//                .setSound(notificationSound)
//                .setContentIntent(pendingIntent);
//        NotificationManager noti=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        noti.notify(0,builder.build());
//    }
}
