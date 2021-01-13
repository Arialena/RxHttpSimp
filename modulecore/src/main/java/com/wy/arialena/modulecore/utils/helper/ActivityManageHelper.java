package com.wy.arialena.modulecore.utils.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
/**
 * @author wuyan
 */
public class ActivityManageHelper {
    private static class ActivityManageHelperHolder{
        public  static   final  ActivityManageHelper INSTANCE = new ActivityManageHelper();
    }
    private ActivityManageHelper() {}
    public static ActivityManageHelper getInstance(){
        return ActivityManageHelperHolder.INSTANCE;
    }

    private Stack<WeakReference<Activity>> mActivityStack;

    /**
     * 向list中添加Activity
     * @return ActivityManageHelper
     */
    public ActivityManageHelper addActivity(Activity activity){
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }

        mActivityStack.add(new WeakReference<>(activity));
        return this;
    }


    /**
     * 结束特定的Activity
     * @return ActivityManageHelper
     */
    public ActivityManageHelper finshActivity(Activity activity) {

        if (activity != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity temp = activityReference.get();
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove();
                    continue;
                }
                if (temp == activity) {
                    it.remove();
                }
            }
            activity.finish();
        }
        return this;
    }

    /**
     *
     * @param activityClasses
     * @return ActivityManageHelper
     */
    public ActivityManageHelper finshActivities(Class<? extends Activity>... activityClasses) {
        for(Class<?>  cls :activityClasses){
            if (mActivityStack != null && cls!=null) {
                // 使用迭代器进行安全删除
                for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                    WeakReference<Activity> activityReference = it.next();
                    Activity activity = activityReference.get();
                    // 清理掉已经释放的activity
                    if (activity == null) {
                        it.remove();
                        continue;
                    }
                    if (activity.getClass().equals(cls)) {
                        it.remove();
                        activity.finish();
                    }
                }
            }
        }
        return this;
    }
    /**
     * 关闭指定类名的Activity
     * @return ActivityManageHelper
     */
    public void finshActivities(Class<?> cls) {
        if (mActivityStack != null && cls!=null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity activity = activityReference.get();
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove();
                    continue;
                }
                if (activity.getClass().equals(cls)) {
                    it.remove();
                    activity.finish();
                }
            }
        }
    }

    /**
     * @return ActivityManageHelper
     */
    public ActivityManageHelper finshAllActivities() {
        if (mActivityStack != null) {
            for (WeakReference<Activity> activityReference : mActivityStack) {
                Activity activity = activityReference.get();
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
            mActivityStack.clear();
        }
        return this;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity(){
        checkWeakReference();
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            return mActivityStack.lastElement().get();
        }
        return null;
    }

    /**
     * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
     */
    private void checkWeakReference() {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity temp = activityReference.get();
                if (temp == null) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 是否包含当前activity
     */
    public boolean haveActivity(Class<? extends Activity>... activityClasses){
        if (mActivityStack != null && activityClasses!=null) {
            for (WeakReference<Activity> activityReference : mActivityStack) {
                Activity activity = activityReference.get();
                if( Arrays.asList(activityClasses).contains( activity.getClass() ) ){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (mActivityStack != null) {
            for (WeakReference<Activity> activityReference : mActivityStack) {
                Activity activity = activityReference.get();
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
            mActivityStack.clear();
            System.exit(0);//彻底退出
        }
    }

    /**
     * 保留传入界面，其他界面全部关掉
     * @param cls
     */
    public void finishOtherActivity(Class<?> cls){
        if (mActivityStack != null && cls!=null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity activity = activityReference.get();
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove();
                    continue;
                }
                if (!activity.getClass().equals(cls)) {
                    it.remove();
                    activity.finish();
                }
            }
        }
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isTextForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
                return true;
            }
        }
        return false;
    }
    /**
     * 当前类是否展示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 方法描述：判断某一应用是否正在运行
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        boolean isBackground = true;
        String processName = "empty";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                processName = appProcess.processName;
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                    isBackground = true;
                } else if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    isBackground = false;
                } else {
                    isBackground = true;
                }
            }
        }
        if (isBackground) {
           return false;
        } else {
            return true;
        }
    }
}
