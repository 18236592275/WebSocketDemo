package com.meicai.websocketdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.meicai.mcpushlibrary.global.Constants;
import com.meicai.mcpushlibrary.receiver.NotificationBroadcastReceiver;
import com.meicai.mcpushlibrary.service.McPushService;
import com.meicai.mcpushlibrary.utils.NotificationPermissionUtil;
import com.meicai.mcpushlibrary.websocket.ServerConnection;
import com.ycbjie.notificationlib.NotificationUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ServerConnection mServerConnection;
    private Button btnSendMessage;
    private Button btnConnect, btnDisConnect, btnNotification, btnInterrupt;

    int mCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mServerConnection = new ServerConnection();
        btnSendMessage.setEnabled(false);
    }

    public void initView() {
        btnSendMessage = findViewById(R.id.send_message_button);
        btnConnect = findViewById(R.id.connect);
        btnDisConnect = findViewById(R.id.disconnect);
        btnNotification = findViewById(R.id.btnNotification);
        btnInterrupt = findViewById(R.id.btn_interrupt);
        btnSendMessage.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnDisConnect.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnInterrupt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_button:
                mServerConnection.sendMessage(String.valueOf(mCounter++));
                break;
            case R.id.connect:
                mServerConnection.init(this);
                mServerConnection.connect();
                break;
            case R.id.disconnect:
                mServerConnection.disconnect();
                break;
            case R.id.btnNotification:

                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA, "EXTRA");
                bundle.putString(Constants.TITLE, "TITLE");
                bundle.putString(Constants.MESSAGE, "MESSAGE");
                bundle.putString(Constants.CONTENT_TYPE, "CONTENT_TYPE");
                bundle.putString(Constants.APPKEY, "APPKEY");
                bundle.putString(Constants.MSG_ID, "15");

                Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
                intentClick.setAction(Constants.NOTIFICATION_CLICKED);
                intentClick.putExtras(bundle);
                PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);

                NotificationUtils notificationUtils = new NotificationUtils(this);
                if (NotificationPermissionUtil.isPermissionOpen(this)) {
                    notificationUtils.
                            setContentIntent(pendingIntentClick).
                            sendNotification(15, "我是title", "我是message", com.meicai.mcpushlibrary.R.mipmap.ic_launcher);
                } else {
                    Toast.makeText(notificationUtils, "请打开通知栏权限", Toast.LENGTH_SHORT).show();
                    NotificationPermissionUtil.openPermissionSetting(this);
                }
                break;
            case R.id.btn_interrupt:
                Intent intent = new Intent(this, McPushService.class);
                stopService(intent);
                break;
            default:
                break;
        }
    }
}
