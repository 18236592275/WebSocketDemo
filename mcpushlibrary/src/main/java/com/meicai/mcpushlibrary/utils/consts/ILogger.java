package com.meicai.mcpushlibrary.utils.consts;

import com.meicai.mcpushlibrary.global.Constants;

public interface ILogger {
    boolean isShowLog = false;
    boolean isShowStackTrace = false;
    String defaultTag = Constants.TAG;

    void showLog(boolean isShowLog);

    void showStackTrace(boolean isShowStackTrace);

    void debug(String tag, String message);

    void info(String message);

    void info(String tag, String message);

    void warning(String message);

    void warning(String tag, String message);

    void error(String message);

    void error(String tag, String message);

    void monitor(String message);

    boolean isMonitorMode();

    String getDefaultTag();
}
