package com.araditc.uploader.utils;

import android.util.Base64;


import com.araditc.uploader.AppConfig;

import java.io.UnsupportedEncodingException;

public class AppUtil {
    public static String base64Encode(String info) {
        String Authentication = info;
        byte[] data = new byte[0];
        try {
            data = Authentication.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT).replace("\n" , "");
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            AppConfig.applicationHandler.post(runnable);
        } else {
            AppConfig.applicationHandler.postDelayed(runnable, delay);
        }
    }
}
