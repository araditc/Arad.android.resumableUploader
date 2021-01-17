package com.araditc.uploader.utils;

public class ImageUtils {
    private static final String STORAGE = "storage";

    public static String getTypePath(String filePath) {
        if (filePath == null || filePath.equals("")) return "";
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }
}
