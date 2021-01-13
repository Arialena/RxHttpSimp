package com.wy.arialena.modulecore.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.wy.arialena.modulecore.receiver.NetWorkStateReceiver;
import com.wy.arialena.modulecore.utils.MyActivityManager;

import androidx.multidex.MultiDex;

/**
 * @author wuyan
 */
public class BaseApplication extends Application {
    private static Application context;
    private static BaseApplication mInst;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mInst = this;
        ContextHolder.initial(this);

        NetWorkStateReceiver.getInstance().init(this,false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    public void registerActivityLifecy(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override public void onActivityStarted(Activity activity) {

            }

            @Override public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override public void onActivityPaused(Activity activity) {

            }

            @Override public void onActivityStopped(Activity activity) {

            }

            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static  Application getContext(){
        return context;
    }
    public static BaseApplication getInst() { return mInst; }
}
