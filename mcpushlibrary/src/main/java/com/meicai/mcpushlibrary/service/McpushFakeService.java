package com.meicai.mcpushlibrary.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.meicai.mcpushlibrary.R;

public class McpushFakeService extends Service {
    private static final String TAG = McpushFakeService.class.getSimpleName();
    private static String notificationId = "channelId";
    private static String notificationName = "channelName";
    private static NotificationManager notificationManager;

    private static Notification getNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("我正在运行")
                .setContentText("我正在运行");
        //设置Notification的ChannelID,否则不能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }

    public static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onStartCommand: ==========>守护进程开启");
        startForeground(this);
        stopSelf();
    }

    public static void startForeground(Service service) {
        Log.e(TAG, "startForeground: 11111111111");
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        service.startForeground(NOTIFICATION_ID, getNotification(service));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ======》守护进程关闭");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(notificationId);
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
