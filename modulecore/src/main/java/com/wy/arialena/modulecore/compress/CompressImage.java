package com.wy.arialena.modulecore.compress;

import android.net.Uri;

/**
 * @author wuyan
 */
public interface CompressImage {
    void compress();

    /**
     * 压缩结果监听器
     */
    interface CompressListener {
        void onStart();
        /**
         * 压缩成功
         *
         * @param images 已经压缩图片
         */
        void onSuccess(Uri images);


        void onError(Throwable obj);

    }
}
