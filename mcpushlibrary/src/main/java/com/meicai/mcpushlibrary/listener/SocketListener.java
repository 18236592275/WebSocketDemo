package com.meicai.mcpushlibrary.listener;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketListener extends WebSocketListener {
    private static final String TAG = SocketListener.class.getSimpleName();

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.e(TAG, "--------------------onOpen:-------------------- ");
//        Message m = mStatusHandler.obtainMessage(0, Constants.ConnectionStatus.CONNECTED);
//        mStatusHandler.sendMessage(m);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.e(TAG, "--------------------onMessage:-------------------- ");
//        Message m = mMessageHandler.obtainMessage(0, text);
//        mMessageHandler.sendMessage(m);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.e(TAG, "--------------------onClosed: --------------------");
//        Message m = mStatusHandler.obtainMessage(0, Constants.ConnectionStatus.DISCONNECTED);
//        mStatusHandler.sendMessage(m);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "--------------------onFailure: --------------------");
        //  McPush.getInstance().disconnect();
    }

}
