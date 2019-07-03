package com.meicai.mcpushlibrary.global;

public class Constants {
    public static final String SERVER_URL = "ws://192.168.54.122:2277";//推送服务器地址
    public static final long HEART_BEAT_RATE = 5 * 1000;
    public static final int SERVICE_START_DELAYED = 5 * 1000;//websocket连接失败5秒重新连接
    public static final String EXTRA = "cn.mcpush.android.EXTRA";
    public static final String TITLE = "cn.jpush.android.TITLE";
    public static final String MESSAGE = "cn.mcpush.android.MESSAGE";
    public static final String CONTENT_TYPE = "cn.mcpush.android.CONTENT_TYPE";
    public static final String APPKEY = "cn.mcpush.android.APPKEY";
    public static final String MSG_ID = "cn.mcpush.android.MSG_ID";
    public static final String NOTIFICATION_CLICKED = "notification_clicked";
    public static final int CODE_INTENT = 0x01000000;

    public static final String SDK_NAME = "MCPUSH";
    public static final String TAG = SDK_NAME + "::";

    public enum ConnectionStatus {
        DISCONNECTED,
        CONNECTED
    }
}
