package com.meicai.mcpushlibrary.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.meicai.mcpushlibrary.global.Constants;
import com.meicai.mcpushlibrary.global.McPushInterface;


public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String TYPE = "type";
    private static final String TAG = NotificationBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        String extra = bundle.getString(Constants.EXTRA);//通知跟自定义消息中的附加字段
        Log.e(TAG, "push action " + action + "\n\n" + extra + "\n\n" + bundle.toString() + "\n\n" + intent.toString());

        int type = -1;
        try {
            type = Integer.parseInt(bundle.getString(Constants.MSG_ID));
        } catch (NumberFormatException e) {
            type = -1;
            Log.e(TAG, "onReceive: NotificationBroadcastReceiver  error");
            e.printStackTrace();
        }
        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }
        if (action.equals("notification_clicked")) {
            //处理点击事件
            Toast.makeText(context, "clicked " + bundle.get(Constants.MESSAGE), Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent();
            intent1.putExtras(bundle);
            intent1.setAction(McPushInterface.ACTION_NOTIFICATION_CLICK_ACTION);
            intent1.addFlags(Constants.CODE_INTENT);
            context.sendBroadcast(intent1);
        }

//        if (action.equals("notification_cancelled")) {
//            //处理滑动清除和点击删除事件
//            // Toast.makeText(context, "cancelled", Toast.LENGTH_LONG).show();
//        }

    }

}
