package com.wy.arialena.modulecore.widget.titleBar;

import android.view.View;

/**
 *    github : https://github.com/getActivity/TitleBar
 *    desc   : 标题栏点击监听接口
 */
public interface OnTitleBarListener {

    /**
     * 左项被点击
     *
     * @param v     被点击的左项View
     */
    void onLeftClick(View v);

    /**
     * 右项被点击
     *
     * @param v     被点击的右项View
     */
    void onRightClick(View v);
}