package com.meicai.mcpushlibrary.listener;

import com.meicai.mcpushlibrary.global.Constants;

public interface ServerListener {
    void onNewMessage(String message);

    void onStatusChange(Constants.ConnectionStatus status);
}
