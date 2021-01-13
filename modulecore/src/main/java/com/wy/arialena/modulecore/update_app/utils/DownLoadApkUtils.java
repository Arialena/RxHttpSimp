package com.wy.arialena.modulecore.update_app.utils;

import android.os.Environment;

import java.io.File;

/**
 * Date  : On 2018/9/29
 * Desc  :
 * @author wuyan
 */
public class DownLoadApkUtils {

    //文件下载地址
    public static String downloadPath = getSdcardPathOnSys() + "/Download/";

    public static String getSdcardPathOnSys() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getPath();
                return path;
            }

            File path = Environment.getExternalStorageDirectory().getParentFile();
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        if (containsAny(files[i].getPath(), "sdcard")) {
                            if (files[i].list().length > 0) {
                                return files[i].getPath();
                            } else {
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
        }

        return "/mnt/sdcard";
    }
    public static boolean containsAny(String str, String searchChars) {
        return str.contains(searchChars);
    }
}
