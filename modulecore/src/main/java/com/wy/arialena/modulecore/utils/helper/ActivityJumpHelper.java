package com.wy.arialena.modulecore.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author wuyan
 */
public class ActivityJumpHelper {

    public static void startActivity(Context context, Class<?> distClass) {
        try {
            Intent in = new Intent(context, distClass);
            //android 9.0 不加 FLAG_ACTIVITY_NEW_TASK 将抛出异常 https://www.jianshu.com/p/f125ccb72da0
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public static void startActivity(Context mContext, Class<?> toCls, Bundle bundle) {
        try {
            Intent intent = new Intent();
            intent.setClass(mContext, toCls);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 含有map通过class跳转界面
     * @param activity
     * @param cls
     * @param hashMap
     */
    public static void startActivity(Activity activity,
                                     Class<? extends Activity> cls,
                                     Map<String, ? extends Object> hashMap) {
        try {
            Intent intent = new Intent(activity, cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Iterator<?> iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                @SuppressWarnings("unchecked")
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
                        .next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    return;
                }
                if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                }
                if (value instanceof Boolean) {
                    intent.putExtra(key, (boolean) value);
                }
                if (value instanceof Integer) {
                    intent.putExtra(key, (int) value);
                }
                if (value instanceof Float) {
                    intent.putExtra(key, (float) value);
                }
                if (value instanceof Double) {
                    intent.putExtra(key, (double) value);
                }
                if (value instanceof Object){
                    intent.putExtra(key, (Serializable) value);
                }
            }
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
