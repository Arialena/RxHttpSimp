package com.wy.arialena.modulecore.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils {
    public static final String TAG = "Utils";

    private static int sysWidth = 0;
    private static int sysHeight = 0;


    /**
     * 获取手机的分比率，高和宽
     */
    public static void getScreen(Activity activity) {
        if (Utils.sysWidth <= 0 || Utils.sysHeight <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            Utils.sysWidth = dm.widthPixels;
            Utils.sysHeight = dm.heightPixels;
        }
    }

    public static int getSysWidth(Activity activity) {
        if (Utils.sysWidth <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            Utils.sysWidth = dm.widthPixels;
        }
        return Utils.sysWidth;
    }

    public static int getSysHeight(Activity activity) {
        if (Utils.sysHeight <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            Utils.sysHeight = dm.heightPixels;
        }
        return Utils.sysHeight;
    }


    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
