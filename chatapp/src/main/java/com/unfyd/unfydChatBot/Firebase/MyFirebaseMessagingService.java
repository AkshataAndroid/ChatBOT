package com.unfyd.unfydChatBot.Firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.unfyd.unfydChatBot.Activity.MainActivity;
import com.unfyd.unfydChatBot.R;

import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public String title, body,imageurl;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //check for the data/notification entry from the payload
        if (remoteMessage != null) {
            Map<String, String> data = remoteMessage.getData();
            title = data.get("title");
            body = data.get("body");
            imageurl=data.get("imageurl");
        }
        sendNotification();
    }

    private void sendNotification() {
        try {
            FutureTarget<Bitmap> futureTarget = Glide.with(this)
                    .asBitmap()
                    .load(imageurl)
                    .submit();

            Bitmap bitmap = futureTarget.get();

            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
             style.bigPicture(bitmap);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "101";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

                //Configure Notification Channel
                notificationChannel.setDescription("Id managemament notification received");
                notificationChannel.enableLights(true);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);

                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.tata_logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setLargeIcon(bitmap)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_MAX);


            notificationManager.notify(1, notificationBuilder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
