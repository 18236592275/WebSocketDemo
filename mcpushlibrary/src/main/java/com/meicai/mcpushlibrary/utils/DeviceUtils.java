package com.meicai.mcpushlibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class DeviceUtils {
    //获得独一无二的设备id
    private static String getDeviceId() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String getDeviceMsg(Context context, String appKey, String appSign) {
        JSONObject json = new JSONObject();
        try {
            //品牌
            json.put("brand", Build.BRAND);
            //型号
            json.put("model", android.os.Build.MODEL);
            //设备id
            json.put("device_id", DeviceUtils.getDeviceId());
            //指纹
            json.put("fingerprint", android.os.Build.FINGERPRINT);
            json.put("os", "android");
            json.put("os_version", android.os.Build.VERSION.RELEASE);
            json.put("app_version", getAppVersionName(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "register_device");
            jsonObject.put("device_info", json);
            jsonObject.put("app_key", appKey);
            jsonObject.put("sign", appSign);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (versionName == null || versionName.length() <= 0) {
            versionName = "";
        }

        return versionName;

    }
}
