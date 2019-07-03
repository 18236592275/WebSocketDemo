package com.meicai.mcpushlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.meicai.mcpushlibrary.service.McPushService;

public class McPush {
    private static final String TAG = McPush.class.getSimpleName();
    private static Context context;

    /**
     * 初始化过程中开启服务
     */
    public static void init(Context mContext) {
        context = mContext;
        startPush();
    }

    /**
     * 启动推送服务
     */
    private static void startPush() {
        if (hasInit()) {
            Intent intent = new Intent(context, McPushService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    /**
     * 停止推送服务
     */
    public static void stopPush() {
        if (hasInit()) {
            context.stopService(new Intent(context, McPushService.class));
        }
    }

    /**
     * 是否已经初始化
     */
    private static boolean hasInit() {
        return context != null;
    }


}
