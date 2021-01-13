package com.wy.arialena.modulecore.base;

import android.content.Context;

/**
 * @author wuyan
 */
public class ContextHolder {
    static Context ApplicationContext;
    public static void initial(Context context) {
        ApplicationContext = context;
    }
    public static Context getContext() {
        return ApplicationContext;
    }
}
