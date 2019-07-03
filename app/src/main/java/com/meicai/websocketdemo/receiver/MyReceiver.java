package com.meicai.websocketdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.meicai.mcpushlibrary.global.McPushInterface;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = MyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: ===========>我接收到了");
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        String extra = bundle.getString(McPushInterface.EXTRA_EXTRA);//通知跟自定义消息中的附加字段
        Log.e(TAG, "push action " + action + "\n\n" + extra + "\n\n" + bundle.toString() + "\n\n" + intent.toString());

        if (McPushInterface.ACTION_REGISTRATION_ID.equals(action)) { // SDK 从服务器注册所得到的注册 ID 。
            final String regId = bundle.getString(McPushInterface.ACTION_REGISTRATION_ID);
        } else if (McPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {//自定义消息

        } else if (McPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {//通知

        } else if (McPushInterface.ACTION_NOTIFICATION_CLICK_ACTION.equals(action)) {//点击了顶部通知
            Log.e(TAG, "onReceive: ===========>通知栏被点击了");
        } else {

        }
    }
}
