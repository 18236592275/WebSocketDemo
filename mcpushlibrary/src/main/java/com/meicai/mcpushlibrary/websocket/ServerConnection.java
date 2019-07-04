package com.meicai.mcpushlibrary.websocket;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.meicai.mcpushlibrary.R;
import com.meicai.mcpushlibrary.bean.AlertBean;
import com.meicai.mcpushlibrary.bean.PushBean;
import com.meicai.mcpushlibrary.global.Constants;
import com.meicai.mcpushlibrary.global.McPushInterface;
import com.meicai.mcpushlibrary.receiver.NotificationBroadcastReceiver;
import com.meicai.mcpushlibrary.utils.BroadcastUtils;
import com.meicai.mcpushlibrary.utils.DefaultLogger;
import com.meicai.mcpushlibrary.utils.DeviceUtils;
import com.meicai.mcpushlibrary.utils.NetWorkUtils;
import com.meicai.mcpushlibrary.utils.SharedPreferencesUtil;
import com.meicai.mcpushlibrary.utils.consts.ILogger;
import com.ycbjie.notificationlib.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * websocket连接
 */
public class ServerConnection {
    private static final String TAG = ServerConnection.class.getSimpleName();
    private static ILogger logger = new DefaultLogger(Constants.TAG);
    private WebSocket mWebSocket;
    private OkHttpClient mClient;
    private static Context context;
    // 发送心跳包
    private Handler mHeartBeatHandler = new Handler();
    //延时5秒重新连接
    private Handler handlerConnect = new Handler();
    //appkey 和sign是在Mainfest中配置的
    private String appkey;
    private String appSign;
    private String registrationId;//服务器返回的用户唯一id
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (NetWorkUtils.hasNetwork(context)) { //检测是否有网络，如果是因为网络原因导致连接失败，则在有网络的时候再次重试连接
                connect();
            }
        }
    };

    public void init(Context mContext) {
        context = mContext;
        logger.showLog(true);
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            appkey = appInfo.metaData.getString("com.mc.push.api_key");
            appSign = appInfo.metaData.getString("com.mc.push.api_sign");
        } catch (PackageManager.NameNotFoundException e) {
            logger.error("init: =======>请在AndroidMainfest中填写appkey,和APPsign");
            e.printStackTrace();
        }
    }

    public ServerConnection() {
        mClient = new OkHttpClient.Builder()
                .readTimeout(3000, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3000, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3000, TimeUnit.SECONDS)//设置连接超时时间
                .build();
    }

    // 发送心跳包
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            //发送心跳包
            sendHeartData();
            mHeartBeatHandler.postDelayed(this, Constants.HEART_BEAT_RATE); //每隔一定的时间，对长连接进行一次心跳检测
        }
    };

    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            logger.warning("------------------onOpen:------------------ " + response.message());
            sendDeviceMsgToGetId();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            String msg = new String(Base64.decode(text.getBytes(), Base64.DEFAULT));
            System.out.println("------------------onMessage:------------------ " + text);
            System.out.println("====>解析后" + msg);

            PushBean pushBean = new Gson().fromJson(msg, PushBean.class);
            Bundle bundle = new Bundle();
//            bundle.putString(Constants.EXTRA, "EXTRA");

            switch (pushBean.type) {
                case "push_message"://自定义透传消息
                    bundle.putString(Constants.MSG_ID, pushBean.message_id);
                    bundle.putString(Constants.MESSAGE, pushBean.data.content);
                    BroadcastUtils.sendReceivedBroadcast(context, McPushInterface.ACTION_MESSAGE_RECEIVED, bundle);
                    //通知服务器用户收到消息
                    sendMessageCallback(pushBean.message_id, "message");
                    break;
                case "push_notice"://通知消息
                    bundle.putString(Constants.MSG_ID, pushBean.message_id);
                    bundle.putString(Constants.TITLE, pushBean.data.alert.title);
                    bundle.putString(Constants.MESSAGE, pushBean.data.alert.content);
                    BroadcastUtils.sendReceivedBroadcast(context, McPushInterface.ACTION_NOTIFICATION_RECEIVED, bundle);
                    showNotification(pushBean, bundle);
                    //通知服务器用户收到消息
                    sendMessageCallback(pushBean.message_id, "notice");
                    break;
                case "push_registration_id"://注册id
                    registrationId = pushBean.push_registration_id;
                    //开启心跳
                    mHeartBeatHandler.postDelayed(heartBeatRunnable, Constants.HEART_BEAT_RATE);
                    //存储唯一id
                    SharedPreferencesUtil.putString(context, McPushInterface.ACTION_REGISTRATION_ID, pushBean.push_registration_id);
                    //通知用户已获得唯一id
                    BroadcastUtils.sendReceivedBroadcast(context, McPushInterface.ACTION_REGISTRATION_ID, bundle);
                    break;
                case "link":
                    logger.info("======>link");
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            logger.info("------------------onClosed:------------------ " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            logger.error("------------------onFailure:------------------ " + t.toString());
            disconnect();
            handlerConnect.postDelayed(runnable, Constants.SERVICE_START_DELAYED);
        }
    }

    /**
     * 展示顶部通知
     */
    private void showNotification(PushBean pushBean, Bundle bundle) {
        AlertBean alertBean = pushBean.data.alert;
        Intent intentClick = new Intent(context, NotificationBroadcastReceiver.class);
        intentClick.setAction(Constants.NOTIFICATION_CLICKED);
        intentClick.putExtras(bundle);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);

        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.
                setContentIntent(pendingIntentClick).
                sendNotification(Integer.parseInt(pushBean.message_id), alertBean.title, alertBean.content, R.mipmap.ic_launcher);
    }


    public void connect() {
        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .build();
        if (mClient == null) {
            logger.error("-----------------请先初始化MCPUSH-----------------");
            return;
        }
        mWebSocket = mClient.newWebSocket(request, new SocketListener());
    }

    /**
     * 断开websocket,断开后停止心跳，但是失败后还会重新连接
     */
    public void disconnect() {
        try {
            mWebSocket.cancel();
        } catch (Exception e) {
            Log.e(TAG, "disconnect:  mWebSocket.cancel() error");
            e.printStackTrace();
        }
        //停止心跳
        mHeartBeatHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 断开心跳和连接
     */
    public void cancel() {
        disconnect();
        handlerConnect.removeCallbacksAndMessages(null);
    }


    /**
     * 自建推送 / 上报-消息触达
     */
    private void sendMessageCallback(String msgId, String msgType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "message_callback");
            jsonObject.put("push_registration_id", registrationId);
            jsonObject.put("message_id", msgId);
            jsonObject.put("push_type", msgType);
            jsonObject.put("app_key", appkey);
            jsonObject.put("sign", appSign);
            jsonObject.put("push_channel", "mjt_push");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage(jsonObject.toString());
    }

    /**
     * 自建推送 / 上报-心跳
     */
    private void sendHeartData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "ping");
            jsonObject.put("push_registration_id", registrationId);
            jsonObject.put("app_key", appkey);
            jsonObject.put("sign", appSign);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        logger.error("sendData: =========>" + jsonObject.toString());
        sendMessage(jsonObject.toString());
    }

    /**
     * 自建推送 / 上报-注册设备获取唯一ID
     */
    private void sendDeviceMsgToGetId() {
        sendMessage(DeviceUtils.getDeviceMsg(context, appkey, appSign));
    }

    /**
     * 发送消息
     */
    public void sendMessage(String message) {
        String msg = Base64.encodeToString(message.getBytes(), Base64.DEFAULT);
        mWebSocket.send(msg);
    }
}
