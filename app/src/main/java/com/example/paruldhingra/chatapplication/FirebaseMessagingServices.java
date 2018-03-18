package com.example.paruldhingra.chatapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;

/**
 * Created by Parul Dhingra on 23-02-2018.
 */

public class FirebaseMessagingServices extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String click_action = remoteMessage.getNotification().getClickAction();
        String NotificationTitle = remoteMessage.getNotification().getTitle();
        String Notification_message = remoteMessage.getNotification().getBody();
        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(NotificationTitle).setContentText(Notification_message);
        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_id",from_user_id);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int notificationId = (int)System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId,mBuilder.build());
    }
}
