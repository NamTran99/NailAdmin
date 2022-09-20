package com.app.inails.booking.admin.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.app.inails.booking.admin.R;
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class NotificationsManager {

    private final String CHANNEL_ID_NEW_MESSAGE = "channel_new_message";
    private final String CHANNEL_ID_DEFAULT = "channel_default";

    private Context mContext;
    private final static AtomicInteger c = new AtomicInteger(0);

    public NotificationsManager(Context context) {
        mContext = context;
    }

    public int getID() {
        return c.incrementAndGet();
    }

    public void defaultNotify(FireBaseCloudMessage cloudMessage, final Class classIntent) {

        Intent intent = new Intent(mContext, classIntent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID_DEFAULT)
                .setAutoCancel(true)
                .setContentTitle(cloudMessage.getTitle())
                .setContentText(cloudMessage.getBody())
                .setLights(Color.MAGENTA, 3000, 3000)
                .setVibrate(new long[]{500, 500})
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder
                    .setColor(mContext.getResources().getColor(R.color.colorAccent))
                    .setTicker(mContext.getString(R.string.app_name));
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DEFAULT,
                    mContext.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.CYAN);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getID(), notificationBuilder.build());
    }

    /**
     * @param id~ Cancel notification
     */
    public void cancelNotification(int id) {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).cancel(id);
    }
}
