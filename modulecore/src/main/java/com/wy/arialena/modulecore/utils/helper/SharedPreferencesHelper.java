package com.wy.arialena.modulecore.utils.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.wy.arialena.modulecore.utils.AppInfoUtil;

import java.io.File;

/**
 * @author wuyan
 * 本地数据存储工具类
 */
public class SharedPreferencesHelper {
    /**
     * save stringFiled refre field to sp
     * <p>
     * 当前仅用来保存sid
     *
     * @param mContext
     * @param field
     * @param value
     */
    public static void savefieldtoSharePre(Context mContext, String field, String value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        sp.edit().putString(field, value).commit();
    }

    /**
     * get stringFiled from sp
     *
     * @param mContext
     * @param filed
     * @param defaultVulue
     * @return
     */
    public static String getValuewithField(Context mContext, String filed,String defaultVulue) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        String stringFiled = sp.getString(filed, defaultVulue);
        return stringFiled;
    }


    /**
     * save stringFiled refre field to sp
     * <p>
     * 当前仅用来保存sid
     *
     * @param mContext
     * @param field
     * @param value
     */
    public static void savefieldtoSharePre(Context mContext, String field, boolean value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        sp.edit().putBoolean(field, value).commit();
    }


    /**
     * get stringFiled from sp
     *
     * @param mContext
     * @param filed
     * @param defaultVulue
     * @return
     */
    public static boolean getBooleanValuewithField(Context mContext, String filed,boolean defaultVulue) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        Boolean stringFiled = sp.getBoolean(filed, defaultVulue);
        return stringFiled;
    }

    public static void saveLongfieldtoSharePre(Context mContext, String field, long value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        sp.edit().putLong(field, value).commit();
    }


    /**
     * get stringFiled from sp
     *
     * @param mContext
     * @param filed
     * @param defaultVulue
     * @return
     */
    public static long getLongValuewithField(Context mContext, String filed,long defaultVulue) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        long longFiled = sp.getLong(filed, defaultVulue);
        return longFiled;
    }


    public static void saveIntfieldtoSharePre(Context mContext, String field, int value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        sp.edit().putInt(field, value).commit();
    }


    /**
     * get stringFiled from sp
     *
     * @param mContext
     * @param filed
     * @param defaultVaule
     * @return
     */
    public static int getIntValuewithField(Context mContext, String filed,int defaultVaule) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        int longFiled = sp.getInt(filed, defaultVaule);
        return longFiled;
    }

    /**
     * 清除指定的KEY缓存
     * @param mContext
     * @param filed
     */
    public static void cleanSpFileld(Context mContext,String filed){
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(AppInfoUtil.getAppName(mContext), 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(filed);
        editor.commit();
    }

    /**
     * 清楚SP缓存
     * @param context
     */
    public static void cleanSpCache(Context context){
        //清空后不退出程序立即生效
        File pref_xml = new File("data/data/" + AppInfoUtil.getPackageName(context) + "/shared_prefs", "*.xml");
        if (pref_xml.exists()) {
            pref_xml.delete();
        }

        //must clear and commit
        SharedPreferences pref = context.getSharedPreferences("*", Context.MODE_PRIVATE);
        if (pref != null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
        }
    }
}
