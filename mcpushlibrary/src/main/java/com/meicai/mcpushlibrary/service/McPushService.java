package com.meicai.mcpushlibrary.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.meicai.mcpushlibrary.websocket.ServerConnection;

/**
 * 建立连接服务
 */
public class McPushService extends Service {
    private static final String TAG = McPushService.class.getSimpleName();
    private ServerConnection mServerConnection;
    private int SERVICE_START_DELAYED = 5;
    public static final int NOTIFICATION_ID = 1001;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServerConnection = new ServerConnection();
        Intent intent = new Intent(this, McpushFakeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        Log.e(TAG, "McPushService onCreate: ==========>");
        cancelAutoStartService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServerConnection.init(this);
        mServerConnection.connect();
        McpushFakeService.startForeground(this);
        Log.e(TAG, "McPushService onStartCommand: ============>");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mServerConnection.disconnect();
        stopForeground(true);
        Log.e(TAG, "McPushService onDestroy: ==============>");
        startServiceAfterClosed(this, SERVICE_START_DELAYED);
    }

    //-------------------------为了防止service被杀死，杀死后再次重启服务-------------------------
    public static void cancelAutoStartService(Context context) {
        AlarmManager alarm = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(getOperation(context));
    }

    private static PendingIntent getOperation(Context context) {
        Intent intent = new Intent(context, McPushService.class);
        PendingIntent operation = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return operation;
    }

    /**
     * service停掉后自动启动应用
     *
     * @param context
     * @param delayed 延后启动的时间，单位为秒
     */
    private static void startServiceAfterClosed(Context context, int delayed) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayed * 1000, getOperation(context));
    }

}
