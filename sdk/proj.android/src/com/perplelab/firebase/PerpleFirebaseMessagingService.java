package com.perplelab.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.perplelab.PerpleSDK;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

public class PerpleFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = "PerpleSDK FirebaseMessagingService";
    private static final int REQUEST_CODE = 1000;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "onMessageReceived - from:" + remoteMessage.getFrom() + ", message:" + remoteMessage.getNotification().getBody());
        }

        if (PerpleSDK.IsReceivePushOnForeground) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onMessageSent(String msgId) {
        if (PerpleSDK.IsDebug) {
            Log.d(LOG_TAG, "onMessageSent - msgId:" + msgId);
        }

        PerpleSDK.onMessageSent(msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        Log.e(LOG_TAG, "onSendError - msgId:" + msgId + ", exception:" + exception.toString());
        PerpleSDK.onSendError(msgId, exception);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Activity mainActivity = PerpleSDK.MainActivity;

        if (mainActivity == null) {
            Log.e(LOG_TAG, "MainActivity for FCM notification is null.");
            return;
        }

        int pushIcon = 0;
        String appName = "";
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(mainActivity.getPackageName(), PackageManager.GET_META_DATA);
            pushIcon = info.icon;
            appName = getString(info.labelRes);
            Bundle bundle = info.metaData;
            if (bundle != null) {
                int icon = bundle.getInt("push_icon");
                if (icon != 0) {
                    pushIcon = icon;
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (messageTitle == null || messageTitle.isEmpty()) {
            messageTitle = appName;
        }

        Intent intent = new Intent(this, mainActivity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(pushIcon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setTicker(messageBody)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE |
                             NotificationCompat.DEFAULT_LIGHTS |
                             NotificationCompat.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
