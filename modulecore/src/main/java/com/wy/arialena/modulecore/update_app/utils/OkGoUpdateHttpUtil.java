package com.wy.arialena.modulecore.update_app.utils;


import com.wy.arialena.modulecore.update_app.HttpManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;

import java.io.File;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 使用OkGo实现接口
 * @author wuyan
 */
public class OkGoUpdateHttpUtil implements HttpManager {
    /**
     * 异步get
     *
     * url      get请求地址
     *  params   get参数
     *  callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        OkGo.<String>get(url).params(params).execute(new com.lzy.okgo.callback.StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                callBack.onResponse(response.body());
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<String> response) {
                super.onError(response);
                callBack.onError("异常");
            }
        });
    }

    public static void asyncMGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        OkGo.<String>get(url).params(params).execute(new com.lzy.okgo.callback.StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                callBack.onResponse(response.body());
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<String> response) {
                super.onError(response);
                callBack.onError("异常");
            }
        });
    }

    /**
     * 异步post
     *
     *  url      post请求地址
     *  params   post请求参数
     *  callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        OkGo.<String>post(url).params(params).execute(new com.lzy.okgo.callback.StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                callBack.onResponse(response.body());
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<String> response) {
                super.onError(response);
                callBack.onError("异常");
            }
        });
    }

    /**
     * 下载
     *
     *  url      下载地址
     *  path     文件保存路径
     *  fileName 文件名称
     *  callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        OkGo.<File>get(url).execute(new com.lzy.okgo.callback.FileCallback(path, fileName) {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                callback.onResponse(response.body());
            }

            @Override
            public void onStart(com.lzy.okgo.request.base.Request<File, ? extends com.lzy.okgo.request.base.Request> request) {
                super.onStart(request);
                callback.onBefore();
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<File> response) {
                super.onError(response);
                callback.onError("异常");
            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);

                callback.onProgress(progress.fraction, progress.totalSize);
            }
        });
    }
}