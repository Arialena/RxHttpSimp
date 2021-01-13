package com.wy.arialena.modulecore.update_app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import com.wy.arialena.modulecore.update_app.listener.ExceptionHandler;
import com.wy.arialena.modulecore.update_app.listener.ExceptionHandlerHelper;
import com.wy.arialena.modulecore.update_app.service.DownloadService;
import com.wy.arialena.modulecore.update_app.utils.AppUpdateUtils;
import com.wy.arialena.modulecore.update_app.utils.LogUtil;

import java.util.Map;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 版本更新管理器
 */
public class UpdateAppManager {


    /**
     * 可以直接利用下载功能，
     *
     * @param context          上下文
     * @param updateAppBean    下载信息配置
     * @param downloadCallback 下载回调
     */
    public static void download(final Context context, @NonNull final UpdateAppBean updateAppBean, @Nullable final DownloadService.DownloadCallback downloadCallback) {

        if (updateAppBean == null) {
            throw new NullPointerException("updateApp 不能为空");
        }

        DownloadService.bindService(context.getApplicationContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ((DownloadService.DownloadBinder) service).start(updateAppBean, downloadCallback);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
    }
}

