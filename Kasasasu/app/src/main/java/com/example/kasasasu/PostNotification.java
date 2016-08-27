package com.example.kasasasu;

/**
 * Created by shunpei on 16/08/27.
 */
/**
*androidの上部に通知を行うクラス
 * サービスが行われている場合は通知される
 * サービスが止まると通知も消える
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class PostNotification {
    private Context context;
    private NotificationManager notificationManager;
    public PostNotification(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void postNotification_send() {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("かささす");
        builder.setContentText("かささす　作動中");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setTicker("new message");
        builder.setOngoing(true);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0, notification);
    }

}
