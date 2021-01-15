package com.wy.arialena.modulecore.base.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wy.arialena.modulecore.R;
import com.wy.arialena.modulecore.base.IContractView;
import com.wy.arialena.modulecore.base.presenter.BasePresenter;
import com.wy.arialena.modulecore.utils.StatusBarUtils;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import me.jessyan.autosize.internal.CustomAdapt;


/**
 * @author wuyan
 */
public abstract class BaseActivity<P extends BasePresenter, V extends IContractView>
        extends BaseAppActivity<P, V> implements CustomAdapt {
    private ActionBar actionBar;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 提示开启权限
     *
     * @param activity
     * @param message
     */
    public static void openSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }, (dialog, which) -> {
        });
    }

    /**
     * 提示开启未知来源安装权限
     *
     * @param activity
     * @param message
     */
    public static void openInstallSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }, (dialog, which) -> {
           openInstallSettingActivity(activity,message);
        });
    }


    public static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancleListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", cancleListener)
                .create()
                .show();
    }

    /**
     * initToornar
     *
     * @param toolbarId
     * @param mActivity
     * @return
     */
    public Toolbar surportToolbar(Activity mActivity, int toolbarId, boolean showHomeAsUp) {
        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        Toolbar toolbar = mAppCompatActivity.findViewById(toolbarId);
        mAppCompatActivity.setSupportActionBar(toolbar);
        actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
        return toolbar;
    }
    /**
     *
     * @param title
     * @param showTitle
     */
    public void setToolbarTitle(String title,boolean showTitle){
        if (actionBar == null) {
            throw new RuntimeException("actionBar is null !,please involk surportToolbar to init");
        }
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(showTitle);
    }


    public void showDefaultSnackbar(String tips) {
        Snackbar mysnackbar = Snackbar.make(getRootView(), tips, Snackbar.LENGTH_SHORT);
        View view = mysnackbar.getView();
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.redColor));
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
        }
        mysnackbar.show();
    }

    protected void setStautStyleColor(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏为透明，否则在部分手机上会呈现系统默认的浅灰色
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以考虑设置为透明色
                //window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 设置状态栏颜色
     * @param s
     * @param stausColor
     */
    protected void setStausColor(String s, int stausColor){
        getWindow().setBackgroundDrawable(null);
        Window window = activity.getWindow();
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setNavigationBarColor(Color.WHITE);
                if (!TextUtils.isEmpty(s)){
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
                if (stausColor != 0){
                    window.setStatusBarColor(ContextCompat.getColor(context, stausColor));
                }else {
                    window.setStatusBarColor(stausColor);
                }
            } else {

                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
        if (stausColor == R.color.whiteColor){
            StatusBarUtils.setLightStatusBar(activity,true);
        }
    }

    /**
     * 设置状态栏颜色
     * @param stausColor
     */
    protected void setStausColor(int stausColor){
        Window window = activity.getWindow();
        initStatusBar(stausColor);
//        window.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                initStatusBar(stausColor);
//                getWindow().getDecorView().removeOnLayoutChangeListener(this);
//            }
//        });
    }



    private void initStatusBar(int statusDrawable) {
        View statusBarView = null;
        if (statusBarView == null) {
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);
        }
        if (statusBarView != null) {
            statusBarView.setBackgroundResource(statusDrawable);
        }
    }

    /**
     * 手机适配
     */
    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 667;
    }
}
