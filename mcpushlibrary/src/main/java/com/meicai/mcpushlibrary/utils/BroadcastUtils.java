package com.meicai.mcpushlibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.meicai.mcpushlibrary.global.Constants;

public class BroadcastUtils {
    public static void sendReceivedBroadcast(Context context, String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setAction(action);
        intent.addFlags(Constants.CODE_INTENT);
        context.sendBroadcast(intent);
    }

}
