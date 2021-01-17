package com.araditc.uploader.notificaiton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.araditc.uploader.AppConfig;
import com.araditc.uploader.R;

public class NotificationBuilder {
    public static final int NOTIFY_ID = 1002;
    public static final int REQUEST_CODE = 1003;
    public static String ID = "21458752";
    public static String FORGROUND_ID = "51423987";

    public enum STATUS {
        START_TYPE,
        STOP_TYPE
    }

    public static NotificationCompat.Builder buildForegroundNotification(@NonNull STATUS action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        String title = "";

        switch (action) {
            case STOP_TYPE:
                title = AppConfig.application.getString(R.string.fg_notitifcation_title_starting);
                break;
            case START_TYPE:
                title = AppConfig.application.getString(R.string.fg_notitifcation_title_starting);
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AppConfig.application, FORGROUND_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title);

        return builder;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(FORGROUND_ID, AppConfig.newInstance().getApplicationName(), NotificationManager.IMPORTANCE_LOW);
        chan.setLightColor(Color.RED);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) AppConfig.newInstance().application.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

}
